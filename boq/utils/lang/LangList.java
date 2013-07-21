package boq.utils.lang;

import cpw.mods.fml.common.registry.LanguageRegistry;

public final class LangList {
    private LangList() {}

    public static class Lang {
        private String lang;

        public Lang(String lang) {
            this.lang = lang;
        }

        public void load(String resourcePrefix, boolean isXml) {
            String fileName = resourcePrefix + lang + (isXml ? ".xml" : ".lang");
            LanguageRegistry.instance().loadLocalization(fileName, lang, isXml);
        }
    }

    public static Lang[] languages = new Lang[] {
            new Lang("en_US")
    };

    public static void loadAll(String resourcePrefix, boolean isXml) {
        for (Lang l : languages)
            l.load(resourcePrefix, isXml);
    }

}
