package dev.arketec.localflight;

import dev.arketec.localflight.configuration.ModConfig;
import dev.arketec.localflight.registration.RegistrationManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(LocalFlight.MODID)
public class LocalFlight {
    public static final String MODID = "localflight";

    public  LocalFlight() {
        ModConfig.loadConfig(ModConfig.CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve("local-flight.toml"));
        RegistrationManager.register();
    }
}
