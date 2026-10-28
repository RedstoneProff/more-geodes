package com.github.thedeathlycow.moregeodes.neoforge.world;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeodesBiomeModifiersTest {
    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
    }

    private static final TagKey<Biome> EMERALD_TAG = TagKey.create(Registries.BIOME,
            new ResourceLocation(MoreGeodes.MODID, "has_emerald_geode"));

    private static HolderSet<Biome> biomesSet(boolean contains) {
        HolderSet<Biome> set = mock(HolderSet.class);
        when(set.contains(any())).thenReturn(contains);
        return set;
    }

    private static BiomeModifier modifier(HolderSet<Biome> biomes, String configKey, Optional<String> requiresMod)
            throws Exception {
        Class<?> recordClass = Class.forName(
                "com.github.thedeathlycow.moregeodes.neoforge.world.GeodesBiomeModifiers$ConfigAddFeatures");
        Constructor<?> ctor = recordClass.getDeclaredConstructor(
                HolderSet.class, Holder.class, GenerationStep.Decoration.class, String.class, Optional.class);
        ctor.setAccessible(true);
        return (BiomeModifier) ctor.newInstance(
                biomes,
                mock(Holder.class),
                GenerationStep.Decoration.UNDERGROUND_DECORATION,
                configKey,
                requiresMod
        );
    }

    private static Properties configProperties() throws Exception {
        java.lang.reflect.Field field = MoreGeodes.CONFIG.getClass().getDeclaredField("properties");
        field.setAccessible(true);
        return (Properties) field.get(MoreGeodes.CONFIG);
    }

    private static Holder<Biome> biomeInTag(TagKey<Biome> tag) {
        Holder<Biome> biome = mock(Holder.class);
        when(biome.is(tag)).thenReturn(true);
        return biome;
    }

    private static ModifiableBiomeInfo.BiomeInfo defaultInfo() {
        var climate = new Biome.ClimateSettings(true, 0.5F, Biome.TemperatureModifier.NONE, 0.5F);
        var effects = new BiomeSpecialEffects.Builder()
                .grassColorOverride(0x000000).foliageColorOverride(0x000000).waterColor(0x000000).waterFogColor(0x000000)
                .fogColor(0x000000).skyColor(0x000000).build();
        var generation = new BiomeGenerationSettings.PlainBuilder().build();
        var spawns = new MobSpawnSettings.Builder().build();
        return new ModifiableBiomeInfo.BiomeInfo(climate, effects, generation, spawns);
    }

    private static ModifiableBiomeInfo.BiomeInfo.Builder modify(BiomeModifier modifier, Holder<Biome> biome,
                                                                BiomeModifier.Phase phase) {
        var builder = ModifiableBiomeInfo.BiomeInfo.Builder.copyOf(defaultInfo());
        modifier.modify(biome, phase, builder);
        return builder;
    }

    @Test
    void modifierAddsFeatureWhenConfigEnabled() throws Exception {
        BiomeModifier modifier = modifier(biomesSet(true), "generate_emerald_geodes", Optional.empty());
        var added = modify(modifier, biomeInTag(EMERALD_TAG), BiomeModifier.Phase.ADD)
                .getGenerationSettings()
                .getFeatures(GenerationStep.Decoration.UNDERGROUND_DECORATION);
        assertEquals(1, added.size(), "the emerald geode feature must be added to the selected biome");
    }

    @Test
    void modifierSkipsFeatureWhenConfigDisabled() throws Exception {
        Properties properties = configProperties();
        String old = properties.getProperty("generate_emerald_geodes");
        properties.setProperty("generate_emerald_geodes", "false");
        try {
            BiomeModifier modifier = modifier(biomesSet(true), "generate_emerald_geodes", Optional.empty());
            var added = modify(modifier, biomeInTag(EMERALD_TAG), BiomeModifier.Phase.ADD)
                    .getGenerationSettings()
                    .getFeatures(GenerationStep.Decoration.UNDERGROUND_DECORATION);
            assertTrue(added.isEmpty(), "a disabled geode must not be added to the biome");
        } finally {
            properties.setProperty("generate_emerald_geodes", old);
        }
    }

    @Test
    void modifierRequiresModWhenKeyPresent() throws Exception {
        BiomeModifier modifier = modifier(biomesSet(true), "generate_certus_geodes", Optional.of("ae2"));
        var added = modify(modifier, biomeInTag(EMERALD_TAG), BiomeModifier.Phase.ADD)
                .getGenerationSettings()
                .getFeatures(GenerationStep.Decoration.UNDERGROUND_DECORATION);
        assertTrue(added.isEmpty(), "a modifier whose required mod is absent must not add its feature");
    }

    @Test
    void modifierIgnoresBiomesOutsideItsSet() throws Exception {
        BiomeModifier modifier = modifier(biomesSet(false), "generate_emerald_geodes", Optional.empty());
        Holder<Biome> otherBiome = mock(Holder.class);
        when(otherBiome.is(EMERALD_TAG)).thenReturn(false);
        var added = modify(modifier, otherBiome, BiomeModifier.Phase.ADD)
                .getGenerationSettings()
                .getFeatures(GenerationStep.Decoration.UNDERGROUND_DECORATION);
        assertTrue(added.isEmpty(), "an untagged biome must not receive the feature");
    }

    @Test
    void modifierIgnoresNonAddPhase() throws Exception {
        BiomeModifier modifier = modifier(biomesSet(true), "generate_emerald_geodes", Optional.empty());
        var added = modify(modifier, biomeInTag(EMERALD_TAG), BiomeModifier.Phase.REMOVE)
                .getGenerationSettings()
                .getFeatures(GenerationStep.Decoration.UNDERGROUND_DECORATION);
        assertTrue(added.isEmpty(), "only the ADD phase may add features");
    }
}
