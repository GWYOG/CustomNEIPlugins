package pers.gwyog.customneiplugins.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackUtils {
    
    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null)
            return false;
        else
            return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage();
    }
    
    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2, boolean oredict) {
        return oredict? areItemsEquivalent(stack1, stack2): areItemsEqual(stack1, stack2);
    }
    
    // This method is copied and modified from ChinaCraft, because it was written by me as well. XD
    public static boolean areItemsEquivalent(ItemStack target, ItemStack input) {
        if (target == null || input == null) return false;
        int[] targetIDs = OreDictionary.getOreIDs(target);
        int[] inputIDs = OreDictionary.getOreIDs(input);
        if (targetIDs != null && inputIDs != null)
            for (int targetID: targetIDs)
                for (int inputID: inputIDs)
                    if (targetID == inputID)
                        return true;
        return target.isItemEqual(input);
    }
    
    // This method is copied and modified from ChinaCraft, because it was written by me as well. XD
    public static List<ItemStack> getEquivalentItemStacks(ItemStack stack) {
        List<ItemStack> stackList = new ArrayList<ItemStack>();
        int[] oreIDs = OreDictionary.getOreIDs(stack);
        if (oreIDs != null) {
            for (int oreID: oreIDs)
                if (oreID != -1) {
                    List<ItemStack> originStackList = OreDictionary.getOres(oreID);
                    if (originStackList != null && !originStackList.isEmpty())
                        for (ItemStack originStack: originStackList) {
                            ItemStack newStack = originStack.copy();
                            newStack.stackSize = stack.stackSize;
                            stackList.add(newStack);
                        }   
                } 
        }
        if (stackList.isEmpty()) stackList.add(stack);
        return stackList;
    }
    
}
