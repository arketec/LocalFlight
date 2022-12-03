package dev.arketec.localflight.registration;

import dev.arketec.localflight.blocks.entity.BlockEntityFlightController;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntityTypes {

    public static final RegistryObject<BlockEntityType<BlockEntityFlightController>> CONTROLLER = register("controller", BlockEntityFlightController::new, ModBlocks.CONTROLLER);
    static void register() {}

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, RegistryObject<? extends Block> block) {
        return RegistrationManager.TILE_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }
}
