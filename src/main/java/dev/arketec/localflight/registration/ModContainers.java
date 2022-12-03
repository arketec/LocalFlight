package dev.arketec.localflight.registration;

import dev.arketec.localflight.blocks.BlockFlightController;
import dev.arketec.localflight.containers.ControllerContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModContainers {
    public static final RegistryObject<MenuType<ControllerContainer>> CONTROLLER =
            RegistrationManager.CONTAINERS.register("controller", () -> new MenuType<>(ControllerContainer::new));
    public static final void register() {}

}
