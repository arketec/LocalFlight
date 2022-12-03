package dev.arketec.localflight.configuration;

import java.nio.file.Path;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import org.openjdk.nashorn.internal.runtime.regexp.joni.Regex;

public class ModConfig {
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static ForgeConfigSpec.IntValue range;
    public static ForgeConfigSpec.ConfigValue<String> fuelType;
    public static ForgeConfigSpec.IntValue fuelTime;
    public static ForgeConfigSpec.BooleanValue requiresFuel;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionWhitelist;

    static {

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Config for local flight controller").push("General");

        requiresFuel = builder
                .comment("Determines if Flight Controller needs to burn fuel to work")
                .define("requiresFuel", true);

        fuelType = builder
                .comment("fuel to power FlightController (mod:item)")
                .define("fuelType",
                        "minecraft:nether_star"
                );

        fuelTime = builder
                .comment("The number of ticks a single fuel unit provides (1 seconds = 20 ticks)")
                .defineInRange("fuelTime", 6000 , 20, 72000);

        range = builder
                .comment("Radius of effect (in blocks)")
                .defineInRange("range", 32 , 5, 96);

        dimensionWhitelist = builder
                .comment("dimensions allowed (mod:dimension_name)")
                .defineList("dimensionWhitelist",
                        Arrays.asList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"),
                        e -> Pattern.compile("[a-z]+:[a-z_]+").matcher(e.toString()).matches()
                );


        builder.pop();

        CONFIG_SPEC = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();

        spec.setConfig(configData);
    }
}

