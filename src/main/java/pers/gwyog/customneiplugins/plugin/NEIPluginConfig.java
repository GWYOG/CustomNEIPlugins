package pers.gwyog.customneiplugins.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import pers.gwyog.customneiplugins.CustomNEIPlugins;
import pers.gwyog.customneiplugins.plugin.manager.PluginMachineRecipeManager;
import pers.gwyog.customneiplugins.plugin.manager.PluginStackInfoManager;
import pers.gwyog.customneiplugins.plugin.manager.PluginStackInfoManager.ComponentPluginStackInfo;

public class NEIPluginConfig implements IConfigureNEI {
    
    @Override
    public String getName() {
        return "Custom NEI Plugins";
    }

    @Override
    public String getVersion() {
        return CustomNEIPlugins.VERSION;
    }

    @Override
    public void loadConfig() {
        loadConfigThroughAPI();
    }
    
    public static void registerAllThePlugins() {
        loadConfigThroughAPI();
        loadConfigPostInit();
    }
    
    public static void loadConfigThroughAPI() {
        PluginStackInfo pluginStackInfo = new PluginStackInfo();
        API.registerRecipeHandler(pluginStackInfo);
        API.registerUsageHandler(pluginStackInfo);
    }
    
    public static void loadConfigPostInit() {
        // Since two instance of the same class are regarded as one by the API of NEI,
        // So we directly added them to the handlers of NEI, without using the API class.
        for (PluginMachineRecipe pluginMachineRecipe: PluginMachineRecipeManager.listPluginMachineRecipe) {
            GuiCraftingRecipe.craftinghandlers.add(pluginMachineRecipe);
            GuiUsageRecipe.usagehandlers.add(pluginMachineRecipe);
        }
    }
    
    public static void resetPluginManagers() {
        PluginStackInfoManager.mapStackToPluginInfo.clear();
        PluginMachineRecipeManager.listPluginMachineRecipe.clear();
    }

}
