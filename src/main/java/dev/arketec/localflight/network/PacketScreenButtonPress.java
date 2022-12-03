package dev.arketec.localflight.network;

import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.blocks.entity.AbstractModBlockEntity;
import dev.arketec.localflight.blocks.entity.BlockEntityFlightController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PacketScreenButtonPress implements INetworkPacket {

    private BlockPos entityPos;

    public PacketScreenButtonPress(BlockPos pos) {
        entityPos = pos;
    }
    public PacketScreenButtonPress(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }
    @Override
    public boolean handle(Supplier<NetworkEvent.Context> context) {
        final var success = new AtomicBoolean(false);
        context.get().enqueueWork(() -> {
            final BlockEntity entity = context.get().getSender().level.getBlockEntity(entityPos);
            if (entity != null && entity instanceof BlockEntityFlightController t) {
                t.setEnabled(!t.getEnabled());
                success.set(true);
            }
        });
        context.get().setPacketHandled(true);
        return success.get();
    }
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(entityPos);
    }
}
