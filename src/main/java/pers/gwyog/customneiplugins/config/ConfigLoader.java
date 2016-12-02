package pers.gwyog.customneiplugins.config;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import pers.gwyog.customneiplugins.CustomNEIPlugins;
import pers.gwyog.customneiplugins.helper.CustomStuff2Helper;
import pers.gwyog.customneiplugins.plugin.NEIPluginConfig;
import pers.gwyog.customneiplugins.plugin.PluginMachineRecipe;
import pers.gwyog.customneiplugins.plugin.PluginStackInfo;
import pers.gwyog.customneiplugins.plugin.PluginMachineRecipe.CustomRecipeTransferRectHandler;
import pers.gwyog.customneiplugins.plugin.component.ComponentExtraStrings;
import pers.gwyog.customneiplugins.plugin.component.ComponentGuiOffset;
import pers.gwyog.customneiplugins.plugin.component.ComponentInputStacks;
import pers.gwyog.customneiplugins.plugin.component.ComponentOutputStacks;
import pers.gwyog.customneiplugins.plugin.component.ComponentProgressBar;
import pers.gwyog.customneiplugins.plugin.component.ComponentRectangle;
import pers.gwyog.customneiplugins.plugin.manager.PluginMachineRecipeManager;
import pers.gwyog.customneiplugins.plugin.manager.PluginStackInfoManager;
import pers.gwyog.customneiplugins.plugin.manager.PluginStackInfoManager.ComponentPluginStackInfo;

public class ConfigLoader {
    public static final String[] PLUGIN_TYPES = {"PluginStackInfo", "PluginMachineRecipe"};
    public static Configuration config;
    public static String directoryPath;
    public static List<String> loadedPlugins = new ArrayList<String>();
    public static List<String> disabledPlugins = new ArrayList<String>();
    public static List<String> erroredPlugins = new ArrayList<String>();
    
