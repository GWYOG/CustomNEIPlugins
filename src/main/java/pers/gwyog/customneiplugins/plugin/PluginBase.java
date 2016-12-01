package pers.gwyog.customneiplugins.plugin;

import java.awt.Rectangle;

import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import pers.gwyog.customneiplugins.helper.CustomStuff2Helper;

public class PluginBase extends TemplateRecipeHandler {
    private int recipePerPage;
    private String unlocalizedRecipeName;
    private String guiTextureLocation;
    private boolean isCraftingRecipe;
    private boolean isUsageRecipe;
    
    public void setRecipePerPage(int recipePerPage) {
        this.recipePerPage = recipePerPage;
    }
    
    @Override
    public int recipiesPerPage() {
        return this.recipePerPage;
    }
    
    public void setUnlocalizedRecipeName(String unlocalizedRecipeName) {
        this.unlocalizedRecipeName = unlocalizedRecipeName;
    }
    
    public String getUnlocalizedRecipeName() {
        return this.unlocalizedRecipeName;
    }
    
    @Override
    public String getRecipeName() {
        if (this.unlocalizedRecipeName == null)
            return "Null Page";     // This shall never return, but NEI will crash when this is method returns null.
        else
            return I18n.format(this.unlocalizedRecipeName);
    }
    
    public void setGuiTextureLocation(String guiTextureLocation) {
        this.guiTextureLocation = guiTextureLocation;
    }
    
    @Override
    public String getGuiTexture() {
        return this.guiTextureLocation;
    } 
    
    public void setIsCraftingRecipe(boolean isCraftingRecipe) {
        this.isCraftingRecipe = isCraftingRecipe;
    }
    
    public boolean isCraftingRecipe() {
        return isCraftingRecipe;
    }   
    
    public void setIsUsageRecipe(boolean isUsageRecipe) {
        this.isUsageRecipe = isUsageRecipe;
    }
    
    public boolean isUsageRecipe() {
        return isUsageRecipe;
    }
    
}
