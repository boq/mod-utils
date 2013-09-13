package boq.utils.lang;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class Lang {
    private final String lang;
    private final boolean isXml;

    public Lang(String lang, boolean isXml) {
        this.lang = lang;
        this.isXml = isXml;
    }

    public void load(String resourcePrefix) {
        String fileName = resourcePrefix + lang + (isXml ? ".xml" : ".lang");
        LanguageRegistry.instance().loadLocalization(fileName, lang, isXml);
    }
}