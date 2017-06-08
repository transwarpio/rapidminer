package com.rapidminer.license.internal;

/**
 * Created by mk on 3/9/16.
 */

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.license.*;
import com.rapidminer.license.location.LicenseLoadingException;
import com.rapidminer.license.location.LicenseLocation;
import com.rapidminer.license.location.LicenseStoringException;
import com.rapidminer.license.product.Constraint;
import com.rapidminer.license.product.Product;
import com.rapidminer.license.utils.Base;
import com.rapidminer.license.utils.LicenseUtilities;
import com.rapidminer.license.utils.Pair;
import com.rapidminer.license.violation.LicenseConstraintViolation;
import com.rapidminer.license.violation.LicenseViolation;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DefaultLicenseManager implements LicenseManager {
    private static final String PUBLIC_KEY = "H4sIAAAAAAAAAAEmAdn+MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA58/mQ8VjKWDj9ai3mTzFX0b2S0VbV7LIQFv97U8ePdFoLu/cAcTvw7jsvQAT/3RHS7kzXXOk4OGDb7rmL85Dw6nfDs1jFA1auvrICW2vvOdpLrOOijJX5S5EJWHxKoBXSOfxU/fKFa93iuSVKJdqXJeah2Lgs/wq54BBcp4SrxogwWiuqFImqDo7BZKAZgLSm/v2IlICxKGM9QgAoYYLL/bongBpp6SxTy1gm/YD108jJxEk5wuFefDPDMlP0kioSsmGonU6o++pqYLuLkbFdNOdbmtoTphzP5vNaLaTQBmw9vuFHqh80BmIEQi6pK/Wz2RjOU6CYDpn9wv1Lgo2JQIDAQABbOI6ryYBAAA=";
    private static final String STARTER_LICENSE_ID = "starter-edition";
    public static final String RAPIDMINER_PRODUCT_ID_PREFIX = "rapidminer-";
    static final byte[] DEF = new byte[]{(byte)24, (byte)4, (byte)124, (byte)10, (byte)91};
    protected static final int KEY_LEN = 62;
    private static final byte[][] PARAMS = new byte[][]{{(byte)24, (byte)4, (byte)127}, {(byte)10, (byte)0, (byte)56}, {(byte)1, (byte)2, (byte)91}, {(byte)7, (byte)1, (byte)100}};
    private static final String SHA1_WITH_RSA = "SHA1withRSA";
    private LicenseVersion currentVersion;
    private ObjectMapper jsonObjectMapper;
    private PublicKey publicKey;
    private List<Product> registeredProducts;
    private Map<Product, List<License>> loadedLicenses;
    private Map<Product, License> productToActiveLicenseMap;
    private Map<Product, License> productToStarterLicenseMap;
    private LicenseLocation licenseLocation;
    private List<LicenseManagerListener> licenseManagerListeners;
    private LicenseAnnotationValidator licenseAnnotationValidator;

    public DefaultLicenseManager() {
        this(null);
    }

    public DefaultLicenseManager(String publicKey) {
        this.currentVersion = DefaultLicenseManager.LicenseVersion.PRODUCT_SIGNATURE_VERSION;
        this.registeredProducts = new LinkedList();
        this.loadedLicenses = new HashMap();
        this.productToActiveLicenseMap = new HashMap();
        this.productToStarterLicenseMap = new HashMap();
        this.licenseManagerListeners = new LinkedList();

        try {
            X509EncodedKeySpec e = new X509EncodedKeySpec(Base.decode(publicKey == null?"H4sIAAAAAAAAAAEmAdn+MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA58/mQ8VjKWDj9ai3mTzFX0b2S0VbV7LIQFv97U8ePdFoLu/cAcTvw7jsvQAT/3RHS7kzXXOk4OGDb7rmL85Dw6nfDs1jFA1auvrICW2vvOdpLrOOijJX5S5EJWHxKoBXSOfxU/fKFa93iuSVKJdqXJeah2Lgs/wq54BBcp4SrxogwWiuqFImqDo7BZKAZgLSm/v2IlICxKGM9QgAoYYLL/bongBpp6SxTy1gm/YD108jJxEk5wuFefDPDMlP0kioSsmGonU6o++pqYLuLkbFdNOdbmtoTphzP5vNaLaTQBmw9vuFHqh80BmIEQi6pK/Wz2RjOU6CYDpn9wv1Lgo2JQIDAQABbOI6ryYBAAA=":publicKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.publicKey = kf.generatePublic(e);
        } catch (InvalidKeySpecException | IOException | NoSuchAlgorithmException var4) {
            throw new IllegalStateException("PublicKey could not be initialized.", var4);
        }

        this.jsonObjectMapper = new ObjectMapper();
        this.licenseAnnotationValidator = new LicenseAnnotationValidator(this);
        this.jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public final void registerProduct(Product newProduct) throws AlreadyRegisteredException, LicenseLoadingException, InvalidProductException {
        if(newProduct == null) {
            throw new IllegalArgumentException("newProduct must not be null!");
        } else {
            try {
                LicenseStatus e = this.verifyProduct(newProduct);
                if(e == LicenseStatus.SIGNATURE_INVALID) {
                    throw new InvalidProductException("Signature verification failed for product!", newProduct.getProductId());
                }

                if(e == LicenseStatus.WRONG_PRODUCT_ID) {
                    throw new InvalidProductException("Only RapidMiner products are allowed to use product IDs starting with rapidminer-", newProduct.getProductId());
                }
            } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException var8) {
                throw new InvalidProductException("Signature verification failed for product.", newProduct.getProductId(), var8);
            }

            synchronized(this.registeredProducts) {
                String productId = newProduct.getProductId();
                Iterator var4 = this.registeredProducts.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        this.registeredProducts.add(newProduct);
                        break;
                    }

                    Product product = (Product)var4.next();
                    if(productId.equals(product.getProductId())) {
                        throw new AlreadyRegisteredException(productId);
                    }
                }
            }

            this.reloadLicenses(newProduct);
        }
    }

    public final void reloadLicenses(Product product) throws LicenseLoadingException {
        synchronized(this.loadedLicenses) {
            this.loadedLicenses.remove(product);
            if(this.licenseLocation == null) {
                throw new LicenseLoadingException("Cannot load licenses. No license location specified.");
            } else {
                List licenseStrings = this.licenseLocation.loadLicenses(product.getProductId());
                Iterator var4 = licenseStrings.iterator();

                while(var4.hasNext()) {
                    String licenseString = (String)var4.next();

                    try {
                        this.addLicense(product, licenseString);
                    } catch (LicenseValidationException var8) {
                        Logger.getLogger(DefaultLicenseManager.class.getCanonicalName()).log(Level.WARNING, "Could not add license for product \'" + product.getProductId() + "\'.", var8);
                    }
                }

                this.setActiveLicense(product, this.getBestValidLicense(product));
            }
        }
    }

    private final List<License> getLicensesForProduct(Product product) {
        synchronized(this.loadedLicenses) {
            List list = (List)this.loadedLicenses.get(product);
            if(list == null) {
                list = new LinkedList();
                this.loadedLicenses.put(product, list);
            }

            return list;
        }
    }

    public final List<License> getLicenses(Product product) {
        List licensesForProduct = this.getLicensesForProduct(product);
        ArrayList clonedLicenses = new ArrayList(licensesForProduct.size());
        Iterator var4 = licensesForProduct.iterator();

        while(var4.hasNext()) {
            License lic = (License)var4.next();
            clonedLicenses.add(lic.copy());
        }

        return Collections.unmodifiableList(clonedLicenses);
    }

    private final Pair<Product, License> addLicense(String licenseString) throws UnknownProductException, LicenseValidationException {
        Pair productAndLicense = this.validateLicense(null, licenseString);

        this.getLicensesForProduct((Product)productAndLicense.getFirst())
                .add((License)productAndLicense.getSecond());
        return productAndLicense;
    }

    private final License addLicense(Product product, String licenseString) throws LicenseValidationException {
        Pair productAndLicense;
        try {
            productAndLicense = this.validateLicense(product, licenseString);
        } catch (UnknownProductException var5) {
            throw new AssertionError("Unknown product when adding license for known product. This should not happen", var5);
        }

        this.getLicensesForProduct(product).add((License)productAndLicense.getSecond());
        return (License)productAndLicense.getSecond();
    }

    public final License storeNewLicense(String licenseText) throws LicenseStoringException, UnknownProductException, LicenseValidationException {
        Pair productLicensePair = this.addLicense(licenseText);
        Product product = (Product)productLicensePair.getFirst();
        License license = (License)productLicensePair.getSecond();
        if(this.licenseLocation == null) {
            throw new LicenseStoringException("License location has not been set yet. Cannot store licenses.");
        } else {
            String versionString = null;
            if(license.getVersions() != null && !license.getVersions().isEmpty()) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;

                String version;
                for(Iterator var8 = license.getVersions().iterator(); var8.hasNext(); builder.append(version)) {
                    version = (String)var8.next();
                    if(first) {
                        first = false;
                    } else {
                        builder.append("-");
                    }
                }

                versionString = builder.toString();
            }

            this.licenseLocation.storeLicense(product.getProductId(), versionString, license.getProductEdition(), license.getStartDate(), license.getExpirationDate(), licenseText);
            this.handleLicenseEvent(new LicenseEvent(license, false));
            this.setActiveLicense(product, this.getBestValidLicense(product));
            return license;
        }
    }

    private final Product getProductForLicense(LicenseContent licenseContent) throws UnknownProductException {
        Iterator var2 = this.registeredProducts.iterator();

        Product product;
        String licenseProductId;
        String productId;
        do {
            if(!var2.hasNext()) {
                throw new UnknownProductException(licenseContent.getProductId());
            }

            product = (Product)var2.next();
            licenseProductId = licenseContent.getProductId();
            productId = product.getProductId();
        } while(!licenseProductId.equals(productId));

        return product;
    }

    private final License getBestValidLicense(Product product) {
        LinkedList licensesForProduct = new LinkedList(this.getLicensesForProduct(product));
        License nextActiveLicense = null;
        Iterator var4 = licensesForProduct.iterator();

        while(true) {
            License license;
            do {
                do {
                    if(!var4.hasNext()) {
                        if(nextActiveLicense == null) {
                            return this.getStarterLicense(product, !licensesForProduct.isEmpty()?(License)licensesForProduct.get(0):null);
                        }

                        return nextActiveLicense;
                    }

                    license = (License)var4.next();
                } while(license.getStatus() != LicenseStatus.VALID && license.getStatus() != LicenseStatus.STARTS_IN_FUTURE);
            } while(license.getStatus() == LicenseStatus.STARTS_IN_FUTURE && license.validate(this.getNow()) != LicenseStatus.VALID);

            if(nextActiveLicense == null) {
                nextActiveLicense = license;
            } else if(license.getPrecedence() > nextActiveLicense.getPrecedence()) {
                nextActiveLicense = license;
            } else if(license.getPrecedence() == nextActiveLicense.getPrecedence()) {
                boolean currentDoesExpire = license.getExpirationDate() != null;
                if(currentDoesExpire) {
                    if(nextActiveLicense.getExpirationDate() != null && license.getExpirationDate().after(nextActiveLicense.getExpirationDate())) {
                        nextActiveLicense = license;
                    }
                } else {
                    nextActiveLicense = license;
                }
            }
        }
    }

    private final void setActiveLicense(Product product, License lic) {
        if(this.productToActiveLicenseMap.get(product) != lic) {
            this.productToActiveLicenseMap.put(product, lic);
            this.handleLicenseEvent(new LicenseEvent(lic, true));
        }

    }

    public final License getActiveLicense(Product product) {
        return this.getDefaultActiveLicense(product);
    }

    private final License getDefaultActiveLicense(Product product) {
        synchronized(this.productToActiveLicenseMap) {
            License validatedLicense = this.productToActiveLicenseMap.get(product);
            if(validatedLicense != null) {
                LicenseStatus oldStatus = validatedLicense.getStatus();
                LicenseStatus newStatus = validatedLicense.validate(this.getNow());
                if(oldStatus == LicenseStatus.VALID && newStatus == LicenseStatus.EXPIRED) {
                    License nextLicense = this.getBestValidLicense(product);
                    this.handleLicenseEvent(new LicenseEvent(validatedLicense.copy(), nextLicense.copy()));
                    this.setActiveLicense(product, nextLicense);
                    validatedLicense = nextLicense;
                }
            } else {
                validatedLicense = this.getBestValidLicense(product);
                this.setActiveLicense(product, validatedLicense);
            }

            return validatedLicense.copy();
        }
    }

    private License createStarterLicense(Product product, License expiredLicense) {
        DefaultConstraints constraints = new DefaultConstraints();
        Iterator expiredUser = product.getConstraints().iterator();

        while(expiredUser.hasNext()) {
            Constraint content = (Constraint)expiredUser.next();
            Object defaultValue = content.getDefaultValue();
            constraints.addConstraint(content, defaultValue);
        }

        Object expiredUser1 = new DefaultLicenseUser(null, null);
        if(expiredLicense != null) {
            expiredUser1 = expiredLicense.getLicenseUser().copy();
        }

        LicenseContent content1 = new LicenseContent(product.getProductId(), Arrays.asList(new String[]{product.getProductVersion()}), "starter", 10, (String)null, (Date)null, (Date)null, (LicenseUser)expiredUser1, constraints, expiredLicense == null?"starter-edition":expiredLicense.getLicenseID(), (String)null);
        return new DefaultLicense(LicenseStatus.VALID, content1);
    }

    private License getStarterLicense(Product product, License expiredLicense) {
        License starterLicense = this.productToStarterLicenseMap.get(product);
        if(starterLicense == null || starterLicense.getLicenseUser().getName() == null) {
            starterLicense = this.createStarterLicense(product, expiredLicense);
            this.productToStarterLicenseMap.put(product, starterLicense);
        }

        return starterLicense;
    }

    public final License getUpcomingLicense(Product product) {
        List licensesForProduct = this.getLicensesForProduct(product);
        License activeLicense = this.getDefaultActiveLicense(product);
        License upcomingLicense = null;
        Iterator var5 = licensesForProduct.iterator();

        while(true) {
            while(true) {
                License inspectedLicense;
                do {
                    do {
                        do {
                            if(!var5.hasNext()) {
                                if(upcomingLicense == null) {
                                    return this.getStarterLicense(product, activeLicense);
                                }

                                return upcomingLicense.copy();
                            }

                            inspectedLicense = (License)var5.next();
                        } while(inspectedLicense.equals(activeLicense));
                    } while(!this.isCorrectLicenseStatus(inspectedLicense));
                } while(!activeLicense.isStarterLicense() && !this.isValidAfterExpiration(inspectedLicense, activeLicense) && !this.isValidBeforeActiveExpirationAndHigherPrecedence(inspectedLicense, activeLicense));

                if(upcomingLicense == null) {
                    upcomingLicense = inspectedLicense;
                } else {
                    Date inspectedStartDate = inspectedLicense.getStartDate();
                    Date upcomingStartDate = upcomingLicense.getStartDate();
                    boolean bothNull = upcomingStartDate == null && inspectedStartDate == null;
                    boolean sameStartDate = bothNull || inspectedStartDate.equals(upcomingStartDate);
                    if(sameStartDate && this.isBetter(inspectedLicense, upcomingLicense)) {
                        upcomingLicense = inspectedLicense;
                    } else if(this.isStartDateSet(upcomingLicense) && inspectedStartDate.before(upcomingStartDate)) {
                        upcomingLicense = inspectedLicense;
                    }
                }
            }
        }
    }

    private boolean isCorrectLicenseStatus(License inspectedLicense) {
        return inspectedLicense.getStatus() == LicenseStatus.VALID || inspectedLicense.getStatus() == LicenseStatus.STARTS_IN_FUTURE;
    }

    private boolean isValidBeforeActiveExpirationAndHigherPrecedence(License lic1, License lic2) {
        return this.isBetter(lic1, lic2) && this.isValidBeforeExpiration(lic1, lic2);
    }

    private boolean isValidBeforeExpiration(License lic1, License lic2) {
        if(this.isExpiring(lic2)) {
            if(!this.isStartDateSet(lic1)) {
                return true;
            } else {
                Date expirationDate = lic2.getExpirationDate();
                Date startDate = lic1.getStartDate();
                return expirationDate.after(startDate) || expirationDate.equals(startDate);
            }
        } else {
            return false;
        }
    }

    private boolean isBetter(License lic1, License lic2) {
        return lic1.compareTo(lic2) > 0;
    }

    private boolean isExpiring(License lic) {
        return lic.getExpirationDate() != null;
    }

    private boolean isStartDateSet(License lic) {
        return lic.getStartDate() != null;
    }

    private boolean isValidAfterExpiration(License lic1, License lic2) {
        if(!this.isExpiring(lic2)) {
            return false;
        } else {
            Date secondExpDate = lic2.getExpirationDate();
            boolean willStart = false;
            if(this.isStartDateSet(lic1)) {
                Date validAfterExpiration = lic1.getStartDate();
                willStart = secondExpDate.before(validAfterExpiration) || secondExpDate.equals(validAfterExpiration);
            } else {
                willStart = true;
            }

            boolean validAfterExpiration1 = false;
            if(this.isExpiring(lic1)) {
                Date firstExpDate = lic1.getExpirationDate();
                validAfterExpiration1 = secondExpDate.before(firstExpDate);
            } else {
                validAfterExpiration1 = true;
            }

            return willStart || validAfterExpiration1;
        }
    }

    public final Pair<Product, License> validateLicense(Product product, String licenseText) throws LicenseValidationException, UnknownProductException {
        Product licenseProduct = product;

        SignedLicense signedLicense;
        LicenseContent licenseContent;
        try {
            String decodedSignedLicense = new String(Base.decode(licenseText), StandardCharsets.UTF_8);
            signedLicense = this.jsonObjectMapper.readValue(decodedSignedLicense, SignedLicense.class);
            licenseContent = this.jsonObjectMapper.readValue(signedLicense.getLicenseJSON(), LicenseContent.class);
        } catch (IOException var15) {
            throw new LicenseValidationException("Decoding license from JSON failed.", var15);
        }

        if(product == null) {
            licenseProduct = this.getProductForLicense(licenseContent);
        }

        LicenseStatus status;
        try {
            status = this.verifyLicense(signedLicense);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException var14) {
            throw new LicenseValidationException("Signature verification failed for license.", var14);
        }

        DefaultLicense license = new DefaultLicense(status, licenseContent);
        if(!licenseProduct.getProductId().equals(license.getProductId())) {
            license.setStatus(LicenseStatus.WRONG_PRODUCT_ID);
        }

        if(license.getStatus() == LicenseStatus.VALID) {
            List licenseProductVersions = licenseContent.getProductVersions();
            if(licenseProductVersions != null && !licenseProductVersions.isEmpty()) {
                boolean supported = licenseProductVersions.contains(licenseProduct.getProductVersion());
                List productSupportedVersions = licenseProduct.getSupportedVersions();

                String productSupportedVersion;
                for(Iterator licenseKey = productSupportedVersions.iterator(); licenseKey.hasNext(); supported |= licenseProductVersions.contains(productSupportedVersion)) {
                    productSupportedVersion = (String)licenseKey.next();
                }

                if(!supported) {
                    license.setStatus(LicenseStatus.PRODUCT_VERSION_INVALID);
                } else {
                    license.setStatus(licenseContent.isValid(this.getNow()));
                    if(license.getStatus() == LicenseStatus.VALID) {
                        String licenseKey1 = licenseContent.getLicenseKey();
                        if(licenseKey1 != null) {
                            license.setStatus(this.checkKey(licenseKey1));
                        }
                    }
                }
            } else {
                license.setStatus(LicenseStatus.PRODUCT_VERSION_INVALID);
            }
        }

        return new Pair(licenseProduct, license);
    }

    private final boolean validateKeyChecksum(String key) {
        if(key.length() != 62) {
            return false;
        } else {
            String checksum = key.substring(58);
            return checksum.equals(this.getChecksum(key.substring(0, 58)));
        }
    }

    private final String getChecksum(String s) {
        int left = 86;
        int right = 175;
        byte[] sum = s.getBytes(StandardCharsets.UTF_8);
        int var5 = sum.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            byte b = sum[var6];
            right += b;
            if(right > 255) {
                right -= 255;
            }

            left += right;
            if(left > 255) {
                left -= 255;
            }
        }

        int var8 = (left << 8) + right;
        return LicenseUtilities.numberToHex(Integer.valueOf(var8), 4);
    }

    private final LicenseStatus checkKey(String key) {
        if(!this.validateKeyChecksum(key)) {
            return LicenseStatus.KEY_INVALID;
        } else {
            int seed;
            try {
                seed = Integer.valueOf(key.substring(0, 8), 16).intValue();
            } catch (NumberFormatException var10) {
                return LicenseStatus.KEY_PHONY;
            }

            String kb2 = key.substring(12, 14);
            byte b2 = this.getKeyByte(seed, getParams()[2][0], getParams()[2][1], getParams()[2][2]);
            if(!kb2.equals(LicenseUtilities.numberToHex(Byte.valueOf(b2), 2))) {
                return LicenseStatus.KEY_PHONY;
            } else {
                byte[] encodedEntropy = this.getHardwareEntropy();
                int i = 16;

                for(int j = 0; j + 2 < encodedEntropy.length; j += 3) {
                    String kb = key.substring(i, i + 2);
                    byte b = this.getKeyByte(seed, encodedEntropy[j], encodedEntropy[j + 1], encodedEntropy[j + 2]);
                    if(!kb.equals(LicenseUtilities.numberToHex(Byte.valueOf(b), 2))) {
                        return LicenseStatus.KEY_INVALID;
                    }

                    i += 2;
                }

                return LicenseStatus.VALID;
            }
        }
    }

    private final byte getKeyByte(int seed, byte a, byte b, byte c) {
        int a1 = a % 25;
        int b1 = b % 3;
        return a1 % 2 == 0?(byte)(seed >> a1 & 255 ^ (seed >> b1 | c)):(byte)(seed >> a1 & 255 ^ seed >> b1 & c);
    }

    private final byte[] getHardwareEntropy() {
        byte[] mac;
        try {
            InetAddress entropyEncoded = InetAddress.getLocalHost();
            if(entropyEncoded == null) {
                mac = DEF;
            } else {
                NetworkInterface digest = NetworkInterface.getByInetAddress(entropyEncoded);
                if(digest != null) {
                    mac = digest.getHardwareAddress();
                    if(mac == null) {
                        mac = DEF;
                    }
                } else {
                    mac = DEF;
                }
            }
        } catch (UnknownHostException | SocketException var5) {
            mac = DEF;
        }

        byte[] entropyEncoded1 = null;

        try {
            MessageDigest digest1 = MessageDigest.getInstance("SHA-512");
            digest1.reset();
            entropyEncoded1 = digest1.digest(mac);
        } catch (NoSuchAlgorithmException var4) {
            ;
        }

        return entropyEncoded1;
    }

    private final LicenseStatus verifyLicense(SignedLicense signedLicense) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature rsaSignature = Signature.getInstance("SHA1withRSA");
        rsaSignature.initVerify(this.publicKey);
        rsaSignature.update(signedLicense.getLicenseJSON().getBytes(Charset.forName("UTF-8")));
        return rsaSignature.verify(signedLicense.getSignature())?LicenseStatus.VALID:LicenseStatus.SIGNATURE_INVALID;
    }

    private final LicenseStatus verifyProduct(Product product) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if(product.isExtension() && product.getProductId().startsWith("rapidminer-")) {
            return LicenseStatus.WRONG_PRODUCT_ID;
        } else {
            Signature rsaSignature = Signature.getInstance("SHA1withRSA");
            rsaSignature.initVerify(this.publicKey);
            rsaSignature.update(product.createBase64Representation().getBytes(StandardCharsets.UTF_8));
            return rsaSignature.verify(DatatypeConverter.parseBase64Binary(product.getSignature()))?LicenseStatus.VALID:LicenseStatus.SIGNATURE_INVALID;
        }
    }

    private final Date getNow() {
        return Calendar.getInstance().getTime();
    }

    public <S, C> LicenseConstraintViolation<S, C> checkConstraintViolation(Product product, Constraint<S, C> constraint, C checkedValue, boolean informListeners) {
        return this.checkConstraintViolation(product, constraint, checkedValue, (String)null, informListeners);
    }

    public <S, C> LicenseConstraintViolation<S, C> checkConstraintViolation(Product product, Constraint<S, C> constraint, C checkedValue, String i18nKey, boolean informListeners) {
        License activeLicense = this.getActiveLicense(product);
        S constraintValue = null;

        try {
            constraintValue = this.getConstraintValue(activeLicense, constraint);
        } catch (ConstraintNotRestrictedException var9) {
            return null;
        }

        if(!constraint.isAllowed(constraintValue, checkedValue)) {
            LicenseConstraintViolation cause = new LicenseConstraintViolation(activeLicense, constraint, constraintValue, checkedValue, i18nKey);
            if(informListeners) {
                this.handleLicenseEvent(new LicenseEvent(cause));
            }

            return cause;
        } else {
            return null;
        }
    }

    public boolean isAllowedByAnnotations(Object obj) {
        return this.licenseAnnotationValidator.isAllowedByAnnotations(obj, this.registeredProducts);
    }

    public List<LicenseViolation> checkAnnotationViolations(Object obj, boolean informListeners) {
        List checkConstraintCauses = this.licenseAnnotationValidator.checkAnnotationViolations(obj, this.registeredProducts);
        if(informListeners && !checkConstraintCauses.isEmpty()) {
            this.handleLicenseEvent(new LicenseEvent(checkConstraintCauses));
        }

        return checkConstraintCauses;
    }

    public <S, C> boolean isAllowed(Product product, Constraint<S, C> constraint, C checkedValue) {
        try {
            License e = this.getActiveLicense(product);
            S constraintValue = this.getConstraintValue(e, constraint);
            return constraint.isAllowed(constraintValue, checkedValue);
        } catch (ConstraintNotRestrictedException var6) {
            return true;
        }
    }

    public <S, C> S getConstraintValue(Product product, Constraint<S, C> constraint) throws ConstraintNotRestrictedException {
        return this.getConstraintValue(this.getActiveLicense(product), constraint);
    }

    public boolean isAtLeastVersion(DefaultLicenseManager.LicenseVersion minimumRequiredVersion) {
        if(minimumRequiredVersion == null) {
            throw new IllegalArgumentException("minimumRequiredVersion must not be null!");
        } else {
            return DefaultLicenseManager.LicenseVersion.isVersionAtLeast(this.currentVersion, minimumRequiredVersion);
        }
    }

    public DefaultLicenseManager.LicenseVersion getVersion() {
        return this.currentVersion;
    }

    private <S, C> S getConstraintValue(License license, Constraint<S, C> constraint) throws ConstraintNotRestrictedException {
        Constraints licenseConstraints = license.getConstraints();
        String valueFromLicense = licenseConstraints.getConstraintValue(constraint);
        return valueFromLicense == null?null:constraint.transformFromString(valueFromLicense);
    }

    public void registerLicenseManagerListener(LicenseManagerListener l) {
        this.licenseManagerListeners.add(l);
    }

    public void removeLicenseManagerListener(LicenseManagerListener l) {
        this.licenseManagerListeners.remove(l);
    }

    private <S, C> void handleLicenseEvent(LicenseEvent<S, C> e) {
        LinkedList listenersToInform = new LinkedList(this.licenseManagerListeners);
        Iterator var3 = listenersToInform.iterator();

        while(var3.hasNext()) {
            LicenseManagerListener l = (LicenseManagerListener)var3.next();
            l.handleLicenseEvent(e);
        }

    }

    public void setLicenseLocation(LicenseLocation location) {
        this.licenseLocation = location;
    }

    public List<License> getAllActiveLicenses() {
        ArrayList licenses = new ArrayList(this.registeredProducts.size());
        Iterator var2 = this.registeredProducts.iterator();

        while(var2.hasNext()) {
            Product product = (Product)var2.next();
            licenses.add(this.getActiveLicense(product));
        }

        return licenses;
    }

    public static byte[][] getParams() {
        return (byte[][])Arrays.copyOf(PARAMS, PARAMS.length);
    }

    public static enum LicenseVersion {
        PRODUCT_SIGNATURE_VERSION(1);

        private int version;

        private LicenseVersion(int version) {
            if(version <= 0) {
                throw new IllegalArgumentException("version must not be <= 0!");
            } else {
                this.version = version;
            }
        }

        public int getVersion() {
            return this.version;
        }

        public static boolean isVersionAtLeast(DefaultLicenseManager.LicenseVersion toCheck, DefaultLicenseManager.LicenseVersion atLeast) {
            return toCheck.getVersion() >= atLeast.getVersion();
        }
    }
}
