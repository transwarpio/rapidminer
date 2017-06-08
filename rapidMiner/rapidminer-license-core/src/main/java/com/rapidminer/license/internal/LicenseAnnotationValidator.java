package com.rapidminer.license.internal;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.license.ConstraintNotRestrictedException;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseManager;
import com.rapidminer.license.UnknownProductException;
import com.rapidminer.license.annotation.LicenseConstraint;
import com.rapidminer.license.annotation.LicenseLevel;
import com.rapidminer.license.product.Constraint;
import com.rapidminer.license.product.Product;
import com.rapidminer.license.violation.LicenseConstraintViolation;
import com.rapidminer.license.violation.LicenseLevelViolation;
import com.rapidminer.license.violation.LicenseViolation;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class LicenseAnnotationValidator {
    private final LicenseManager lm;

    LicenseAnnotationValidator(LicenseManager lm) {
        if(lm == null) {
            throw new IllegalArgumentException("LicenseManager must not be null!");
        } else {
            this.lm = lm;
        }
    }

    public <T> List<LicenseViolation> checkAnnotationViolations(T obj, List<Product> products) {
        LinkedList causes = new LinkedList();
        if(!this.isAllowedByAnnotations(obj, products)) {
            List licenseLevelAnnotations = this.getLicenseAnnotations(obj, LicenseLevel.class);
            Iterator constraintAnnotations = licenseLevelAnnotations.iterator();

            while(constraintAnnotations.hasNext()) {
                LicenseLevel annotation = (LicenseLevel)constraintAnnotations.next();
                if(!this.isAllowedByLicenseLevel(annotation, products)) {
                    License annotation1 = null;

                    try {
                        annotation1 = this.getActiveLicense(annotation.productId(), products);
                    } catch (UnknownProductException var9) {
                        ;
                    }

                    causes.add(new LicenseLevelViolation(obj, annotation1, annotation));
                }
            }

            List constraintAnnotations1 = this.getLicenseAnnotations(obj, LicenseConstraint.class);
            Iterator annotation2 = constraintAnnotations1.iterator();

            while(annotation2.hasNext()) {
                LicenseConstraint annotation3 = (LicenseConstraint)annotation2.next();
                this.checkForLicenseConstraintViolation(products, causes, annotation3);
            }
        }

        return causes;
    }

    private void checkForLicenseConstraintViolation(List<Product> products, List<LicenseViolation> causes, LicenseConstraint annotation) {
        if(!this.isAllowedByLicenseConstraint(annotation, products)) {
            LicenseConstraintViolation licenseConstraintViolation = this.getLicenseConstraintViolation(annotation, products);
            if(licenseConstraintViolation != null) {
                causes.add(licenseConstraintViolation);
            }
        }

    }

    private LicenseConstraintViolation<Object, Object> getLicenseConstraintViolation(LicenseConstraint annotation, List<Product> products) {
        Product productForAnnotation;
        try {
            productForAnnotation = this.getFirstProductForMatchingId(products, annotation.productId());
        } catch (UnknownProductException var9) {
            return new LicenseConstraintViolation((License)null, (Constraint)null, (Object)null, (Object)null, annotation.i18nKey());
        }

        License activeLicense = this.lm.getActiveLicense(productForAnnotation);
        Constraint constraintForAnnotation = this.getConstraintForAnnotation(annotation, productForAnnotation);
        if(constraintForAnnotation == null) {
            return new LicenseConstraintViolation(activeLicense, (Constraint)null, (Object)null, (Object)null, annotation.i18nKey());
        } else {
            String constraintValue;
            try {
                constraintValue = activeLicense.getConstraints().getConstraintValue(constraintForAnnotation);
            } catch (ConstraintNotRestrictedException var8) {
                return null;
            }

            return new LicenseConstraintViolation(activeLicense, constraintForAnnotation, constraintValue, constraintForAnnotation.transformValueFromString(annotation.value()), annotation.i18nKey());
        }
    }

    public <T> boolean isAllowedByAnnotations(T obj, List<Product> products) {
        boolean isAllowed = true;
        List levelAnnotations = this.getLicenseAnnotations(obj, LicenseLevel.class);
        isAllowed &= levelAnnotations.isEmpty();
        Iterator constraintAnnotations = levelAnnotations.iterator();

        LicenseLevel licenseAnnotation;
        do {
            if(!constraintAnnotations.hasNext()) {
                List constraintAnnotations1 = this.getLicenseAnnotations(obj, LicenseConstraint.class);
                isAllowed &= constraintAnnotations1.isEmpty();
                Iterator licenseAnnotation1 = constraintAnnotations1.iterator();

                LicenseConstraint constraint;
                do {
                    if(!licenseAnnotation1.hasNext()) {
                        return isAllowed;
                    }

                    constraint = (LicenseConstraint)licenseAnnotation1.next();
                } while(!this.isAllowedByLicenseConstraint(constraint, products));

                return true;
            }

            licenseAnnotation = (LicenseLevel)constraintAnnotations.next();
        } while(!this.isAllowedByLicenseLevel(licenseAnnotation, products));

        return true;
    }

    private <O, T extends Annotation> List<T> getLicenseAnnotations(O object, Class<T> annotationClass) {
        LinkedList licenseAnntotations = new LinkedList();

        for(Class currentClass = object.getClass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            Annotation annotation = currentClass.getAnnotation(annotationClass);
            if(annotation != null) {
                licenseAnntotations.add(annotation);
            }
        }

        return licenseAnntotations;
    }

    private boolean isAllowedByLicenseLevel(LicenseLevel licenseLevel, List<Product> products) {
        License activeLicense;
        try {
            activeLicense = this.getActiveLicense(licenseLevel.productId(), products);
        } catch (UnknownProductException var5) {
            return false;
        }

        int precedence = licenseLevel.precedence();
        switch(licenseLevel.comparison().ordinal()) {
            case 1:
                return activeLicense.getPrecedence() == precedence;
            case 2:
                return activeLicense.getPrecedence() >= precedence;
            default:
                throw new IllegalStateException("Unknown precedence predicate " + licenseLevel.comparison());
        }
    }

    private boolean isAllowedByLicenseConstraint(LicenseConstraint annotation, List<Product> products) {
        Product productForAnnotation;
        try {
            productForAnnotation = this.getFirstProductForMatchingId(products, annotation.productId());
        } catch (UnknownProductException var5) {
            return false;
        }

        Constraint constraintForAnnotation = this.getConstraintForAnnotation(annotation, productForAnnotation);
        return constraintForAnnotation == null?false:this.lm.isAllowed(productForAnnotation, constraintForAnnotation, constraintForAnnotation.transformValueFromString(annotation.value()));
    }

    private final Product getFirstProductForMatchingId(List<Product> products, String productIdRegEx) throws UnknownProductException {
        Iterator var3 = products.iterator();

        Product product;
        do {
            if(!var3.hasNext()) {
                throw new UnknownProductException(productIdRegEx);
            }

            product = (Product)var3.next();
        } while(!product.getProductId().matches(productIdRegEx));

        return product;
    }

    private final Constraint getConstraintForAnnotation(LicenseConstraint annotation, Product product) {
        return product.findConstraint(annotation.constraintId());
    }

    private final License getActiveLicense(String productIdRegex, List<Product> products) throws UnknownProductException {
        return this.lm.getActiveLicense(this.getFirstProductForMatchingId(products, productIdRegex));
    }
}
