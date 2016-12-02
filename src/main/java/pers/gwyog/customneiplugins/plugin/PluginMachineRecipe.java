package pers.gwyog.customneiplugins.plugin;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.render.CCRenderState;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cubex2.cs2.gui.GuiAttributes;
import cubex2.cs2.gui.GuiCSContainer;
import cubex2.cs2.gui.GuiCSFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import pers.gwyog.customneiplugins.CustomNEIPlugins;
import pers.gwyog.customneiplugins.helper.CustomStuff2Helper;
import pers.gwyog.customneiplugins.plugin.component.ComponentExtraStrings;
import pers.gwyog.customneiplugins.plugin.component.ComponentGuiOffset;
import pers.gwyog.customneiplugins.plugin.component.ComponentInputStacks;
import pers.gwyog.customneiplugins.plugin.component.ComponentOutputStacks;
import pers.gwyog.customneiplugins.plugin.component.ComponentProgressBar;
import pers.gwyog.customneiplugins.plugin.component.ComponentRectangle;
import pers.gwyog.customneiplugins.util.ItemStackUtils;
import pers.gwyog.customneiplugins.util.StringUtils;

public class PluginMachineRecipe extends PluginBase {
    private List<ComponentInputStacks> listInputRecipe;
    private List<ComponentOutputStacks> listOutputRecipe;
    private List<ComponentExtraStrings> listExtraStrings;
    private Class<? extends GuiContainer> guiContainerClass;
    private ComponentRectangle rectangle;
    // This field is not null if and only if this handler is for CS2's Gui
    private String guiNameCS2;
    private String recipeIdentifier;
    private String guiBackgroundTextureLocation;
    private ComponentProgressBar progressBar;    
    private ComponentGuiOffset guiOffset;
    
    public class CachedMachineRecipe extends CachedRecipe {
        public List<PositionedStack> listInput;
        public List<PositionedStack> listOutput;
        public ComponentExtraStrings extraStrings;
        
        public CachedMachineRecipe(ComponentInputStacks componentInputStacks, ComponentOutputStacks componentOutputStacks, ComponentExtraStrings componentExtraStrings) {
            this.listInput = new ArrayList<PositionedStack>();
            this.listOutput = new ArrayList<PositionedStack>();
            for (int i = 0; i < componentInputStacks.inputs.size(); i++)
                if (componentInputStacks.inputs.get(i) != null) {
                    PositionedStack positionedStack = componentInputStacks.oredicts.get(i)? new PositionedStack(ItemStackUtils.getEquivalentItemStacks(componentInputStacks.inputs.get(i)), componentInputStacks.posX.get(i), componentInputStacks.posY.get(i)): new PositionedStack(componentInputStacks.inputs.get(i), componentInputStacks.posX.get(i), componentInputStacks.posY.get(i)); 
                    listInput.add(positionedStack);               
                }
            for (int i = 0; i < componentOutputStacks.outputs.size(); i++)
                if (componentOutputStacks.outputs.get(i) != null)
                    listOutput.add(new PositionedStack(componentOutputStacks.outputs.get(i), componentOutputStacks.posX.get(i), componentOutputStacks.posY.get(i)));
            this.extraStrings = componentExtraStrings;
        }
        
        @Override
        public List<PositionedStack> getIngredients() {
            for (PositionedStack inputPositionStack: listInput)
                inputPositionStack.setPermutationToRender((cycleticks / 20) % inputPositionStack.items.length);
            listInput.addAll(listOutput);
            return listInput;
        }
        
