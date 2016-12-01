package pers.gwyog.customneiplugins.helper;

import cubex2.cs2.CustomStuff2;
import cubex2.cs2.Mod;
import cubex2.cs2.gui.GuiCSContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import pers.gwyog.customneiplugins.CustomNEIPlugins;

public class CustomStuff2Helper {
    
    public static Class<? extends GuiContainer> getCS2GuiContainer(String modid, String guiName) {
        if (!CustomNEIPlugins.isCustomStuff2Loaded)
            return null;
        else {
            Mod mod = CustomStuff2.mods.get(modid);
            if (mod == null)
                return null;
            else 
                return (Class<? extends GuiContainer>) mod.getGuiHandler().getAttributes(guiName).getInformation().getGuiClass(); 
        }
    }
}
