package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.deployment.client.wsimport.AccountService;
import com.rapidminer.deployment.client.wsimport.AccountServiceService;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.client.wsimport.UpdateServiceException_Exception;
import com.rapidminer.deployment.client.wsimport.UpdateServiceService;
import com.rapidminer.deployment.update.client.ChecksumException;
import com.rapidminer.deployment.update.client.InMemoryZipFile;
import com.rapidminer.deployment.update.client.PendingPurchasesInstallationDialog;
import com.rapidminer.deployment.update.client.UpdateConfirmDialog;
import com.rapidminer.deployment.update.client.UpdateDialog;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.security.UserCredential;
import com.rapidminer.gui.security.Wallet;
import com.rapidminer.gui.tools.PasswordDialog;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.VersionNumber;
import com.rapidminer.gui.tools.dialogs.ExtendedErrorDialog;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.PasswordInputCanceledException;
import com.rapidminer.tools.PlatformUtilities;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.tools.RMUrlHandler;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.WebServiceTools;
import com.rapidminer.tools.PlatformUtilities.Platform;
import com.rapidminer.tools.io.ProgressReportingInputStream;
import com.rapidminer.tools.plugin.ManagedExtension;
import com.rapidminer.tools.update.internal.UpdateManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.swing.SwingUtilities;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MarketplaceUpdateManager implements UpdateManager {
    public static final Platform TARGET_PLATFORM;
    public static final boolean DEVELOPMENT_BUILD;
    private static final UpdateServerAccount usAccount;
    private final UpdateService service;
    private static UpdateService theService;
    private static URI lastUsedUri;
    private static AccountService accountService;
    private static String packageIdRapidMiner;

    public MarketplaceUpdateManager(UpdateService service) {
        this.service = service;
    }

    private InputStream openStream(URL url, ProgressListener listener, int minProgress, int maxProgress) throws IOException {
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        WebServiceTools.setURLConnectionDefaults(con);
        con.setDoInput(true);
        con.setDoOutput(false);
        String lengthStr = con.getHeaderField("Content-Length");

        InputStream urlIn;
        try {
            urlIn = con.getInputStream();
        } catch (IOException var11) {
            throw new IOException(con.getResponseCode() + ": " + con.getResponseMessage(), var11);
        }

        if(lengthStr != null && !lengthStr.isEmpty()) {
            try {
                long e = Long.parseLong(lengthStr);
                return new ProgressReportingInputStream(urlIn, listener, minProgress, maxProgress, e);
            } catch (NumberFormatException var10) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.sending_illegal_content_length_error", new Object[]{lengthStr}), var10);
                return urlIn;
            }
        } else {
            LogService.getRoot().log(Level.WARNING, "com.rapid_i.deployment.update.client.UpdateManager.sending_content_length_error");
            return urlIn;
        }
    }

    public List<PackageDescriptor> performUpdates(List<PackageDescriptor> downloadList, ProgressListener progressListener) throws IOException, UpdateServiceException_Exception {
        Iterator i = downloadList.iterator();

        while(i.hasNext()) {
            PackageDescriptor failedLoads = (PackageDescriptor)i.next();
            if("STAND_ALONE".equals(failedLoads.getPackageTypeName()) && DEVELOPMENT_BUILD) {
                SwingTools.showVerySimpleErrorMessageAndWait("update_error_development_build", new Object[0]);
                i.remove();
            }
        }

        int var35 = 0;
        int var36 = 0;
        int availableLoads = downloadList.size();
        LinkedList installedPackage = new LinkedList();

        try {
            Iterator e = downloadList.iterator();

            label217:
            while(true) {
                while(true) {
                    if(!e.hasNext()) {
                        break label217;
                    }

                    PackageDescriptor desc = (PackageDescriptor)e.next();
                    String urlString = this.service.getDownloadURL(desc.getPackageId(), desc.getVersion(), desc.getPlatformName());
                    int minProgress = 20 + 80 * var35 / downloadList.size();
                    int maxProgress = 20 + 80 * (var35 + 1) / downloadList.size();
                    boolean incremental = isIncrementalUpdate();
                    if("RAPIDMINER_PLUGIN".equals(desc.getPackageTypeName())) {
                        ManagedExtension url = ManagedExtension.getOrCreate(desc.getPackageId(), desc.getName(), desc.getLicenseName());
                        String e1 = url.getLatestInstalledVersionBefore(desc.getVersion());
                        incremental &= e1 != null;
                        URL url1 = getUpdateServerURI(urlString + (incremental?"?baseVersion=" + URLEncoder.encode(e1, "UTF-8"):"")).toURL();
                        boolean succesful = false;
                        if(incremental) {
                            LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.updating_package_id_incrementally", desc.getPackageId());

                            try {
                                this.updatePluginIncrementally(url, this.openStream(url1, progressListener, minProgress, maxProgress), e1, desc.getVersion(), urlString + "?baseVersion=" + URLEncoder.encode(e1, "UTF-8") + "&md5");
                                succesful = true;
                            } catch (IOException var31) {
                                LogService.getRoot().warning("com.rapid_i.deployment.update.client.UpdateManager.incremental_update_error");
                                incremental = false;
                                url1 = getUpdateServerURI(urlString).toURL();
                            } catch (ChecksumException var32) {
                                LogService.getRoot().warning("com.rapid_i.deployment.update.client.UpdateManager.incremental_update_error");
                                incremental = false;
                                url1 = getUpdateServerURI(urlString).toURL();
                            }
                        }

                        if(!incremental) {
                            LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.updating_package_id", desc.getPackageId());

                            try {
                                this.updatePlugin(url, this.openStream(url1, progressListener, minProgress, maxProgress), desc.getVersion(), urlString + "?md5");
                                succesful = true;
                            } catch (IOException var29) {
                                ++var36;
                                this.reportDownloadError(var29, desc.getName());
                            } catch (ChecksumException var30) {
                                ++var36;
                                this.reportChecksumError(var30, desc.getName());
                            }
                        }

                        if(succesful) {
                            installedPackage.add(desc);
                            url.addAndSelectVersion(desc.getVersion());
                        } else if(e1 == null) {
                            ManagedExtension.remove(desc.getPackageId());
                        }
                    } else if("STAND_ALONE".equals(desc.getPackageTypeName())) {
                        if(DEVELOPMENT_BUILD) {
                            SwingTools.showVerySimpleErrorMessageAndWait("update_error_development_build", new Object[0]);
                            continue;
                        }

                        if(useOSXUpdateMechansim()) {
                            continue;
                        }

                        URL var37 = getUpdateServerURI(urlString + (incremental?"?baseVersion=" + URLEncoder.encode(RapidMiner.getLongVersion(), "UTF-8"):"")).toURL();
                        LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.updating_rapidminer_core");

                        try {
                            this.updateRapidMiner(this.openStream(var37, progressListener, minProgress, maxProgress), desc.getVersion(), urlString + (incremental?"?baseVersion=" + URLEncoder.encode(RapidMiner.getLongVersion(), "UTF-8") + "&md5":"?md5"));
                            installedPackage.add(desc);
                        } catch (ChecksumException var27) {
                            ++var36;
                            this.reportChecksumError(var27, desc.getName());
                        } catch (IOException var28) {
                            ++var36;
                            this.reportDownloadError(var28, desc.getName());
                        }
                    } else {
                        SwingTools.showVerySimpleErrorMessageAndWait("updatemanager.unknown_package_type", new Object[]{desc.getName(), desc.getPackageTypeName()});
                    }

                    ++var35;
                    progressListener.setCompleted(20 + 80 * var35 / downloadList.size());
                }
            }
        } catch (URISyntaxException var33) {
            throw new IOException(var33);
        } finally {
            progressListener.complete();
        }

        if(availableLoads == var36 && availableLoads > 0) {
            SwingTools.showVerySimpleErrorMessageAndWait("updatemanager.updates_installed_partially", new Object[]{String.valueOf(availableLoads - var36), String.valueOf(availableLoads)});
        }

        return installedPackage;
    }

    private void reportChecksumError(final ChecksumException e, final String failedPackageName) {
        LogService.getRoot().log(Level.INFO, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.md5_failed", new Object[]{failedPackageName, e.getMessage()}), e);

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    ExtendedErrorDialog dialog = new ExtendedErrorDialog(RapidMinerGUI.getMainFrame(), "update_md5_error", e, true, new Object[]{failedPackageName, e.getMessage()});
                    dialog.setModal(true);
                    dialog.setVisible(true);
                }
            });
        } catch (InvocationTargetException var4) {
            LogService.getRoot().log(Level.WARNING, "Error showing error message: " + e, e);
        } catch (InterruptedException var5) {
            ;
        }

    }

    private void reportDownloadError(final IOException e, final String failedPackageName) {
        LogService.getRoot().log(Level.INFO, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.md5_failed", new Object[]{failedPackageName, e.getMessage()}), e);

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    ExtendedErrorDialog dialog = new ExtendedErrorDialog(RapidMinerGUI.getMainFrame(), "error_downloading_package", e, true, new Object[]{failedPackageName, e.getMessage()});
                    dialog.setModal(true);
                    dialog.setVisible(true);
                }
            });
        } catch (InvocationTargetException var4) {
            LogService.getRoot().log(Level.WARNING, "Error showing error message: " + e, e);
        } catch (InterruptedException var5) {
            ;
        }

    }

    private void updatePlugin(ManagedExtension extension, InputStream updateIn, String newVersion, String md5Adress) throws IOException, ChecksumException {
        File outFile = extension.getDestinationFile(newVersion);
        FileOutputStream out = new FileOutputStream(outFile);

        try {
            Tools.copyStreamSynchronously(updateIn, out, true);
        } finally {
            try {
                out.close();
            } catch (IOException var13) {
                ;
            }

        }

        if(!this.compareMD5(outFile, md5Adress)) {
            Tools.delete(outFile);
            throw new ChecksumException();
        }
    }

    private void updateRapidMiner(InputStream openStream, String version, String md5adress) throws IOException, ChecksumException {
        if(DEVELOPMENT_BUILD) {
            SwingTools.showVerySimpleErrorMessage("update_error_development_build", new Object[0]);
        }

        File updateRootDir = new File(FileSystemService.getUserRapidMinerDir(), "update");
        if(!updateRootDir.exists() && !updateRootDir.mkdir()) {
            throw new IOException("Cannot create update directory. Please ensure you have administrator permissions.");
        } else if(!updateRootDir.canWrite()) {
            throw new IOException("Cannot write to update directory. Please ensure you have administrator permissions.");
        } else {
            File updateFile = new File(updateRootDir, "rmupdate-" + version + ".jar");
            Tools.copyStreamSynchronously(openStream, new FileOutputStream(updateFile), true);
            if(!this.compareMD5(updateFile, md5adress)) {
                Tools.delete(updateFile);
                Tools.delete(updateRootDir);
                throw new ChecksumException();
            } else {
                File ruInstall = new File(updateRootDir, "RUinstall");
                ZipFile zip = new ZipFile(updateFile);
                Enumeration en = zip.entries();

                while(en.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry)en.nextElement();
                    if(!entry.isDirectory()) {
                        String name = entry.getName();
                        if("META-INF/UPDATE".equals(name)) {
                            Tools.copyStreamSynchronously(zip.getInputStream(entry), new FileOutputStream(new File(updateRootDir, "UPDATE")), true);
                        } else {
                            if(name.startsWith("rapidminer/")) {
                                name = name.substring("rapidminer/".length());
                            } else if(name.startsWith("rapidminer-studio/")) {
                                name = name.substring("rapidminer-studio/".length());
                            }

                            File dest = new File(ruInstall, name);
                            File parent = dest.getParentFile();
                            if(parent != null && !parent.exists()) {
                                parent.mkdirs();
                            }

                            Tools.copyStreamSynchronously(zip.getInputStream(entry), new FileOutputStream(dest), true);
                        }
                    }
                }

                zip.close();
                updateFile.delete();
                LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.prepared_rapidminer_for_update");
            }
        }
    }

    private void updatePluginIncrementally(ManagedExtension extension, InputStream diffJarIn, String fromVersion, String newVersion, String md5Adress) throws IOException, ChecksumException {
        ByteArrayOutputStream diffJarBuffer = new ByteArrayOutputStream();
        Tools.copyStreamSynchronously(diffJarIn, diffJarBuffer, true);
        byte[] downloadedFile = diffJarBuffer.toByteArray();
        LogService.getRoot().log(Level.FINE, "com.rapid_i.deployment.update.client.UpdateManager.downloaded_incremental_zip");
        InMemoryZipFile diffJar = new InMemoryZipFile(downloadedFile);
        if(!this.compareMD5(downloadedFile, md5Adress)) {
            throw new ChecksumException();
        } else {
            HashSet toDelete = new HashSet();
            byte[] updateEntry = diffJar.getContents("META-INF/UPDATE");
            if(updateEntry == null) {
                throw new IOException("META-INFO/UPDATE entry missing");
            } else {
                BufferedReader updateReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(updateEntry), "UTF-8"));

                String line;
                while((line = updateReader.readLine()) != null) {
                    String[] allNames = line.split(" ", 2);
                    if(allNames.length != 2) {
                        diffJarBuffer.close();
                        throw new IOException("Illegal entry in update script: " + line);
                    }

                    if(!"DELETE".equals(allNames[0])) {
                        diffJarBuffer.close();
                        throw new IOException("Illegal entry in update script: " + line);
                    }

                    toDelete.add(allNames[1].trim());
                }

                LogService.getRoot().log(Level.FINE, "com.rapid_i.deployment.update.client.UpdateManager.extracted_update_script", Integer.valueOf(toDelete.size()));
                HashSet allNames1 = new HashSet();
                allNames1.addAll(diffJar.entryNames());
                JarFile fromJar = extension.findArchive(fromVersion);
                Throwable var15 = null;

                try {
                    Enumeration e = fromJar.entries();

                    while(e.hasMoreElements()) {
                        ZipEntry newFile = (ZipEntry)e.nextElement();
                        allNames1.add(newFile.getName());
                    }

                    LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.extracted_entry_names", Integer.valueOf(allNames1.size()));
                    File newFile1 = extension.getDestinationFile(newVersion);
                    ZipOutputStream newJar = new ZipOutputStream(new FileOutputStream(newFile1));
                    JarFile oldArchive = extension.findArchive();
                    Throwable var20 = null;

                    try {
                        Iterator var21 = allNames1.iterator();

                        while(var21.hasNext()) {
                            String name = (String)var21.next();
                            if(toDelete.contains(name)) {
                                LogService.getRoot().log(Level.FINEST, "com.rapid_i.deployment.update.client.UpdateManager.delete_name", name);
                            } else {
                                newJar.putNextEntry(new ZipEntry(name));
                                if(diffJar.containsEntry(name)) {
                                    newJar.write(diffJar.getContents(name));
                                    LogService.getRoot().log(Level.FINEST, "com.rapid_i.deployment.update.client.UpdateManager.update_name", name);
                                } else {
                                    ZipEntry oldEntry = oldArchive.getEntry(name);
                                    Tools.copyStreamSynchronously(oldArchive.getInputStream(oldEntry), newJar, false);
                                    LogService.getRoot().log(Level.FINEST, "com.rapid_i.deployment.update.client.UpdateManager.store_name", name);
                                }

                                newJar.closeEntry();
                            }
                        }

                        newJar.finish();
                        newJar.close();
                    } catch (Throwable var45) {
                        var20 = var45;
                        throw var45;
                    } finally {
                        if(oldArchive != null) {
                            if(var20 != null) {
                                try {
                                    oldArchive.close();
                                } catch (Throwable var44) {
                                    var20.addSuppressed(var44);
                                }
                            } else {
                                oldArchive.close();
                            }
                        }

                    }
                } catch (Throwable var47) {
                    var15 = var47;
                    throw var47;
                } finally {
                    if(fromJar != null) {
                        if(var15 != null) {
                            try {
                                fromJar.close();
                            } catch (Throwable var43) {
                                var15.addSuppressed(var43);
                            }
                        } else {
                            fromJar.close();
                        }
                    }

                }
            }
        }
    }

    public static String getBaseUrl() {
        String property = ParameterService.getParameterValue("rapidminer.update.url");
        if(property == null) {
            return "https://marketplace.rapidminer.com/UpdateServer";
        } else {
            if(property.equals("http://marketplace.rapid-i.com:80/UpdateServer")) {
                property = "https://marketplace.rapidminer.com/UpdateServer";
                ParameterService.setParameterValue("rapidminer.update.url", "https://marketplace.rapidminer.com/UpdateServer");
            }

            return property;
        }
    }

    public static URI getUpdateServerURI(String suffix) throws URISyntaxException {
        return new URI(getBaseUrl() + suffix);
    }

    public static boolean isIncrementalUpdate() {
        return !"false".equals(ParameterService.getParameterValue("rapidminer.update.incremental"));
    }

    public static synchronized UpdateService getService() throws MalformedURLException, URISyntaxException {
        URI uri = getUpdateServerURI("/UpdateServiceService?wsdl");
        if(theService == null || lastUsedUri != null && !lastUsedUri.equals(uri)) {
            UpdateServiceService uss = new UpdateServiceService(uri.toURL(), new QName("http://ws.update.deployment.rapid_i.com/", "UpdateServiceService"));

            try {
                theService = uss.getUpdateServicePort();
            } catch (Error var3) {
                throw new RuntimeException(var3);
            }
        }

        lastUsedUri = uri;
        return theService;
    }

    public static synchronized void resetService() {
        lastUsedUri = null;
        theService = null;
    }

    public static final boolean isAccountServiceCreated() {
        return accountService != null;
    }

    public static void clearAccountSerive() {
        accountService = null;
        WebServiceTools.clearAuthCache();
    }

    public static synchronized AccountService getAccountService() throws MalformedURLException, URISyntaxException {
        URI uri = getUpdateServerURI("/AccountService?wsdl");
        if(accountService == null) {
            AccountServiceService ass = new AccountServiceService(uri.toURL(), new QName("http://ws.update.deployment.rapid_i.com/", "AccountServiceService"));
            accountService = ass.getAccountServicePort();
            WebServiceTools.setCredentials((BindingProvider)accountService, usAccount.getUserName(), usAccount.getPassword());
        }

        return accountService;
    }

    public static void saveLastUpdateCheckDate() {
        File file = FileSystemService.getUserConfigFile("updatecheck.date");
        PrintWriter out = null;

        try {
            out = new PrintWriter(new FileWriter(file));
            out.println((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        } catch (IOException var6) {
            LogService.getRoot().log(Level.WARNING, "Failed to save update timestamp: " + var6, var6);
        } finally {
            if(out != null) {
                out.close();
            }

        }

    }

    private String getServerMD5(InputStream md5Stream) throws IOException {
        byte[] md5Hash = new byte[0];

        try {
            ByteArrayOutputStream e = new ByteArrayOutputStream();
            Tools.copyStreamSynchronously(md5Stream, e, true);
            md5Hash = e.toByteArray();
        } catch (IOException var4) {
            md5Stream.close();
            throw new IOException("failure while downloading the hash from Server: " + var4.getMessage());
        }

        return new String(md5Hash);
    }

    private boolean compareMD5(File toCompare, String urlString) {
        if(urlString != null && toCompare != null && !urlString.equals("")) {
            try {
                String e = getMD5hash(toCompare);
                URL url = getUpdateServerURI(urlString).toURL();
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                WebServiceTools.setURLConnectionDefaults(con);
                con.setDoInput(true);
                con.setDoOutput(false);

                String serverMD5;
                try {
                    serverMD5 = this.getServerMD5(con.getInputStream());
                } catch (IOException var8) {
                    throw new IOException(con.getResponseCode() + ": " + con.getResponseMessage(), var8);
                }

                return serverMD5.compareTo(e) == 0;
            } catch (Exception var9) {
                return false;
            }
        } else {
            throw new IllegalArgumentException("parameter is empty");
        }
    }

    private boolean compareMD5(byte[] toCompare, String urlString) {
        if(urlString != null && toCompare != null && !urlString.equals("")) {
            String localMD5 = getMD5hash(toCompare);

            try {
                URL e = getUpdateServerURI(urlString).toURL();
                HttpURLConnection con = (HttpURLConnection)e.openConnection();
                WebServiceTools.setURLConnectionDefaults(con);
                con.setDoInput(true);
                con.setDoOutput(false);

                String serverMD5;
                try {
                    serverMD5 = this.getServerMD5(con.getInputStream());
                } catch (IOException var8) {
                    throw new IOException(con.getResponseCode() + ": " + con.getResponseMessage(), var8);
                }

                return serverMD5.compareTo(localMD5) == 0;
            } catch (Exception var9) {
                return false;
            }
        } else {
            throw new IllegalArgumentException("parameter is empty");
        }
    }

    public static String getMD5hash(File toHash) throws FileNotFoundException {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(toHash);
            byte[] buffer = new byte[8192];
            boolean read = false;

            try {
                int var22;
                while((var22 = inputStream.read(buffer)) > 0) {
                    e.update(buffer, 0, var22);
                }

                byte[] e1 = e.digest();
                StringBuffer hex = new StringBuffer();
                byte[] var7 = e1;
                int e2 = e1.length;

                for(int var9 = 0; var9 < e2; ++var9) {
                    byte one = var7[var9];
                    hex.append(Integer.toHexString(one & 255 | 256).toLowerCase().substring(1, 3));
                }

                String var23 = hex.toString();
                return var23;
            } catch (IOException var19) {
                throw new RuntimeException("Unable to process file for MD5", var19);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException var18) {
                    throw new RuntimeException("Unable to close input stream for MD5 calculation", var18);
                }
            }
        } catch (NoSuchAlgorithmException var21) {
            throw new UnsupportedOperationException("No implementation of MD5 found.");
        }
    }

    public static String getMD5hash(byte[] toHash) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(toHash, 0, toHash.length);
            byte[] md5sum = e.digest();
            StringBuffer hex = new StringBuffer();
            byte[] var4 = md5sum;
            int var5 = md5sum.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte one = var4[var6];
                hex.append(Integer.toHexString(one & 255 | 256).toLowerCase().substring(1, 3));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException var8) {
            throw new UnsupportedOperationException("No implementation of MD5 found.");
        }
    }

    public static String getMD5hash(InputStream toHash) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            boolean read = false;

            try {
                int var21;
                while((var21 = toHash.read(buffer)) > 0) {
                    e.update(buffer, 0, var21);
                }

                byte[] e1 = e.digest();
                StringBuffer hex = new StringBuffer();
                byte[] var6 = e1;
                int e2 = e1.length;

                for(int var8 = 0; var8 < e2; ++var8) {
                    byte one = var6[var8];
                    hex.append(Integer.toHexString(one & 255 | 256).toLowerCase().substring(1, 3));
                }

                String var22 = hex.toString();
                return var22;
            } catch (IOException var18) {
                throw new RuntimeException("Unable to process file for MD5", var18);
            } finally {
                try {
                    toHash.close();
                } catch (IOException var17) {
                    throw new RuntimeException("Unable to close input stream for MD5 calculation", var17);
                }
            }
        } catch (NoSuchAlgorithmException var20) {
            throw new UnsupportedOperationException("No implementation of MD5 found.");
        }
    }

    private static Date loadLastUpdateCheckDate() {
        File file = FileSystemService.getUserConfigFile("updatecheck.date");
        if(!file.exists()) {
            return null;
        } else {
            BufferedReader in = null;

            Date var3;
            try {
                in = new BufferedReader(new FileReader(file));
                String e = in.readLine();
                if(e != null) {
                    var3 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(e);
                    return var3;
                }

                var3 = null;
                return var3;
            } catch (Exception var14) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.reading_update_check_error", new Object[0]), var14);
                var3 = null;
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var13) {
                        ;
                    }
                }

            }

            return var3;
        }
    }

    public static void checkForUpdates() {
        String updateProperty = ParameterService.getParameterValue("rapidminer.update.check");
        if(Tools.booleanValue(updateProperty, true)) {
            if(RapidMiner.getExecutionMode() == ExecutionMode.WEBSTART) {
                LogService.getRoot().config("com.rapid_i.deployment.update.client.UpdateManager.ignoring_update_check_webstart_mode");
                return;
            }

            boolean check = true;
            final Date lastCheckDate = loadLastUpdateCheckDate();
            if(lastCheckDate != null) {
                Calendar lastCheck = Calendar.getInstance();
                lastCheck.setTime(lastCheckDate);
                Calendar currentDate = Calendar.getInstance();
                currentDate.add(6, -2);
                if(!lastCheck.before(currentDate)) {
                    check = false;
                    LogService.getRoot().log(Level.CONFIG, "com.rapid_i.deployment.update.client.UpdateManager.ignoring_update_check_last_checkdate", lastCheckDate);
                }
            }

            if(check) {
                (new ProgressThread("check_for_updates") {
                    public void run() {
                        LogService.getRoot().info("com.rapid_i.deployment.update.client.UpdateManager.update_checking");
                        boolean updatesExist = false;

                        try {
                            String latestRMVersion = MarketplaceUpdateManager.getService().getLatestVersion("rapidminer-studio-6", MarketplaceUpdateManager.TARGET_PLATFORM.toString(), RapidMiner.getLongVersion());
                            updatesExist = latestRMVersion != null && !RapidMiner.getVersion().isAtLeast(new VersionNumber(latestRMVersion));
                        } catch (Exception var4) {
                            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.checking_for_updates_error", new Object[]{var4}), var4);
                            return;
                        }

                        MarketplaceUpdateManager.saveLastUpdateCheckDate();
                        if(updatesExist) {
                            UpdateConfirmDialog dialog = new UpdateConfirmDialog();
                            dialog.setVisible(true);
                            if(dialog.getReturnOption() == 0) {
                                if(MarketplaceUpdateManager.useOSXUpdateMechansim()) {
                                    MarketplaceUpdateManager.openOSXDownloadURL();
                                } else {
                                    UpdateDialog.showUpdateDialog(true, new String[0]);
                                }
                            }
                        } else {
                            LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.no_updates_aviable", lastCheckDate);
                        }

                    }
                }).start();
            }
        }

    }

    public static void checkForPurchasedNotInstalled() {
        String updateProperty = ParameterService.getParameterValue("rapidminer.update.purchased.not_installed.check");
        if(Tools.booleanValue(updateProperty, true)) {
            try {
                Class.forName("com.rapidminer.deployment.update.client.UpdateServerAccount");
            } catch (ClassNotFoundException var9) {
                LogService.getRoot().log(Level.WARNING, "The class \'UpdateServerAccount\' could not be found.");
            }

            String updateServerURI = null;

            try {
                updateServerURI = getUpdateServerURI("").toString();
            } catch (URISyntaxException var8) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.malformed_update_server_uri", new Object[]{var8}), var8);
                return;
            }

            UserCredential authentication = Wallet.getInstance().getEntry("Marketplace", updateServerURI);
            if(authentication == null || authentication.getPassword() == null) {
                return;
            }

            PasswordAuthentication passwordAuthentication = null;

            try {
                passwordAuthentication = PasswordDialog.getPasswordAuthentication("Marketplace", updateServerURI, false, true, "authentication.marketplace", new Object[0]);
            } catch (PasswordInputCanceledException var7) {
                ;
            }

            UpdateServerAccount.setPasswordAuthentication(passwordAuthentication);

            boolean check;
            try {
                getAccountService();
                check = true;
            } catch (Exception var6) {
                check = false;
            }

            if(check) {
                (new ProgressThread("check_for_recently_purchased_extensions") {
                    public void run() {
                        LogService.getRoot().info("com.rapid_i.deployment.update.client.UpdateManager.purchased_extensions_checking");
                        boolean updatesExist = false;
                        new ArrayList();

                        List purchasedExtensions;
                        try {
                            purchasedExtensions = MarketplaceUpdateManager.getAccountService().getLicensedProducts();
                            Iterator pnid = purchasedExtensions.iterator();

                            while(pnid.hasNext()) {
                                String packageId = (String)pnid.next();
                                if(ManagedExtension.get(packageId) != null) {
                                    pnid.remove();
                                }
                            }

                            updatesExist = !purchasedExtensions.isEmpty();
                            if(updatesExist) {
                                purchasedExtensions.removeAll(MarketplaceUpdateManager.readNeverRemindInstallExtensions());
                                updatesExist = !purchasedExtensions.isEmpty();
                            }
                        } catch (Exception var5) {
                            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.UpdateManager.checking_for_purchased_extensions_error", new Object[]{var5}), var5);
                            return;
                        }

                        if(updatesExist) {
                            PendingPurchasesInstallationDialog pnid1 = new PendingPurchasesInstallationDialog(purchasedExtensions);
                            pnid1.setVisible(true);
                        } else {
                            LogService.getRoot().log(Level.INFO, "com.rapid_i.deployment.update.client.UpdateManager.no_purchased_extensions", "");
                        }

                    }
                }).start();
            }
        }

    }

    private static List<String> readNeverRemindInstallExtensions() {
        File userConfigFile = FileSystemService.getUserConfigFile("ignored_extensions.xml");
        if(!userConfigFile.exists()) {
            return new ArrayList();
        } else {
            LogService.getRoot().log(Level.CONFIG, "com.rapid_i.deployment.update.client.UpdateManager.reading_ignored_extensions_file");

            Document doc;
            try {
                doc = XMLTools.parse(userConfigFile);
            } catch (Exception var6) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.PurchasedNotInstalledDialog.creating_xml_document_error", new Object[]{var6}), var6);
                return new ArrayList();
            }

            ArrayList ignoreList = new ArrayList();
            NodeList extensionElems = doc.getDocumentElement().getElementsByTagName("extension_name");

            for(int i = 0; i < extensionElems.getLength(); ++i) {
                Element extensionElem = (Element)extensionElems.item(i);
                ignoreList.add(extensionElem.getTextContent());
            }

            return ignoreList;
        }
    }

    protected static boolean useOSXUpdateMechansim() {
        return PlatformUtilities.getReleasePlatform() == Platform.OSX;
    }

    protected static void openOSXDownloadURL() {
        try {
            RMUrlHandler.browse(new URI(I18N.getGUILabel("update.osx.url", new Object[0])));
        } catch (URISyntaxException | IOException var1) {
            SwingTools.showVerySimpleErrorMessage("cannot_open_browser", new Object[0]);
        }

    }

    public static UpdateServerAccount getUpdateServerAccount() {
        return usAccount;
    }

    /** @deprecated */
    @Deprecated
    public static String getRMPackageId() {
        return packageIdRapidMiner;
    }

    /** @deprecated */
    @Deprecated
    public static void setRMPackageId(String packageIdRapidMiner) {
        packageIdRapidMiner = packageIdRapidMiner;
    }

    public void installSelectedPackages(final List<String> selectedPackages) {
        if(ProductConstraintManager.INSTANCE.isCommunityFeatureAllowed()) {
            UpdateDialog updateDialog = new UpdateDialog((String[])selectedPackages.toArray(new String[selectedPackages.size()]));
            updateDialog.installSelectedPackages();
        } else {
            UpdateDialog.showOnboardingDialog(new Runnable() {
                public void run() {
                    MarketplaceUpdateManager.this.installSelectedPackages(selectedPackages);
                }
            });
        }

    }

    public void showUpdateDialog(boolean selectUpdateTab, String... preselectedExtensions) {
        UpdateDialog.showUpdateDialog(selectUpdateTab, preselectedExtensions);
    }

    public String getExtensionIdForOperatorPrefix(String forPrefix) throws MalformedURLException, URISyntaxException, IOException {
        try {
            return getService().getRapidMinerExtensionForOperatorPrefix(forPrefix);
        } catch (RuntimeException var3) {
            throw new IOException("Unable to connect to the RapidMiner Marketplace.", var3);
        }
    }

    public String getLatestVersion(String extensionId, String targetPlatform, String rapidMinerStudioVersion) throws MalformedURLException, URISyntaxException, IOException {
        try {
            return getService().getLatestVersion(extensionId, targetPlatform, rapidMinerStudioVersion);
        } catch (RuntimeException | UpdateServiceException_Exception var5) {
            throw new IOException("Unable to connect to the RapidMiner Marketplace.", var5);
        }
    }

    public String getExtensionName(String extensionId, String latestVersion, String targetPlatform) throws MalformedURLException, URISyntaxException, IOException {
        PackageDescriptor desc;
        try {
            desc = getService().getPackageInfo(extensionId, latestVersion, targetPlatform);
        } catch (RuntimeException | UpdateServiceException_Exception var6) {
            throw new IOException("Unable to connect to the RapidMiner Marketplace.", var6);
        }

        return desc != null?desc.getName():null;
    }

    static {
        Platform platform = PlatformUtilities.getReleasePlatform();
        if(platform != null && !RapidMiner.getVersion().isDevelopmentBuild()) {
            if(platform == Platform.OSX) {
                TARGET_PLATFORM = Platform.ANY;
            } else {
                TARGET_PLATFORM = platform;
            }

            DEVELOPMENT_BUILD = false;
        } else {
            LogService.getRoot().log(Level.WARNING, "com.rapid_i.deployment.update.client.UpdateManager.error_development_build");
            TARGET_PLATFORM = Platform.ANY;
            DEVELOPMENT_BUILD = true;
        }

        usAccount = new UpdateServerAccount();
        theService = null;
        lastUsedUri = null;
        packageIdRapidMiner = "rapidminer-studio-6";
    }
}
