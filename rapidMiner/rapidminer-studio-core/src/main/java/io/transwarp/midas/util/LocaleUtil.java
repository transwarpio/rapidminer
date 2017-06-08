package io.transwarp.midas.util;

import com.rapidminer.RapidMiner;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;

import java.util.Locale;
import java.util.logging.Level;

public class LocaleUtil {
    private static Locale _locale = null;
    public static Locale getLocale() {
        if( _locale != null){
            return _locale;
        } else {
            String localeLanguage = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_GENERAL_LOCALE_LANGUAGE);
            // String country = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_GENERAL_LOCALE_COUNTRY);
            Locale locale = Locale.getDefault();
            if (localeLanguage != null) {
                locale = new Locale(localeLanguage);
                Locale.setDefault(locale);
                LogService.getRoot().log(Level.INFO, "com.rapidminer.tools.I18N.set_locale_to", locale);
            } else {
                LogService.getRoot().log(Level.INFO, "com.rapidminer.tools.I18N.using_default_locale", locale);
            }
            _locale = locale;
            return locale;

        }
    }
}
