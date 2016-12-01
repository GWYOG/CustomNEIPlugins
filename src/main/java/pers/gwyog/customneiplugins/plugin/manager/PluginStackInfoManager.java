package pers.gwyog.customneiplugins.plugin.manager;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

public class PluginStackInfoManager {
    public static HashMap<ItemStack, ComponentPluginStackInfo> mapStackToPluginInfo = new HashMap<ItemStack, ComponentPluginStackInfo>();
    
    public static void registerPluginStackInfo(ItemStack stack, ComponentPluginStackInfo componentPluginStackInfo) {
        mapStackToPluginInfo.put(stack, componentPluginStackInfo);
    }
    
    public static class ComponentPluginStackInfo {
        public int recipePerPage;
        public String unlocalizedRecipeName;
        public String guiTextureLocation;
        public String[] unlocalizedDocuments;
        public boolean isCraftingRecipe;
        public boolean isUsageRecipe;
        
        public ComponentPluginStackInfo(int recipePerPage, String unlocalizedRecipeName, String guiTextureLocation, String[] unlocalizedDocuments, boolean isCraftingRecipe, boolean isUsageRecipe) {
            this.recipePerPage = recipePerPage;
            this.unlocalizedRecipeName = unlocalizedRecipeName;
            this.guiTextureLocation = guiTextureLocation;
            this.unlocalizedDocuments = unlocalizedDocuments;
            this.isCraftingRecipe = isCraftingRecipe;
            this.isUsageRecipe = isUsageRecipe;
        }
    }
    
}
