package com.github.thedeathlycow.moregeodes.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigFirstRunTest {
    @TempDir
    Path configDir;

    @Test
    void missingConfigFileGeneratesTheDefaultConfig() throws IOException {
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
        assertTrue(Files.isRegularFile(file), "loadConfig must generate the default config file");
        List<String> lines = Files.readAllLines(file);
        assertTrue(lines.stream().anyMatch(l -> l.contains("generate_emerald_geodes=true")),
                "generated file must contain the emerald key");
    }

    @Test
    void generatedDefaultConfigSurvivesAReload() throws IOException {
        GeodesConfig first = new GeodesConfig();
        first.loadConfig(configDir);
        assertTrue(first.generateEmeraldGeodes());

        GeodesConfig second = new GeodesConfig();
        second.loadConfig(configDir);
        assertTrue(second.generateEmeraldGeodes());
        assertTrue(second.generateGypsumPatches());
    }

    @Test
    void userEditsToTheGeneratedFileArePickedUpOnReload() throws IOException {
        new GeodesConfig().loadConfig(configDir);
        Path file = configDir.resolve("geodes.properties");
        Files.writeString(file, "generate_echo_geodes=false\n");

        GeodesConfig reloaded = new GeodesConfig();
        reloaded.loadConfig(configDir);
        assertFalse(reloaded.generateEchoGeodes());
        assertTrue(reloaded.generateEmeraldGeodes());
    }
}
