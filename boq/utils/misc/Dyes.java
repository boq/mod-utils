package boq.utils.misc;

import java.util.Map;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Maps;

public class Dyes {
    private final static String[] dyeNames =
    {
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"
    };

    private static Map<Integer, Integer> dyeOres;

    private static Map<Integer, Integer> dyes() {
        if (dyeOres == null) {
            dyeOres = Maps.newHashMap();
            for (int i = 0; i < dyeNames.length; i++) {
                String dyeOreName = dyeNames[i];
                int dyeOreId = OreDictionary.getOreID(dyeOreName);
                int dyeColor = ItemDye.dyeColors[i];
                dyeOres.put(dyeOreId, dyeColor);
            }
        }

        return dyeOres;
    }

    public static boolean isDye(ItemStack stack) {
        return OreDictionary.getOreID(stack) != -1;
    }

    public static Integer dyeStackToColor(ItemStack stack) {
        int oreId = OreDictionary.getOreID(stack);
        if (oreId < 0)
            return null;

        return dyes().get(oreId);
    }
}
