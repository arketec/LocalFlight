package dev.arketec.localflight.registration;

import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.client.events.ClientModEvents;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistrationManager {
    public static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = create(ForgeRegistries.CONTAINERS);
    public static final DeferredRegister<MobEffect> EFFECTS = create(ForgeRegistries.MOB_EFFECTS);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = create(ForgeRegistries.BLOCK_ENTITIES);
    public static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        EFFECTS.register(modEventBus);
        CONTAINERS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        ModBlocks.register();
        ModContainers.register();
        ModPackets.register();
        ModTileEntityTypes.register();

    }

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, LocalFlight.MODID);
    }
}
