package pers.gwyog.customneiplugins.plugin.component;

import java.util.List;

import net.minecraft.item.ItemStack;

public class ComponentOutputStacks {
    public List<ItemStack> outputs;
    public List<Integer> posX;
    public List<Integer> posY;
    
    public ComponentOutputStacks(List<ItemStack> outputs, List<Integer> posX, List<Integer> posY) {
        this.outputs = outputs;
        this.posX = posX;
        this.posY = posY;
    }
    
}
