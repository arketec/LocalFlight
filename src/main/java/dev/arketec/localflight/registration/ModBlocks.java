package dev.arketec.localflight.registration;

import dev.arketec.localflight.blocks.BlockFlightController;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistryObject<Block> CONTROLLER = register("controller", () ->
            new BlockFlightController()
    );
    public static final void register() {}

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return RegistrationManager.BLOCKS.register(name, block);
    }


    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerBlock(name, block);
        RegistrationManager.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
        return  ret;
    }
}
