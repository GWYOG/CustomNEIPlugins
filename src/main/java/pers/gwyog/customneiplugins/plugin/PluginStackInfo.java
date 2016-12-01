package pers.gwyog.customneiplugins.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.java.games.util.plugins.Plugins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import pers.gwyog.customneiplugins.config.ModConfig;
import pers.gwyog.customneiplugins.plugin.manager.PluginStackInfoManager;
import pers.gwyog.customneiplugins.plugin.manager.PluginStackInfoManager.ComponentPluginStackInfo;
import pers.gwyog.customneiplugins.util.ItemStackUtils;

public class PluginStackInfo extends PluginBase {
    private String[] unlocalizedDocuments;
    
    public class CachedStackInfoRecipe extends CachedRecipe {
        public PositionedStack positionedStack;
        public String info;
        
        public CachedStackInfoRecipe(ItemStack stack, String info) {
            this.positionedStack = new PositionedStack(stack, ModConfig.pluginSIDisplayItemStackPosX, ModConfig.pluginSIDisplayItemStackPosY);
            this.info = info;
        }
        
        @Override
        public PositionedStack getIngredient() {
            return positionedStack;
        }
        
        @Override
        public PositionedStack getResult() {
            return null;
        }    
    }
    
    public void setUnlocalizedDocuments(String[] unlocalizedDocuments) {
        this.unlocalizedDocuments = unlocalizedDocuments;
    }
    
    @Override
    public void drawExtras(int recipe) {
        CachedStackInfoRecipe stackInfoRecipe = (CachedStackInfoRecipe) arecipes.get(recipe);
        String info = I18n.format(stackInfoRecipe.info);
        List<String> infoList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, ModConfig.pluginSIDisplayStringWidth);
        for (int i = 0; i < infoList.size(); i++)
            GuiDraw.drawString(infoList.get(i), ModConfig.pluginSIDisplayStringPosX, ModConfig.pluginSIDisplayStringPosY + (GuiDraw.fontRenderer.FONT_HEIGHT + ModConfig.pluginSIDisplayStringVerticalIntervalOffset) * i, 0x404040, false);        
    }
    
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (String info: unlocalizedDocuments)
            this.arecipes.add(new CachedStackInfoRecipe(result, info));        
    }
    
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (String info: unlocalizedDocuments)
            this.arecipes.add(new CachedStackInfoRecipe(ingredient, info));   
    }
    
    @Override
    public ICraftingHandler getRecipeHandler(String outputId, Object... results) {
        if (!outputId.equals("item"))
            return this;
        ItemStack result = (ItemStack) results[0];
        for (ItemStack stack: PluginStackInfoManager.mapStackToPluginInfo.keySet()) {
            if (ItemStackUtils.areItemEqual(stack, result)) {
                PluginStackInfo pluginStackInfo = new PluginStackInfo();
                ComponentPluginStackInfo componentPluginStackInfo = PluginStackInfoManager.mapStackToPluginInfo.get(stack);
                pluginStackInfo.setRecipePerPage(componentPluginStackInfo.recipePerPage);
                pluginStackInfo.setUnlocalizedRecipeName(componentPluginStackInfo.unlocalizedRecipeName);
                pluginStackInfo.setGuiTextureLocation(componentPluginStackInfo.guiTextureLocation);
                pluginStackInfo.setUnlocalizedDocuments(componentPluginStackInfo.unlocalizedDocuments);
                if (componentPluginStackInfo.isCraftingRecipe) {
                    pluginStackInfo.loadCraftingRecipes(stack);
                    return pluginStackInfo;
                }
            }
        }
        return this;
    }
    
    @Override
    public IUsageHandler getUsageHandler(String inputId, Object... ingredients) {
        if (!inputId.equals("item"))
            return this;
        ItemStack ingredient = (ItemStack) ingredients[0];
        for (ItemStack stack: PluginStackInfoManager.mapStackToPluginInfo.keySet()) {
            if (ItemStackUtils.areItemEqual(stack, (ItemStack) ingredient)) {
                PluginStackInfo pluginStackInfo = new PluginStackInfo();
                ComponentPluginStackInfo componentPluginStackInfo = PluginStackInfoManager.mapStackToPluginInfo.get(stack);
                pluginStackInfo.setRecipePerPage(componentPluginStackInfo.recipePerPage);
                pluginStackInfo.setUnlocalizedRecipeName(componentPluginStackInfo.unlocalizedRecipeName);
                pluginStackInfo.setGuiTextureLocation(componentPluginStackInfo.guiTextureLocation);
                pluginStackInfo.setUnlocalizedDocuments(componentPluginStackInfo.unlocalizedDocuments);
                if (componentPluginStackInfo.isUsageRecipe) {
                    pluginStackInfo.loadUsageRecipes(stack);
                    return pluginStackInfo;
                }
            }
        }
        return this;
    }

}
