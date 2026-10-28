package com.github.thedeathlycow.moregeodes;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.blocks.GeodesBlockProperties;
import com.github.thedeathlycow.moregeodes.blocks.GeodesPushReactions;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.github.thedeathlycow.moregeodes.features.ModPlacedFeatures;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import com.github.thedeathlycow.moregeodes.sounds.CrystalBlockSoundGroup;
import com.github.thedeathlycow.moregeodes.sounds.ModBlockSoundGroups;
import com.github.thedeathlycow.moregeodes.state.property.ModProperties;
import com.github.thedeathlycow.moregeodes.tag.GeodesGameEventTags;
import com.github.thedeathlycow.moregeodes.tag.ModBiomeTags;
import com.github.thedeathlycow.moregeodes.tag.ModBlockTags;
import com.github.thedeathlycow.moregeodes.tag.ModItemTags;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import com.github.thedeathlycow.moregeodes.util.SilkTouchHelper;
import com.github.thedeathlycow.moregeodes.world.GeodesGameEvents;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistryBootstrapTest {
    @TempDir
    static Path configDir;

    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
        forceLoad(List.of(
                ModBlockTags.class, ModItemTags.class, ModBiomeTags.class,
                GeodesGameEventTags.class, ModPlacedFeatures.class, Tunings.class,
                ModProperties.class, GeodesBlockProperties.class,
                GeodesPushReactions.class, ModBlockSoundGroups.class,
                CrystalBlockSoundGroup.class, GeodesIds.class, SilkTouchHelper.class,
                GeodesMemoryModules.class, GeodesEntityTypes.class, GeodesGameEvents.class
        ));
    }

    private static void forceLoad(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            Class.forName(clazz.getName());
        }
    }

    private static void unfreezeVanillaRegistries() throws Exception {
        java.lang.reflect.Field frozenField = net.minecraft.core.MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        for (var registry : BuiltInRegistries.REGISTRY) {
            if (registry instanceof net.minecraft.core.MappedRegistry<?> mapped) {
                frozenField.setBoolean(mapped, false);
            }
        }
    }

    private static Block block(String name) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(MoreGeodes.MODID, name));
    }

    private static Item item(String name) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(MoreGeodes.MODID, name));
    }

    @Test
    void emeraldGeodeBlocksAreRegistered() {
        assertNotNull(block("emerald_crystal_block"));
        assertNotNull(block("budding_emerald"));
        assertNotNull(block("emerald_cluster"));
        assertNotNull(block("small_emerald_bud"));
        assertNotNull(block("medium_emerald_bud"));
        assertNotNull(block("large_emerald_bud"));
    }

    @Test
    void quartzGeodeBlocksAreRegistered() {
        assertNotNull(block("quartz_crystal_block"));
        assertNotNull(block("budding_quartz"));
        assertNotNull(block("quartz_cluster"));
        assertNotNull(block("small_quartz_bud"));
        assertNotNull(block("medium_quartz_bud"));
        assertNotNull(block("large_quartz_bud"));
    }

    @Test
    void diamondEchoLapisGypsumBlocksAreRegistered() {
        assertNotNull(block("diamond_cluster"));
        assertNotNull(block("budding_echo"));
        assertNotNull(block("echo_cluster"));
        assertNotNull(block("budding_lapis"));
        assertNotNull(block("lapis_cluster"));
        assertNotNull(block("budding_gypsum"));
        assertNotNull(block("gypsum_rose"));
        assertNotNull(block("gypsum_cluster"));
    }

    @Test
    void gypsumDecorativeSetIsRegistered() {
        assertNotNull(block("chiseled_gypsum"));
        assertNotNull(block("gypsum_pillar"));
        assertNotNull(block("polished_gypsum"));
        assertNotNull(block("smooth_gypsum"));
        assertNotNull(block("gypsum_stairs"));
        assertNotNull(block("gypsum_slab"));
        assertNotNull(block("gypsum_wall"));
        assertNotNull(block("pyrite"));
        assertNotNull(block("pyrite_stairs"));
        assertNotNull(block("pyrite_slab"));
        assertNotNull(block("pyrite_wall"));
        assertNotNull(block("calcite_stairs"));
        assertNotNull(block("calcite_slab"));
        assertNotNull(block("calcite_wall"));
    }

    @Test
    void itemsAreRegistered() {
        assertNotNull(item("emerald_shard"));
        assertNotNull(item("quartz_shard"));
        assertNotNull(item("echo_shard"));
        assertNotNull(item("lapis_shard"));
        assertNotNull(item("gypsum_shard"));
        assertNotNull(item("pyrite_chunk"));
        assertNotNull(item("crystal_locator"));
        assertNotNull(item("tuned_crystal_locator"));
        assertNotNull(item("echo_locator"));
    }

    @Test
    void soundEventsAreRegistered() {
        assertTrue(BuiltInRegistries.SOUND_EVENT.keySet().stream().anyMatch(
                id -> id.getNamespace().equals(MoreGeodes.MODID)),
                "no geodes sound events registered");
    }

    @Test
    void gameEventsAreRegistered() {
        assertTrue(BuiltInRegistries.GAME_EVENT.keySet().stream().anyMatch(
                id -> id.getNamespace().equals(MoreGeodes.MODID)),
                "no geodes game events registered");
    }

    @Test
    void memoryModulesAreRegistered() {
        assertTrue(BuiltInRegistries.MEMORY_MODULE_TYPE.keySet().stream().anyMatch(
                id -> id.getNamespace().equals(MoreGeodes.MODID)),
                "no geodes memory modules registered");
    }

    @Test
    void recipeSerializersAreRegistered() {
        assertTrue(BuiltInRegistries.RECIPE_SERIALIZER.keySet().stream().anyMatch(
                id -> id.getNamespace().equals(MoreGeodes.MODID)),
                "no geodes recipe serializers registered");
    }

    @Test
    void entityTypesAreRegistered() {
        assertTrue(BuiltInRegistries.ENTITY_TYPE.keySet().stream().anyMatch(
                id -> id.getNamespace().equals(MoreGeodes.MODID)),
                "no geodes entity types registered");
    }

    @Test
    void untunedTuningConstantIsWellFormed() {
        assertNotNull(Tuning.UNTUNED);
        assertNotNull(Tuning.UNTUNED.getColor());
        assertNotNull(Tuning.UNTUNED.getDescription());
        assertNotNull(Tuning.UNTUNED.getIsTunedToBlock());
    }

    @Test
    void tuningsRegistryKeyUsesTheModNamespace() {
        assertEquals(MoreGeodes.MODID, Tunings.REGISTRY_KEY.location().getNamespace());
        assertEquals("tunings", Tunings.REGISTRY_KEY.location().getPath());
    }

    @Test
    void configuredAndPlacedFeatureKeysUseTheModNamespace() {
        for (var key : List.of(
                com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures.EMERALD_GEODE,
                com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures.QUARTZ_GEODE,
                com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures.DIAMOND_GEODE,
                com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures.ECHO_GEODE,
                com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures.LAPIS_GEODE,
                com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures.GYPSUM_PATCH)) {
            assertEquals(MoreGeodes.MODID, key.location().getNamespace());
            assertEquals(net.minecraft.core.registries.Registries.CONFIGURED_FEATURE.location(), key.registry());
        }
        for (var key : List.of(
                ModPlacedFeatures.EMERALD_GEODE, ModPlacedFeatures.QUARTZ_GEODE,
                ModPlacedFeatures.DIAMOND_GEODE, ModPlacedFeatures.ECHO_GEODE,
                ModPlacedFeatures.LAPIS_GEODE, ModPlacedFeatures.GYPSUM_PATCH)) {
            assertEquals(MoreGeodes.MODID, key.location().getNamespace());
            assertEquals(net.minecraft.core.registries.Registries.PLACED_FEATURE.location(), key.registry());
        }
    }
}
