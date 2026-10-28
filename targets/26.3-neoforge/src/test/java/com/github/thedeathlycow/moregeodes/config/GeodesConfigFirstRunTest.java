package com.github.thedeathlycow.moregeodes.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeodesConfigFirstRunTest {
    private static final List<String> ALL_KEYS = List.of(
            "generate_emerald_geodes",
            "generate_quartz_geodes",
            "generate_diamond_geodes",
            "generate_echo_geodes",
            "generate_lapis_geodes",
            "generate_gypsum_patches",
            "generate_certus_geodes",
            "generate_bismuth_geodes"
    );

    @TempDir
    Path configDir;

    @Test
    void missingConfigFileIsGeneratedWithAllTrueDefaults() throws IOException {
        assertTrue(Files.list(configDir).findAny().isEmpty(), "precondition: empty config dir");

        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertTrue(config.generateEmeraldGeodes());
        assertTrue(config.generateQuartzGeodes());
        assertTrue(config.generateDiamondGeodes());
        assertTrue(config.generateEchoGeodes());
        assertTrue(config.generateLapisGeodes());
        assertTrue(config.generateGypsumPatches());
        assertTrue(config.generateCertusGeodes());
        assertTrue(config.generateBismuthGeodes());

        Path file = configDir.resolve("geodes.properties");
        assertTrue(Files.isRegularFile(file), "the first run must generate geodes.properties");
        String content = Files.readString(file);
        for (String key : ALL_KEYS) {
            assertTrue(content.contains(key + "=true"),
                    "generated default config must contain " + key + "=true, got: " + content);
        }
    }

    @Test
    void generatedDefaultFileRoundTripsThroughAFreshLoad() throws IOException {
        GeodesConfig first = new GeodesConfig();
        first.loadConfig(configDir);
        assertTrue(Files.isRegularFile(configDir.resolve("geodes.properties")));

        GeodesConfig second = new GeodesConfig();
        second.loadConfig(configDir);
        for (String key : ALL_KEYS) {
            assertTrue(readByName(second, key), key + " must stay enabled after a config round trip");
        }
    }

    @Test
    void userEditedGeneratedFileIsRespectedOnReload() throws IOException {
        new GeodesConfig().loadConfig(configDir);
        Path file = configDir.resolve("geodes.properties");

        String generated = Files.readString(file);
        String edited = generated.replace("generate_quartz_geodes=true", "generate_quartz_geodes=false");
        assertTrue(!edited.equals(generated), "the generated file must contain generate_quartz_geodes=true");
        Files.writeString(file, edited);

        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);
        assertFalse(config.generateQuartzGeodes(), "the user's false must survive a restart");
        assertTrue(config.generateEmeraldGeodes(), "untouched keys keep the default");
        assertTrue(config.generateGypsumPatches(), "untouched keys keep the default");
    }

    private static boolean readByName(GeodesConfig config, String key) {
        return switch (key) {
            case "generate_emerald_geodes" -> config.generateEmeraldGeodes();
            case "generate_quartz_geodes" -> config.generateQuartzGeodes();
            case "generate_diamond_geodes" -> config.generateDiamondGeodes();
            case "generate_echo_geodes" -> config.generateEchoGeodes();
            case "generate_lapis_geodes" -> config.generateLapisGeodes();
            case "generate_gypsum_patches" -> config.generateGypsumPatches();
            case "generate_certus_geodes" -> config.generateCertusGeodes();
            case "generate_bismuth_geodes" -> config.generateBismuthGeodes();
            default -> throw new IllegalStateException("unknown key " + key);
        };
    }
}