    public static void init(FMLPreInitializationEvent event) {
        directoryPath = event.getModConfigurationDirectory() + "/CustomNEIPlugins/";
        
        // Setting up the main config
        config = new Configuration(new File(directoryPath + "MainSettings.cfg"));
        loadModConfig();
        
        // Setting up the Plugin Folders
        for (String pluginType: PLUGIN_TYPES) {
            File directoryPluginStackInfo = new File(directoryPath + pluginType + "/");
            if (!directoryPluginStackInfo.exists())
                directoryPluginStackInfo.mkdirs();
        }
        
        // Output The Example Plugins
        String[] exampleLocations = {
            "PluginStackInfo/ExamplePluginStackInfo.json",  
            "PluginMachineRecipe/ExamplePluginMachineRecipe1.json",               
            "PluginMachineRecipe/ExamplePluginMachineRecipe2.json"
        };
        for (String exampleLocation: exampleLocations) {
            try {
                File file = new File(directoryPath + exampleLocation);
                if (!file.exists()) {
                    PrintWriter printWriter = new PrintWriter(directoryPath + exampleLocation);
                    String examplePluginText = Resources.toString(Resources.getResource("assets/" + CustomNEIPlugins.MODID + "/examples/" + exampleLocation), Charsets.UTF_8);
                    printWriter.println(examplePluginText);
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    
    /**
     * Load all the plugins from json files to plugin managers.
     */
    public static void loadPlugins() {
        // Load Plugins
        for (String pluginType: PLUGIN_TYPES) {
            File directoryPluginStackInfo = new File(directoryPath + pluginType + "/");
            if (directoryPluginStackInfo.exists()) {
                File[] filePlugins = directoryPluginStackInfo.listFiles();
                if (filePlugins != null)
                    for (File filePlugin: filePlugins)
                        if (filePlugin.getName().endsWith(".json")) {
                            int loadStatus = loadJsonPlugin(filePlugin);
                            String fileName = filePlugin.getName().replace(".json", "");
                            switch (loadStatus) {
                            case -1: erroredPlugins.add(fileName);break;
                            case 0 : disabledPlugins.add(fileName);break;
                            case 1 : loadedPlugins.add(fileName);
                            }
                        }
            }
        }
    }
    
    /**
     * Reload all the plugins in-game.
     */
    public static void reloadPlugins() {
        removeAllThePlugins();
        loadModConfig();
        loadPlugins();
        NEIPluginConfig.registerAllThePlugins();
    }
    
    /**
     * Load the main config file of CustomNEIPlugins.
     */
    private static void loadModConfig() {
        config.load();
        config.addCustomCategoryComment("PluginStackInfo Settings", "You can configure the layout of all of the StackInfo Plugins here. (Tips: The width of the basic Gui is 176.)");
        ModConfig.pluginSIDisplayItemStackPosX = config.get("PluginStackInfo Settings", "displayItemStackPosX", 75).getInt();
        ModConfig.pluginSIDisplayItemStackPosY = config.get("PluginStackInfo Settings", "displayItemStackPosY", 0).getInt();
        ModConfig.pluginSIDisplayStringPosX = config.get("PluginStackInfo Settings", "displayStringPosX", 5).getInt();
        ModConfig.pluginSIDisplayStringPosY = config.get("PluginStackInfo Settings", "displayStringPosY", 20).getInt();
        ModConfig.pluginSIDisplayStringWidth = config.get("PluginStackInfo Settings", "displayStringWidth", 156).getInt();
        ModConfig.pluginSIDisplayStringVerticalIntervalOffset = config.get("PluginStackInfo Settings", "displayStringVerticalIntervalOffset", 2).getInt();
        config.save();
    } 
    
    private static int loadJsonPlugin(File filePlugin) {
        try {  
            JsonParser jsonParser=new JsonParser();
            JsonObject jsonObject=(JsonObject) jsonParser.parse(new FileReader(filePlugin));
            String pluginType = jsonObject.get("plugin_type").getAsString();
            Boolean pluginEnabled = jsonObject.get("plugin_enabled").getAsBoolean();
            int recipePerPage = jsonObject.get("plugin_recipe_per_page").getAsInt();
            String pluginUnlocalizedName = jsonObject.get("plugin_unlocalized_title_name").getAsString();
            if (!pluginEnabled)
                return 0;
            else if (pluginType.equals("plugin_stack_info")) {  
                JsonArray pluginContent = jsonObject.get("plugin_content").getAsJsonArray();  
                if (pluginContent != null && pluginContent.size() > 0) {
                    // Load the contents
                    JsonObject pluginInternalContent = pluginContent.get(0).getAsJsonObject();
                    String itemName = pluginInternalContent.get("item").getAsString();
                    int documentAmount = pluginInternalContent.get("document_amount").getAsInt();
                    String basicUnlocalizedDisplayString = pluginInternalContent.get("basic_unlocalized_display_string").getAsString();
                    List<String> listUnlocalizedDisplayString = new ArrayList<String>();
                    for (int i = 1; i <= documentAmount; i++)
                        listUnlocalizedDisplayString.add(basicUnlocalizedDisplayString + i);
                    boolean isCraftingRecipe = pluginInternalContent.get("is_crafting_recipe").getAsBoolean();
                    boolean isUsageRecipe = pluginInternalContent.get("is_usage_recipe").getAsBoolean();
                    
                    // Register the plugin
                    ComponentPluginStackInfo componentPluginStackInfo = new ComponentPluginStackInfo(recipePerPage, pluginUnlocalizedName, "customneiplugins:textures/gui/nei/guiBase.png", listUnlocalizedDisplayString.toArray(new String[documentAmount]), isCraftingRecipe, isUsageRecipe);
                    PluginStackInfoManager.registerPluginStackInfo(findItemsStack(itemName, 1), componentPluginStackInfo);
                }
            }
            else if (pluginType.equals("plugin_machine_recipe")) {
                // Load the Gui Texture Location
                String pluginGuiTextureLocation = jsonObject.get("plugin_gui_texture").getAsString();   
                
                // Load the Background Texture Location
                String pluginBackgroundTextureLocation = jsonObject.get("plugin_background_texture").getAsString();
                
                // Load the Gui class
                String pluginGuiClassPath = jsonObject.get("plugin_gui_class").getAsString();
                String guiNameCS2 = null;
                Class<? extends GuiContainer> clazzGuiContainer = null;
                if (pluginGuiClassPath.startsWith("custom_stuff2")) {
                    String[] temp = pluginGuiClassPath.split("\\.");
                    if (temp.length == 3) {
                        clazzGuiContainer = CustomStuff2Helper.getCS2GuiContainer(temp[1], temp[2]);    
                        guiNameCS2 = temp[2];
                    }
                }
                else
                    clazzGuiContainer = getClass(pluginGuiClassPath);
                
                // Load the recipe identifier
                String recipeIdentifier = jsonObject.get("plugin_recipe_identifier").getAsString(); 
                
                // Load the transfer rectangle
                ComponentRectangle rectangle = null;
                if (jsonObject.get("plugin_transfer_rect").getAsJsonArray().size() > 0) {
                    JsonObject pluginTransferRect = jsonObject.get("plugin_transfer_rect").getAsJsonArray().get(0).getAsJsonObject();  
                    int transferRectPosX = pluginTransferRect.get("posX").getAsInt();
                    int transferRectPosY = pluginTransferRect.get("posY").getAsInt();
                    int transferRectWidth = pluginTransferRect.get("width").getAsInt();
                    int transferRectHeight = pluginTransferRect.get("height").getAsInt();
                    rectangle = new ComponentRectangle(transferRectPosX, transferRectPosY, transferRectWidth, transferRectHeight);
                }
                
                // Load the progress bar
                ComponentProgressBar progressBar = null;
                if (jsonObject.get("plugin_progress_bar").getAsJsonArray().size() > 0) {
                    JsonObject pluginProgressBar = jsonObject.get("plugin_progress_bar").getAsJsonArray().get(0).getAsJsonObject();  
                    int progressBarPosX = pluginProgressBar.get("posX").getAsInt();
                    int progressBarPosY = pluginProgressBar.get("posY").getAsInt();
                    int progressBarTextureX = pluginProgressBar.get("textureX").getAsInt();
                    int progressBarTextureY = pluginProgressBar.get("textureY").getAsInt();
                    int progressBarWidth = pluginProgressBar.get("width").getAsInt();
                    int progressBarHeight = pluginProgressBar.get("height").getAsInt();
                    int progressBarTicks = pluginProgressBar.get("ticks").getAsInt();
                    int progressBarDirection = pluginProgressBar.get("direction").getAsInt();
                    progressBar = new ComponentProgressBar(progressBarPosX, progressBarPosY, progressBarTextureX, progressBarTextureY, progressBarWidth, progressBarHeight, progressBarTicks, progressBarDirection);
                }
               
                // Load the gui offset
                // TODO: let other types of plugin can have customized gui offset.
                JsonObject pluginGuiOffset = jsonObject.get("plugin_gui_offset").getAsJsonArray().get(0).getAsJsonObject();
                int guiOffsetX = pluginGuiOffset.get("offsetX").getAsInt();
                int guiOffsetY = pluginGuiOffset.get("offsetY").getAsInt();
                ComponentGuiOffset componentGuiOffset = new ComponentGuiOffset(guiOffsetX, guiOffsetY);
                
                // Load the recipes
                JsonArray pluginRecipes = jsonObject.get("plugin_recipes").getAsJsonArray();  
                List<ComponentInputStacks> listInputRecipe = new ArrayList<ComponentInputStacks>();
                List<ComponentOutputStacks> listOutputRecipe = new ArrayList<ComponentOutputStacks>();
                List<ComponentExtraStrings> listExtraStrings = new ArrayList<ComponentExtraStrings>();    
                for (int i = 0; i < pluginRecipes.size(); i++) {
                    JsonObject recipe = pluginRecipes.get(i).getAsJsonObject();
                    // Load the inputs
                    List<ItemStack> stacks = new ArrayList<ItemStack>();
                    List<Boolean> oredicts = new ArrayList<Boolean>();
                    List<Integer> posXs = new ArrayList<Integer>();
                    List<Integer> posYs = new ArrayList<Integer>();
                    JsonArray jsonInputStacks = recipe.get("inputs").getAsJsonArray();
                    for (int j = 0; j < jsonInputStacks.size(); j++) {
                        JsonObject jsonInputStack = jsonInputStacks.get(j).getAsJsonObject();
                        stacks.add(findItemsStack(jsonInputStack.get("item").getAsString(), jsonInputStack.get("stacksize").getAsInt()));
                        oredicts.add(jsonInputStack.get("oredict").getAsBoolean());
                        posXs.add(jsonInputStack.get("posX").getAsInt());
                        posYs.add(jsonInputStack.get("posY").getAsInt());
                    }
                    listInputRecipe.add(new ComponentInputStacks(stacks, oredicts, posXs, posYs));               
                    
                    // Load the outputs
                    stacks = new ArrayList<ItemStack>();
                    oredicts = new ArrayList<Boolean>();
                    posXs = new ArrayList<Integer>();
                    posYs = new ArrayList<Integer>();
                    JsonArray jsonOutputStacks = recipe.get("outputs").getAsJsonArray();
                    for (int j = 0; j < jsonOutputStacks.size(); j++) {
                        JsonObject jsonOutputStack = jsonOutputStacks.get(j).getAsJsonObject();
                        stacks.add(findItemsStack(jsonOutputStack.get("item").getAsString(), jsonOutputStack.get("stacksize").getAsInt()));
                        oredicts.add(jsonOutputStack.get("oredict_search").getAsBoolean());
                        posXs.add(jsonOutputStack.get("posX").getAsInt());
                        posYs.add(jsonOutputStack.get("posY").getAsInt());
                    }
                    listOutputRecipe.add(new ComponentOutputStacks(stacks, oredicts, posXs, posYs));
                    
                    // Load the strings
                    List<String> strings = new ArrayList<String>();
                    posXs = new ArrayList<Integer>();
                    posYs = new ArrayList<Integer>();
                    JsonArray jsonExtraStrings = recipe.get("strings").getAsJsonArray();
                    for (int j = 0; j < jsonExtraStrings.size(); j++) {
                        JsonObject jsonExtraString = jsonExtraStrings.get(j).getAsJsonObject();
                        strings.add(jsonExtraString.get("string").getAsString());
                        posXs.add(jsonExtraString.get("posX").getAsInt());
                        posYs.add(jsonExtraString.get("posY").getAsInt());
                    }
                    listExtraStrings.add(new ComponentExtraStrings(strings, posXs, posYs));                 
                }
                
                // Register the plugin
                PluginMachineRecipe pluginMachineRecipe = new PluginMachineRecipe(rectangle, recipeIdentifier, guiNameCS2, clazzGuiContainer);
                pluginMachineRecipe.setRecipePerPage(recipePerPage);
                pluginMachineRecipe.setUnlocalizedRecipeName(pluginUnlocalizedName);
                pluginMachineRecipe.setGuiBackgroundTextureLocation(pluginBackgroundTextureLocation);
                pluginMachineRecipe.setGuiTextureLocation(pluginGuiTextureLocation);;
                pluginMachineRecipe.setProgressBar(progressBar);
                pluginMachineRecipe.setGuiOffset(componentGuiOffset);
                pluginMachineRecipe.setListInputRecipe(listInputRecipe);
                pluginMachineRecipe.setListOutputRecipe(listOutputRecipe);
                pluginMachineRecipe.setListExtraStrings(listExtraStrings);
                PluginMachineRecipeManager.listPluginMachineRecipe.add(pluginMachineRecipe);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private static ItemStack findItemsStack(String itemName, int stacksize) {
        String[] temp = itemName.split(":");
        if (temp.length == 2)
            return new ItemStack(GameRegistry.findItem(temp[0], temp[1]), stacksize);
        else if (temp.length == 3)
            try {
                int meta = Integer.parseInt(temp[2]);
                return new ItemStack(GameRegistry.findItem(temp[0], temp[1]), stacksize, meta);  
            }
            catch (NumberFormatException e){}
        return null;
    }
    
    private static Class getClass(String clazzPath) {
        Class clazz = null;
        try {
            clazz = Class.forName(clazzPath);
        } catch (ClassNotFoundException e) {}
        return clazz;
    }
    
    private static void removeAllThePlugins() {
        removeCraftingHandlers();
        removeUsageHandlers();
        NEIPluginConfig.resetPluginManagers();
        loadedPlugins.clear();
        disabledPlugins.clear();
        erroredPlugins.clear();
    }
    
    private static void removeCraftingHandlers() {
        Iterator iter = GuiCraftingRecipe.craftinghandlers.iterator();
        while (iter.hasNext()) {
            ICraftingHandler craftingHandler = (ICraftingHandler) iter.next();
            if (craftingHandler instanceof PluginStackInfo || craftingHandler instanceof PluginMachineRecipe)
                iter.remove();
        }
    }
    
    private static void removeUsageHandlers() {
        Iterator iter = GuiUsageRecipe.usagehandlers.iterator();
        while (iter.hasNext()) {
            IUsageHandler usageHandler = (IUsageHandler) iter.next();
            if (usageHandler instanceof PluginStackInfo || usageHandler instanceof PluginMachineRecipe)
                iter.remove();
        }
    }
    
}
