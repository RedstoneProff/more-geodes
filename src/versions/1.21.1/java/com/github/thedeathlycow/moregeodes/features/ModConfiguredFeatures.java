package com.github.thedeathlycow.moregeodes.features;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> EMERALD_GEODE = of("emerald_geode");
    public static final ResourceKey<ConfiguredFeature<?, ?>> QUARTZ_GEODE = of("quartz_geode");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND_GEODE = of("diamond_geode");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ECHO_GEODE = of("echo_geode");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAPIS_GEODE = of("lapis_geode");

    public static final ResourceKey<ConfiguredFeature<?, ?>> GYPSUM_PATCH = of("gypsum_patch");


    public static ResourceKey<ConfiguredFeature<?, ?>> of(String id) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, GeodesIds.of(MoreGeodes.MODID, id));
    }

}
