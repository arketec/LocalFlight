package dev.arketec.localflight.containers;

import dev.arketec.localflight.blocks.BlockFlightController;
import dev.arketec.localflight.blocks.entity.BlockEntityFlightController;
import dev.arketec.localflight.registration.ModBlocks;
import dev.arketec.localflight.registration.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class ControllerContainer extends AbstractContainerMenu {
    protected BlockPos pos;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    public ControllerContainer(int id, Inventory playerInv) {
        this(id, playerInv, new ItemStackHandler(1), BlockPos.ZERO, new SimpleContainerData(0));
    }
    public ControllerContainer(int id, Inventory playerInv, IItemHandler slots, BlockPos pos, ContainerData data) {
        super(ModContainers.CONTROLLER.get(), id);
        this.pos = pos;
        this.data = data;
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);

        final int slotSizePlus2 = 18, startX = 8, startY = 4, hotBarY = 142, inventoryY = 10;

        addSlot(new SlotItemHandler(slots, 0, startX, inventoryY));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col< 9; col++) {
                addSlot(
                        new Slot(
                                playerInv,
                                9 + row * 9 + col,
                                startX + col * slotSizePlus2,
                                startY + row * slotSizePlus2
                        )
                );
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, startX + col * slotSizePlus2, hotBarY));
        }
    }

    public static MenuConstructor getServerContainer(BlockEntityFlightController entity, BlockPos pos) {
        return (id, playerInv, player) -> new ControllerContainer(id, playerInv, entity.getInventory(), pos, null);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        var stack = ItemStack.EMPTY;
        final Slot slot = getSlot(index);
        if (slot.hasItem()) {
            final ItemStack item = slot.getItem();
            stack = item.copy();

            if (index < 1) {
                if (!moveItemStackTo(item, 1, slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(item, 0, 27, false)){
                return ItemStack.EMPTY;
            }

            if (item.isEmpty() || item.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (item.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            //slot.onTake(player, item);
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.CONTROLLER.get());
    }

    public BlockPos getPos() {
        return pos;
    }

    public ContainerData getData() {
        return data;
    }
}
