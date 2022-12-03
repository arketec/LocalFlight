package dev.arketec.localflight.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface INetworkPacket {
    boolean handle(Supplier<NetworkEvent.Context> context);

    void encode(FriendlyByteBuf buffer);

}