        @Override
        public PositionedStack getResult() {
            return null;
        }   
    }
    
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(recipeIdentifier))
            for (int i = 0; i < listOutputRecipe.size(); i++)
                arecipes.add(new CachedMachineRecipe(listInputRecipe.get(i), listOutputRecipe.get(i), listExtraStrings.get(i)));
        else
            super.loadCraftingRecipes(outputId, results);
    }
    
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        boolean flag = false;
        for (int i = 0; i < listOutputRecipe.size(); i++) {
            ComponentOutputStacks componentOutputStacks = listOutputRecipe.get(i);
            for (int j = 0; j < componentOutputStacks.outputs.size(); j++)
                if (ItemStackUtils.areItemsEqual(componentOutputStacks.outputs.get(j), result, componentOutputStacks.oredictSearches.get(j))) {
                    flag = true;
                    arecipes.add(new CachedMachineRecipe(listInputRecipe.get(i), componentOutputStacks, listExtraStrings.get(i)));
                }
        }
        if (!flag)
            super.loadCraftingRecipes(result);

    }
    
    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals(recipeIdentifier))
            for (int i = 0; i < listInputRecipe.size(); i++)
                arecipes.add(new CachedMachineRecipe(listInputRecipe.get(i), listOutputRecipe.get(i), listExtraStrings.get(i)));
        else
            super.loadUsageRecipes(inputId, ingredients);
    }
    
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        boolean flag = false;
        for (int i = 0; i < listInputRecipe.size(); i++) {
            ComponentInputStacks componentInputStacks = listInputRecipe.get(i);
            for (int j = 0; j < componentInputStacks.inputs.size(); j++)
                if (ItemStackUtils.areItemsEqual(componentInputStacks.inputs.get(j), ingredient, componentInputStacks.oredicts.get(j))) {
                    flag = true;
                    arecipes.add(new CachedMachineRecipe(componentInputStacks, listOutputRecipe.get(i), listExtraStrings.get(i)));
                }
        }
        if (!flag)
            super.loadUsageRecipes(ingredient);
    }
    
    @Override
    public void drawExtras(int recipe) {
        if (this.progressBar != null)
            drawProgressBar(progressBar.posX, progressBar.posY, progressBar.textureX, progressBar.textureY, progressBar.width, progressBar.height, progressBar.ticks, progressBar.direction);
        CachedMachineRecipe cachedMachineRecipe = (CachedMachineRecipe) arecipes.get(recipe);
        ComponentExtraStrings componentExtraStrings = cachedMachineRecipe.extraStrings;
        for (int i = 0; i < componentExtraStrings.strings.size(); i++)
            if (!componentExtraStrings.strings.get(i).isEmpty()) 
                GuiDraw.drawStringC(StringUtils.parseUnlocalizedString(componentExtraStrings.strings.get(i)), componentExtraStrings.posX.get(i), componentExtraStrings.posY.get(i), 0x000000, false);
   } 
    
    @Override
    public void loadTransferRects() {
        if (this.rectangle != null)
            transferRects.add(new RecipeTransferRect(new Rectangle(rectangle.posX, rectangle.posY, rectangle.width, rectangle.height), this.recipeIdentifier)); 
    }
    
    public void setGuiContainerClass(Class<? extends GuiContainer> guiContainerClass) {
        this.guiContainerClass = guiContainerClass;
    }
    
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return this.guiContainerClass;
    }
    
    public void setGuiNameCS2(String guiNameCS2) {
        this.guiNameCS2 = guiNameCS2;
    }
    
    public void setRecipeIdentifier(String recipeIdentifier) {
        this.recipeIdentifier = recipeIdentifier;
    }
    
    public String getRecipeIdentifier() {
        return recipeIdentifier;
    }
    
    public void setGuiBackgroundTextureLocation(String guiBackgroundTextureLocation) {
        this.guiBackgroundTextureLocation = guiBackgroundTextureLocation;
    }
    
    public String getGuiBackgroundTextureLocation() {
        return guiBackgroundTextureLocation;
    }
    
    public void setRectangle(ComponentRectangle rectangle) {
        this.rectangle = rectangle;
    }
    
    public void setProgressBar(ComponentProgressBar progressBar) {
        this.progressBar = progressBar;
    }
    
    public void setGuiOffset(ComponentGuiOffset guiOffset) {
        this.guiOffset = guiOffset;
    }
    
    public void setListInputRecipe(List<ComponentInputStacks> listInputRecipe) {
        this.listInputRecipe = listInputRecipe;
    }
    
    public void setListOutputRecipe(List<ComponentOutputStacks> listOutputRecipe) {
        this.listOutputRecipe = listOutputRecipe;
    }
    
    public void setListExtraStrings(List<ComponentExtraStrings> listExtraStrings) {
        this.listExtraStrings = listExtraStrings;
    }
    
    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        if (!this.guiBackgroundTextureLocation.equals("nei:textures/gui/recipebg.png") && recipe % recipiesPerPage() == 0) {
            CCRenderState.changeTexture(this.guiBackgroundTextureLocation);
            GuiDraw.drawTexturedModalRect(-5, -16, 0, 0, 176, 166);
        }
        CCRenderState.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, this.guiOffset.offsetX, this.guiOffset.offsetY, 166, 65);
    } 
    
    // Customized TransferRectHandler
    public static class CustomRecipeTransferRectHandler extends TemplateRecipeHandler.RecipeTransferRectHandler {   
        private static HashMap<Class<? extends GuiContainer>, HashSet<RecipeTransferRect>> guiMap = new HashMap<Class<? extends GuiContainer>, HashSet<RecipeTransferRect>>();
        private static HashMap<String, HashSet<RecipeTransferRect>> guiNameCS2Map = new HashMap<String, HashSet<RecipeTransferRect>>(); 
        
        public static void registerRectsToGuis(List<Class<? extends GuiContainer>> classes, List<RecipeTransferRect> rects) {
            if (classes == null)
                return;

            for (Class<? extends GuiContainer> clazz : classes) {
                HashSet<RecipeTransferRect> set = guiMap.get(clazz);
                if (set == null) {
                    set = new HashSet<RecipeTransferRect>();
                    guiMap.put(clazz, set);
                }
                set.addAll(rects);
            }
        }
        
        public static void registerRectsToGuis(String guiNameCS2, LinkedList<RecipeTransferRect> rects) {
            HashSet<RecipeTransferRect> set = guiNameCS2Map.get(guiNameCS2);
            if (set == null) {
                set = new HashSet<RecipeTransferRect>();
                guiNameCS2Map.put(guiNameCS2, set);
            }
            set.addAll(rects);
        }
        
        @Override
        public boolean canHandle(GuiContainer gui) {
            if (CustomNEIPlugins.isCustomStuff2Loaded && gui instanceof GuiCSContainer)
                return guiNameCS2Map.containsKey(getGuiCS2Name(gui));
            else
                return guiMap.containsKey(gui.getClass());
        }   
              
        public String getGuiCS2Name(GuiContainer gui) {
            GuiCSContainer guiCSContainer = (GuiCSContainer) gui;
            Class guiCSContainerClazz = gui.getClass();
            try {
                Field field = guiCSContainerClazz.getDeclaredField("gui");
                field.setAccessible(true);
                GuiAttributes attributes = (GuiAttributes) field.get(guiCSContainer);
                return attributes.name;
            } catch (Exception e) {return "NULL";}
        }
        
        public HashSet<RecipeTransferRect> getRecieTransferRect(GuiContainer gui) {
            if (CustomNEIPlugins.isCustomStuff2Loaded && gui instanceof GuiCSContainer) {
                return guiNameCS2Map.get(getGuiCS2Name(gui));
            }
            else 
                return guiMap.get(gui.getClass());
        }
        
        @Override
        public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {       
            if (!canHandle(gui))
                return false;

            if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
                return transferRect(gui, false);
            else if (keyCode == NEIClientConfig.getKeyBinding("gui.usage"))
                return transferRect(gui, true);

            return false;
        }

        @Override
        public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
            if (!canHandle(gui))
                return false;

            if (button == 0)
                return transferRect(gui, false);
            else if (button == 1)
                return transferRect(gui, true);

            return false;
        }

        private boolean transferRect(GuiContainer gui, boolean usage) {
            int[] offset = RecipeInfo.getGuiOffset(gui);
            try {
                Class clazzTemplateRecipeHandler = Class.forName("codechicken.nei.recipe.TemplateRecipeHandler");
                Method methodTransferRect = clazzTemplateRecipeHandler.getDeclaredMethod("transferRect", GuiContainer.class, Collection.class, int.class, int.class, boolean.class);
                methodTransferRect.setAccessible(true);
                return (Boolean) methodTransferRect.invoke(null, gui, getRecieTransferRect(gui), offset[0], offset[1], usage);
            } catch (Exception e) {return false;}
        }

        @Override
        public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {
        }

        @Override
        public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        }

        @Override
        public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {
        }

        @Override
        public boolean keyTyped(GuiContainer gui, char keyChar, int keyID) {
            return false;
        }

        @Override
        public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
            return false;
        }

        @Override
        public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        }

        @Override
        public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
            if (!canHandle(gui))
                return currenttip;

            if (GuiContainerManager.shouldShowTooltip(gui) && currenttip.size() == 0) {
                int[] offset = RecipeInfo.getGuiOffset(gui);
                try {
                    Class clazzTemplateRecipeHandler = Class.forName("codechicken.nei.recipe.TemplateRecipeHandler");
                    Method methodTransferRect = clazzTemplateRecipeHandler.getDeclaredMethod("transferRectTooltip", GuiContainer.class, Collection.class, int.class, int.class, List.class);
                    methodTransferRect.setAccessible(true);
                    currenttip = (List<String>) methodTransferRect.invoke(null, gui, getRecieTransferRect(gui), offset[0], offset[1], currenttip);
                } catch (Exception e) {}
            }
            return currenttip;
        }

        @Override
        public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
            return currenttip;
        }

        @Override
        public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey, List<String> currenttip) {
            return currenttip;
        }

        @Override
        public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {
        }

    }
    
    static {
        GuiContainerManager.addInputHandler(new CustomRecipeTransferRectHandler());
        GuiContainerManager.addTooltipHandler(new CustomRecipeTransferRectHandler());
    }
    
    // We override this method and let it always return null,
    // since we want to use our CustomRecipeTransferRectHandler,
    // and do not want the origin RecipeTransferRectHandler to work any more.
    @Override
    public List<Class<? extends GuiContainer>> getRecipeTransferRectGuis() {
        return null;
    }
    
    // We use this, and deprecate the above method.
    public List<Class<? extends GuiContainer>> getCustomRecipeTransferRectGuis() {
        Class<? extends GuiContainer> clazz = getGuiClass();
        if (clazz != null) {
            LinkedList<Class<? extends GuiContainer>> list = new LinkedList<Class<? extends GuiContainer>>();
            list.add(clazz);
            return list;
        }
        return null;
    }   
    
    public PluginMachineRecipe(ComponentRectangle rectangle, String recipeIdentifier, String guiNameCS2, Class<? extends GuiContainer> guiContainerClass) {
        this.rectangle = rectangle;
        this.recipeIdentifier = recipeIdentifier;
        this.guiNameCS2 = guiNameCS2;
        this.guiContainerClass = guiContainerClass;
        // We need to run the constructor of the parent class again.
        // Since it was executed before we initialize our plugin.
        loadTransferRects();
        // We use our CustomRecipeTransferRectHandler instead of the origin RecipeTransferRectHandler
        if (this.guiNameCS2 != null)
            CustomRecipeTransferRectHandler.registerRectsToGuis(this.guiNameCS2, transferRects);
        else
            CustomRecipeTransferRectHandler.registerRectsToGuis(getCustomRecipeTransferRectGuis(), transferRects);
    }
    
    @Override
    public TemplateRecipeHandler newInstance() {
        PluginMachineRecipe pluginMachineRecipe = new PluginMachineRecipe(this.rectangle, this.recipeIdentifier, this.guiNameCS2, this.guiContainerClass);
        pluginMachineRecipe.setRecipePerPage(this.recipiesPerPage());
        pluginMachineRecipe.setUnlocalizedRecipeName(this.getUnlocalizedRecipeName());
        pluginMachineRecipe.setGuiBackgroundTextureLocation(this.getGuiBackgroundTextureLocation());
        pluginMachineRecipe.setGuiTextureLocation(this.getGuiTexture());
        pluginMachineRecipe.listInputRecipe = this.listInputRecipe;
        pluginMachineRecipe.listOutputRecipe = this.listOutputRecipe;
        pluginMachineRecipe.listExtraStrings = this.listExtraStrings;
        pluginMachineRecipe.progressBar = this.progressBar;
        pluginMachineRecipe.guiOffset = this.guiOffset;
        return pluginMachineRecipe;
    } 
    
}
