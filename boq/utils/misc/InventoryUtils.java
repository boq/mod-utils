package boq.utils.misc;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class InventoryUtils {
    public static boolean canMergeStacks(ItemStack stackA, ItemStack stackB) {
        return stackA.itemID == stackB.itemID &&
                stackA.getItemDamage() == stackA.getItemDamage() &&
                stackA.stackSize + stackB.stackSize <= stackA.getMaxStackSize() &&
                ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean canInsertItemToInventory(IInventory inventory, ItemStack stack, int slot) {
        return inventory.isItemValidForSlot(slot, stack);
    }

    public static boolean canInsertItemToInventory(ISidedInventory inventory, ItemStack stack, int slot, ForgeDirection side) {
        return inventory.canInsertItem(slot, stack, side.ordinal()) &&
                inventory.isItemValidForSlot(slot, stack);
    }

    private static ItemStack insertStackIntoSlot(IInventory inventory, ItemStack stack, int slot) {
        ItemStack current = inventory.getStackInSlot(slot);

        boolean updated = false;

        if (current == null) {
            inventory.setInventorySlotContents(slot, stack);
            stack = null;
            updated = true;
        } else if (canMergeStacks(stack, current)) {
            int remaining = stack.getMaxStackSize() - current.stackSize;
            int used = Math.min(stack.stackSize, remaining);
            current.stackSize += used;
            stack.stackSize -= used;
            updated = used > 0;

            if (stack.stackSize <= 0)
                stack = null;
        }

        if (updated)
            inventory.onInventoryChanged();

        return stack;
    }

    public static ItemStack insertStack(ISidedInventory inventory, ItemStack stack, ForgeDirection side) {
        if (stack == null || stack.stackSize <= 0)
            return null;

        int[] slots = inventory.getAccessibleSlotsFromSide(side.ordinal());

        for (int slot : slots) {
            if (canInsertItemToInventory(inventory, stack, slot, side))
                stack = insertStackIntoSlot(inventory, stack, slot);

            if (stack.stackSize <= 0)
                return null;
        }

        return stack;
    }

    public static ItemStack insertStack(IInventory inventory, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0)
            return null;

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (canInsertItemToInventory(inventory, stack, slot))
                stack = insertStackIntoSlot(inventory, stack, slot);

            if (stack == null || stack.stackSize <= 0)
                return null;
        }

        return stack;
    }

    public static ItemStack insertStack(IInventory inventory, ItemStack stack, ForgeDirection side) {
        if (inventory instanceof ISidedInventory && side != ForgeDirection.UNKNOWN)
            return insertStack((ISidedInventory)inventory, stack, side);

        return insertStack(inventory, stack);
    }
}
