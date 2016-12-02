package pers.gwyog.customneiplugins.plugin.component;

import java.util.List;

import net.minecraft.item.ItemStack;

public class ComponentOutputStacks {
    public List<ItemStack> outputs;
    public List<Boolean> oredictSearches;
    public List<Integer> posX;
    public List<Integer> posY;
    
    public ComponentOutputStacks(List<ItemStack> outputs, List<Boolean> oredictSearches, List<Integer> posX, List<Integer> posY) {
        this.outputs = outputs;
        this.oredictSearches = oredictSearches;
        this.posX = posX;
        this.posY = posY;
    }
    
}
