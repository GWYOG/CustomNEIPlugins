package pers.gwyog.customneiplugins.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class StringUtils {
    
    // TODO: Complete this method.
    public static List<String> splitString(String str, int length) {
        List list = new ArrayList<String>();
        return list;
    }
    
    public static String parseUnlocalizedString(String str) {
        if (str == null) return "";
        String[] strArray = str.split("_,");
        if (strArray.length == 1)
            return I18n.format(str);
        else {
            String[] paramArray = new String[strArray.length - 1];
            for (int i = 0; i < paramArray.length; i++)
                paramArray[i] = strArray[i + 1];
            return I18n.format(strArray[0], (Object[]) paramArray);
        }
    }
}
