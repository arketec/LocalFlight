package dev.arketec.localflight.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public abstract class AbstractModBlockEntity extends BlockEntity implements BlockEntityTicker<AbstractModBlockEntity> {
    public AbstractModBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }


    @Nonnull
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        writePacketNBT(tag);
    }

    @Nonnull
    @Override
    public final CompoundTag getUpdateTag() {
        CompoundTag tag = serializeNBT();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readPacketNBT(tag);
    }

    public void writePacketNBT(CompoundTag cmp) {}

    public void readPacketNBT(CompoundTag cmp) {}

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return ClientboundBlockEntityDataPacket.create(this, (b) -> tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        handleUpdateTag(packet.getTag());
        readPacketNBT(packet.getTag());
    }
}
