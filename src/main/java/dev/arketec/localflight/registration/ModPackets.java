package dev.arketec.localflight.registration;

import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.network.PacketScreenButtonPress;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LocalFlight.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(PacketScreenButtonPress.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PacketScreenButtonPress::encode)
                .decoder(PacketScreenButtonPress::new)
                .consumer(PacketScreenButtonPress::handle)
                .add();
        //INSTANCE.messageBuilder(null, id++, NetworkDirection.PLAY_TO_CLIENT);
    };
}
