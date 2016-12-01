package pers.gwyog.customneiplugins.plugin.component;

import java.util.List;

import net.minecraft.item.ItemStack;

public class ComponentInputStacks {
    public List<ItemStack> inputs;
    public List<Integer> posX;
    public List<Integer> posY;
    
    public ComponentInputStacks(List<ItemStack> inputs, List<Integer> posX, List<Integer> posY) {
        this.inputs = inputs;
        this.posX = posX;
        this.posY = posY;
    }
    
}
