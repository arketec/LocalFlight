package dev.arketec.localflight.client.events;

import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.client.screen.ControllerScreen;
import dev.arketec.localflight.configuration.ModConfig;
import dev.arketec.localflight.registration.ModBlocks;
import dev.arketec.localflight.registration.ModContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = LocalFlight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    @SubscribeEvent
    public static  void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getPlacedBlock().getBlock() == ModBlocks.CONTROLLER.get()) {
                var dim = ((ServerLevel) event.getWorld()).dimension().location().toString();
                var allowed = ModConfig.dimensionWhitelist.get();
                if (!allowed.stream().anyMatch(s -> s.equals(dim))) {
                    if (event.getEntity() instanceof ServerPlayer player)
                        player.displayClientMessage(new TextComponent("Cannot place Flight Controller in this dimension"), false);
                    event.setCanceled(true);
                }
            }
        }
    }
}
