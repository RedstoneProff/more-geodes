package com.github.thedeathlycow.moregeodes.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeodesConfigTest {
    @TempDir
    Path configDir;

    private static void writeConfig(Path dir, String content) throws IOException {
        Files.createDirectories(dir);
        try (FileWriter writer = new FileWriter(dir.resolve("geodes.properties").toFile())) {
            writer.write(content);
        }
    }

    @Test
    void defaultsAreAllTrueWhenFileIsEmpty() throws IOException {
        writeConfig(configDir, "");
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
    }

    @Test
    void explicitFalseDisablesGeode() throws IOException {
        writeConfig(configDir, "generate_emerald_geodes=false\ngenerate_gypsum_patches=false\n");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertFalse(config.generateEmeraldGeodes());
        assertFalse(config.generateGypsumPatches());
        assertTrue(config.generateQuartzGeodes());
        assertTrue(config.generateBismuthGeodes());
    }

    @Test
    void garbageValuesAreFalsePerBooleanParseBooleanSemantics() throws IOException {
        writeConfig(configDir, """
                generate_emerald_geodes=yes
                generate_quartz_geodes=1
                generate_diamond_geodes=TRUE!
                generate_echo_geodes=true
                """);
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertFalse(config.generateEmeraldGeodes());
        assertFalse(config.generateQuartzGeodes());
        assertFalse(config.generateDiamondGeodes());
        assertTrue(config.generateEchoGeodes());
    }

    @Test
    void trueIsCaseInsensitive() throws IOException {
        writeConfig(configDir, "generate_lapis_geodes=True\n");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertTrue(config.generateLapisGeodes());
    }

    @Test
    void unknownKeysAreIgnored() throws IOException {
        writeConfig(configDir, "some_other_mod_key=false\ngenerate_echo_geodes=false\n");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertFalse(config.generateEchoGeodes());
        assertTrue(config.generateEmeraldGeodes());
        assertTrue(config.generateGypsumPatches());
    }

    @Test
    void configReloadPicksUpNewValues() throws IOException {
        writeConfig(configDir, "generate_emerald_geodes=false\n");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);
        assertFalse(config.generateEmeraldGeodes());

        writeConfig(configDir, "generate_emerald_geodes=true\n");
        config.loadConfig(configDir);
        assertTrue(config.generateEmeraldGeodes());
    }

    @Test
    void disablingOneGeodeDoesNotLeakIntoOthers() throws IOException {
        writeConfig(configDir, "generate_diamond_geodes=false\n");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertFalse(config.generateDiamondGeodes());
        assertTrue(config.generateEmeraldGeodes());
        assertTrue(config.generateQuartzGeodes());
        assertTrue(config.generateEchoGeodes());
        assertTrue(config.generateLapisGeodes());
        assertTrue(config.generateGypsumPatches());
        assertTrue(config.generateCertusGeodes());
        assertTrue(config.generateBismuthGeodes());
    }

    @Test
    void allEightDocumentedKeysAreRecognised() throws IOException {
        writeConfig(configDir, """
                generate_emerald_geodes=false
                generate_quartz_geodes=false
                generate_diamond_geodes=false
                generate_echo_geodes=false
                generate_lapis_geodes=false
                generate_gypsum_patches=false
                generate_certus_geodes=false
                generate_bismuth_geodes=false
                """);
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertFalse(config.generateEmeraldGeodes());
        assertFalse(config.generateQuartzGeodes());
        assertFalse(config.generateDiamondGeodes());
        assertFalse(config.generateEchoGeodes());
        assertFalse(config.generateLapisGeodes());
        assertFalse(config.generateGypsumPatches());
        assertFalse(config.generateCertusGeodes());
        assertFalse(config.generateBismuthGeodes());
    }

    @Test
    void loadConfig_reads_an_existing_file_and_merges_defaults() throws IOException {
        Path file = configDir.resolve("geodes.properties");
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write("generate_emerald_geodes=false" + System.lineSeparator());
            writer.write("generate_echo_geodes=TRUE" + System.lineSeparator());
        }
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertFalse(config.generateEmeraldGeodes(), "explicit false in the file must override the default");
        assertTrue(config.generateEchoGeodes(), "Boolean.parseBoolean is case-insensitive for true");
        assertTrue(config.generateQuartzGeodes(), "keys absent from the file keep the default (true)");
    }

    @Test
    void missingConfigDirectoryKeepsDefaultsWithoutCrashing() {
        Path missing = configDir.resolve("does-not-exist");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(missing);

        assertTrue(config.generateEmeraldGeodes());
        assertTrue(config.generateQuartzGeodes());
        assertTrue(config.generateGypsumPatches());
    }

    @Test
    void configFileThatIsADirectoryFallsBackToDefaults() throws IOException {
        Files.createDirectories(configDir.resolve("geodes.properties"));
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);

        assertTrue(config.generateEmeraldGeodes());
        assertTrue(config.generateEchoGeodes());
        assertTrue(config.generateBismuthGeodes());
    }

    @Test
    void whitespaceAroundValuesFollowsPropertiesAndParseBooleanSemantics() throws IOException {
        writeConfig(configDir, "generate_emerald_geodes= true\n");
        GeodesConfig config = new GeodesConfig();
        config.loadConfig(configDir);
        assertTrue(config.generateEmeraldGeodes(), "Properties trims the leading space, so ' true' is true");

        writeConfig(configDir, "generate_emerald_geodes=true \n");
        GeodesConfig config2 = new GeodesConfig();
        config2.loadConfig(configDir);
        assertFalse(config2.generateEmeraldGeodes(), "a trailing space is not 'true' for Boolean.parseBoolean");
    }
}
