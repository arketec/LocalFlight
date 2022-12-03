package dev.arketec.localflight.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class AbstractInventoryBlockEntity extends AbstractModBlockEntity {
    protected final int size;
    protected int timer;
    protected boolean dirty;

    protected final ItemStackHandler inventory;
    protected LazyOptional<ItemStackHandler> handler;

    public AbstractInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int size) {
        super(type, pos, blockState);
        if (size < 1) {
            this.size = 1;
        } else {
            this.size = size;
        }
        this.inventory = createInventory();
        this.handler = LazyOptional.of(() -> this.inventory);
    }

    private ItemStackHandler createInventory() {
        return new ItemStackHandler(size) {
            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                AbstractInventoryBlockEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!canInsert(stack)) return stack;
                AbstractInventoryBlockEntity.this.update();
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    protected abstract boolean canInsert(ItemStack item);

    public void tick() {
        timer++;
        if (dirty && level != null) {
            update();
            dirty = false;
        }
    }
    public void update() {
        requestModelDataUpdate();
        setChanged();
        if (level != null) {
            level.setBlockAndUpdate(worldPosition, getBlockState());
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast(): super.getCapability(cap, side);
    }

    public ItemStack extractItem(int slot) {
        final int count = getItemInSlot(slot).getCount();
        dirty = true;
        return handler.map(i -> i.extractItem(slot, count, false)).orElse(ItemStack.EMPTY);
    }

    public ItemStack insertItem(int slot, ItemStack stack) {
        if (!canInsert(stack)) return stack;
        ItemStack copy = stack.copy();
        stack.shrink(copy.getCount());
        dirty = true;
        return handler.map(i -> i.insertItem(slot, copy, false)).orElse(ItemStack.EMPTY);
    }
    public ItemStack getItemInSlot(int slot) {
        return handler.map(i -> i.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    @Nonnull
    @Override
    public void readPacketNBT(CompoundTag tag) {
        inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Nonnull
    @Override
    public void writePacketNBT(CompoundTag tag) {
        tag.put("Inventory", this.inventory.serializeNBT());
    }



    public LazyOptional<ItemStackHandler> getHandler() {
        return handler;
    }
    public int getSize() {
        return size;
    }
    public ItemStackHandler getInventory() {
        return inventory;
    }
}
