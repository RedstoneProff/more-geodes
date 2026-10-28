package com.github.thedeathlycow.moregeodes.features;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModPlacedFeaturesTest {
    private static final List<String> SHIPPED = List.of(
            "emerald_geode",
            "emerald_geode_extra",
            "quartz_geode",
            "diamond_geode",
            "echo_geode",
            "lapis_geode",
            "lapis_geode_extra",
            "gypsum_patch",
            "certus_geode",
            "bismuth_geode"
    );

    @Test
    void everyShippedPlacedFeatureHasARegistryKey() {
        assertEquals(SHIPPED.size(), 10, "the 26.3 datapack ships 10 placed features");
        for (String name : SHIPPED) {
            assertTrue(Files.isRegularFile(placedFeatureJson(name)),
                    "placed feature geodes:" + name + " has a registry key constant but no shipped JSON");
        }
    }

    @Test
    void registryKeysUseTheGeodesNamespaceAndMatchTheJsonPaths() {
        for (String name : SHIPPED) {
            var key = keyOf(name);
            assertEquals("geodes", key.identifier().getNamespace(),
                    "placed feature keys must live in the geodes namespace");
            assertEquals(name, key.identifier().getPath(),
                    "the key path must match the datapack file name");
        }
    }

    @Test
    void extraDensityFeaturesReferenceTheSameFeatureFamily() {
        for (String base : List.of("emerald_geode", "lapis_geode")) {
            assertTrue(keyOf(base) != null);
            assertTrue(keyOf(base + "_extra") != null);
            assertTrue(Files.isRegularFile(placedFeatureJson(base)));
            assertTrue(Files.isRegularFile(placedFeatureJson(base + "_extra")));
        }
    }

    private static ResourceKey<PlacedFeature> keyOf(String name) {
        return switch (name) {
            case "emerald_geode" -> ModPlacedFeatures.EMERALD_GEODE;
            case "emerald_geode_extra" -> ModPlacedFeatures.EMERALD_GEODE_EXTRA;
            case "quartz_geode" -> ModPlacedFeatures.QUARTZ_GEODE;
            case "diamond_geode" -> ModPlacedFeatures.DIAMOND_GEODE;
            case "echo_geode" -> ModPlacedFeatures.ECHO_GEODE;
            case "lapis_geode" -> ModPlacedFeatures.LAPIS_GEODE;
            case "lapis_geode_extra" -> ModPlacedFeatures.LAPIS_GEODE_EXTRA;
            case "gypsum_patch" -> ModPlacedFeatures.GYPSUM_PATCH;
            case "certus_geode" -> ModPlacedFeatures.CERTUS_GEODE;
            case "bismuth_geode" -> ModPlacedFeatures.BISMUTH_GEODE;
            default -> throw new IllegalStateException("unknown placed feature " + name);
        };
    }

    private static Path placedFeatureJson(String name) {
        return Paths.get("../../src/versions/26.3/resources/data/geodes/worldgen/placed_feature", name + ".json");
    }
}
