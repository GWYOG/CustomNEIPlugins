package pers.gwyog.customneiplugins.util;

import net.minecraft.item.ItemStack;

public class ItemStackUtils {
    
    public static boolean areItemEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null)
            return false;
        else
            return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage();
    }
}
