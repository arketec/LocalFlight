package dev.arketec.localflight.client.events;

import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.client.screen.ControllerScreen;
import dev.arketec.localflight.registration.ModBlocks;
import dev.arketec.localflight.registration.ModContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LocalFlight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ModContainers.CONTROLLER.get(), ControllerScreen::new);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event)
    {
        if (!event.getWorld().isClientSide())
        {
            if (event.getPlacedBlock().getBlock() == ModBlocks.CONTROLLER.get())
            {
                if(!((ServerLevel)event.getWorld()).dimension().equals(DimensionType.OVERWORLD_LOCATION))
                    event.setCanceled(true);
            }
        }
    }
}

