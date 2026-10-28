package com.github.thedeathlycow.moregeodes.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EchoLocatableTagDataTest {
    private static final Path RESOURCES = Path.of("../../src/versions/1.21.4/resources/data");

    private static JsonObject readTag(String path) throws IOException {
        Path file = RESOURCES.resolve(path);
        assertTrue(Files.exists(file), "tag file must exist: " + path);
        return JsonParser.parseString(Files.readString(file)).getAsJsonObject();
    }

    @Test
    void echoLocatableReferencesBudsClustersAndOres() throws IOException {
        JsonObject tag = readTag("geodes/tags/block/echo_locatable.json");
        List<String> values = tag.getAsJsonArray("values").asList().stream()
                .map(e -> e.getAsString())
                .toList();

        assertEquals(List.of("#c:ores", "#c:buds", "#c:clusters"), values,
                "the echo_locatable tag must reference exactly the three common tags");
        assertFalse(tag.get("replace").getAsBoolean(), "the tag must merge, not replace");
    }

    @Test
    void referencedBudsTagExistsAndListsAllModBuds() throws IOException {
        JsonObject buds = readTag("c/tags/block/buds.json");
        List<String> values = buds.getAsJsonArray("values").asList().stream()
                .map(e -> e.getAsString())
                .toList();

        assertEquals(15, values.size(), "5 gem types x 3 bud sizes");
        assertTrue(values.stream().allMatch(v -> v.startsWith("geodes:")),
                "the shipped buds tag must only contain mod buds");
        assertTrue(values.contains("geodes:small_emerald_bud"));
        assertTrue(values.contains("geodes:large_gypsum_bud"));
        assertTrue(values.contains("geodes:medium_quartz_bud"));
    }

    @Test
    void referencedClustersTagExistsAndListsAllModClusters() throws IOException {
        JsonObject clusters = readTag("c/tags/block/clusters.json");
        List<String> values = clusters.getAsJsonArray("values").asList().stream()
                .map(e -> e.getAsString())
                .toList();

        assertEquals(6, values.size());
        assertTrue(values.contains("geodes:emerald_cluster"));
        assertTrue(values.contains("geodes:diamond_cluster"));
        assertTrue(values.contains("geodes:echo_cluster"));
        assertTrue(values.contains("geodes:quartz_cluster"));
        assertTrue(values.contains("geodes:lapis_cluster"));
        assertTrue(values.contains("geodes:gypsum_rose"));
    }

    @Test
    void cOresTagIsNotShippedByThisMod() {
        assertFalse(Files.exists(RESOURCES.resolve("c/tags/block/ores.json")),
                "c:ores must NOT be shipped by this mod (upstream parity)");
    }
}
