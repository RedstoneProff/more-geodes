package com.github.thedeathlycow.moregeodes.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataResourcesTest {
    private static final String MC_VERSION = System.getProperty("geodes.test.mc");

    static {
        if (MC_VERSION == null || MC_VERSION.isBlank()) {
            throw new IllegalStateException(
                    "System property 'geodes.test.mc' is not set; the shared DataResourcesTest "
                            + "cannot pick per-version expectations. Each target's Gradle test task "
                            + "must set it to its mc version (e.g. -Dgeodes.test.mc=1.21.1).");
        }
    }

    private static boolean isLegacyDataLayout() {
        return switch (MC_VERSION) {
            case "1.20.4", "1.20.6" -> true;
            case "1.21.1", "1.21.4", "26.3" -> false;
            default -> throw new IllegalStateException("Unknown geodes.test.mc version: " + MC_VERSION);
        };
    }

    private enum SilkLootFormat {
        ENCHANTMENT_KEY,
        PREDICATES,
        SILK_CONDITION
    }

    private enum TagLootFormat {
        TAG,
        ITEMS
    }

    private static SilkLootFormat silkLootFormat() {
        return switch (MC_VERSION) {
            case "1.20.6", "1.21.1", "1.21.4" -> SilkLootFormat.PREDICATES;
            case "1.20.4" -> SilkLootFormat.ENCHANTMENT_KEY;
            case "26.3" -> SilkLootFormat.SILK_CONDITION;
            default -> throw new IllegalStateException("Unknown geodes.test.mc version: " + MC_VERSION);
        };
    }

    private static TagLootFormat tagLootFormat() {
        return switch (MC_VERSION) {
            case "1.20.6", "1.21.1", "1.21.4", "26.3" -> TagLootFormat.ITEMS;
            case "1.20.4" -> TagLootFormat.TAG;
            default -> throw new IllegalStateException("Unknown geodes.test.mc version: " + MC_VERSION);
        };
    }

    private static boolean hasItemModelDefinitions() {
        return switch (MC_VERSION) {
            case "1.21.4", "26.3" -> true;
            case "1.20.4", "1.20.6", "1.21.1" -> false;
            default -> throw new IllegalStateException("Unknown geodes.test.mc version: " + MC_VERSION);
        };
    }

    private static final List<Path> RESOURCE_ROOTS = List.of(
            Paths.get("src/main/resources"),
            Paths.get("../../src/main/resources"),
            Paths.get("../../src/versions/" + MC_VERSION + "/resources")
    );

    private static Stream<Path> jsonFiles(Path root) throws IOException {
        if (!Files.isDirectory(root)) {
            return Stream.empty();
        }
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .collect(Collectors.toList())
                    .stream();
        }
    }

    private static List<Path> allJsonFiles() throws IOException {
        List<Path> all = new ArrayList<>();
        for (Path root : RESOURCE_ROOTS) {
            jsonFiles(root).forEach(all::add);
        }
        return all;
    }

    private static JsonElement parse(Path file) throws IOException {
        try {
            return JsonParser.parseString(Files.readString(file));
        } catch (JsonParseException e) {
            throw new AssertionError("Unparsable JSON: " + file, e);
        }
    }

    @Test
    void everyShippedJsonFileParses() throws IOException {
        List<Path> files = allJsonFiles();
        assertFalse(files.isEmpty(), "no resource JSON files found - wrong working directory?");
        for (Path file : files) {
            parse(file);
        }
    }

    @Test
    void dataPackFolderNamesMatchTheVersion() throws IOException {
        List<String> legacy = List.of(
                "recipes", "loot_tables", "advancements", "predicates",
                "tags/blocks", "tags/items"
        );
        List<String> modern = List.of(
                "recipe", "loot_table", "advancement", "predicate",
                "tags/block", "tags/item"
        );
        List<String> forbidden = isLegacyDataLayout() ? modern : legacy;
        for (Path root : RESOURCE_ROOTS) {
            Path data = root.resolve("data");
            if (!Files.isDirectory(data)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(data)) {
                List<String> bad = walk
                        .filter(Files::isDirectory)
                        .map(p -> root.relativize(p).toString().replace('\\', '/'))
                        .filter(rel -> forbidden.stream().anyMatch(
                                l -> rel.equals("data/geodes/" + l) || rel.startsWith("data/geodes/" + l + "/")))
                        .collect(Collectors.toList());
                assertTrue(bad.isEmpty(),
                        (isLegacyDataLayout() ? "1.21+ data folders present on " : "legacy pre-1.21 data folders present on ")
                                + MC_VERSION + ": " + bad);
            }
        }
    }

    @Test
    void tuningRegistryEntriesMatchTheDatapackCodec() throws IOException {
        int found = 0;
        for (Path root : RESOURCE_ROOTS) {
            Path tunings = root.resolve("data/geodes/geodes/tunings");
            if (!Files.isDirectory(tunings)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(tunings)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    JsonObject tuning = parse(file).getAsJsonObject();
                    assertTrue(tuning.get("color").isJsonPrimitive(), "missing color: " + file);
                    JsonObject description = tuning.getAsJsonObject("description");
                    assertTrue(description.has("translate"), "missing description translate: " + file);
                    JsonObject tunedTo = tuning.getAsJsonObject("tuned_to");
                    JsonObject block = tunedTo.getAsJsonObject("block");
                    assertTrue(block.has("blocks") || block.has("tag"),
                            "tuning must be tuned to blocks or a block tag: " + file);
                    found++;
                }
            }
        }
        assertTrue(found >= 7, "expected at least the 7 vanilla tunings, found " + found);
    }

    @Test
    void crystalTuningRecipesMatchTheSerializerCodec() throws IOException {
        String folder = isLegacyDataLayout() ? "recipe" + 's' : "recipe";
        int found = 0;
        for (Path root : RESOURCE_ROOTS) {
            Path recipes = root.resolve("data/geodes/" + folder + "/tuning");
            if (!Files.isDirectory(recipes)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(recipes)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    JsonObject recipe = parse(file).getAsJsonObject();
                    assertEquals("geodes:crystal_tuning", recipe.get("type").getAsString(), file.toString());
                    var ingredients = recipe.getAsJsonArray("ingredients");
                    assertTrue(ingredients.size() >= 1 && ingredients.size() <= 9,
                            "tuning recipe needs 1..9 ingredients: " + file);
                    assertTrue(recipe.has("tuning"), "missing tuning id: " + file);
                    found++;
                }
            }
        }
        assertTrue(found >= 7, "expected at least the 7 vanilla tuning recipes, found " + found);
    }

    @Test
    void lootTablesUseTheVersionItemPredicateFormat() throws IOException {
        String folder = isLegacyDataLayout() ? "loot_table" + 's' : "loot_table";
        int found = 0;
        int silkChecks = 0;
        int tagChecks = 0;
        for (Path root : RESOURCE_ROOTS) {
            Path loot = root.resolve("data/geodes/" + folder);
            if (!Files.isDirectory(loot)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(loot)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    JsonObject table = parse(file).getAsJsonObject();
                    assertEquals("minecraft:block", table.get("type").getAsString(), file.toString());
                    if (!table.has("pools")) {
                        found++;
                        continue;
                    }
                    List<JsonObject> matchToolPredicates = new ArrayList<>();
                    collectMatchToolPredicates(table, matchToolPredicates);
                    for (JsonObject predicate : matchToolPredicates) {
                        if (silkLootFormat() == SilkLootFormat.SILK_CONDITION) {
                            assertFalse(predicate.has("enchantments") || predicate.has("predicates"),
                                    "26.3 ItemPredicate ignores enchantment fields: " + file);
                        } else if (predicate.has("enchantments") || predicate.has("predicates")) {
                            assertSilkPredicateFormat(predicate, file);
                            silkChecks++;
                        }
                        if (predicate.has("tag") || predicate.has("items")) {
                            assertTagPredicateFormat(predicate, file);
                            tagChecks++;
                        }
                    }
                    if (silkLootFormat() == SilkLootFormat.SILK_CONDITION) {
                        silkChecks += countSilkConditions(table);
                    }
                    found++;
                }
            }
        }
        assertTrue(found >= 20, "expected the ~21 block loot tables, found " + found);
        assertTrue(silkChecks >= 15, "expected silk_touch match_tool predicates in the 15 cluster tables, found " + silkChecks);
        assertTrue(tagChecks >= 15, "expected cluster_max_harvestables match_tool predicates in the 15 cluster tables, found " + tagChecks);
    }

    private static void collectMatchToolPredicates(JsonElement element, List<JsonObject> out) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            String name = obj.has("condition") && obj.get("condition").isJsonPrimitive()
                    ? obj.get("condition").getAsString()
                    : obj.has("type") && obj.get("type").isJsonPrimitive()
                            ? obj.get("type").getAsString() : "";
            if ("minecraft:match_tool".equals(name)
                    && obj.has("predicate") && obj.get("predicate").isJsonObject()) {
                out.add(obj.getAsJsonObject("predicate"));
            }
            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                collectMatchToolPredicates(e.getValue(), out);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectMatchToolPredicates(child, out);
            }
        }
    }

    private static int countSilkConditions(JsonElement element) {
        if (element.isJsonObject()) {
            int count = 0;
            for (Map.Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
                if ("condition".equals(e.getKey()) && e.getValue().isJsonPrimitive()
                        && "minecraft:tool/can_silk_touch".equals(e.getValue().getAsString())) {
                    count++;
                } else {
                    count += countSilkConditions(e.getValue());
                }
            }
            return count;
        } else if (element.isJsonArray()) {
            int count = 0;
            for (JsonElement child : element.getAsJsonArray()) {
                count += countSilkConditions(child);
            }
            return count;
        }
        return 0;
    }

    private static void assertSilkPredicateFormat(JsonObject predicate, Path file) {
        switch (silkLootFormat()) {
            case ENCHANTMENT_KEY -> {
                assertTrue(predicate.has("enchantments"),
                        "silk predicate must use the 'enchantments' key on " + MC_VERSION + ": " + file);
                assertFalse(predicate.has("predicates"),
                        "1.21.1-only 'predicates' key on " + MC_VERSION + ": " + file);
                assertFalse(predicate.toString().contains("\"enchantment\": \"minecraft:silk_touch\""),
                        "legacy pre-1.20.5 singular 'enchantment' key in " + file);
            }
            case PREDICATES -> {
                assertTrue(predicate.has("predicates"),
                        "1.21.1 silk predicate must use the 'predicates' key: " + file);
                assertFalse(predicate.has("enchantments"),
                        "legacy 'enchantments' key on 1.21.1: " + file);
            }
            case SILK_CONDITION -> throw new IllegalStateException(
                    "26.3 silk is the shorthand 'minecraft:tool/can_silk_touch' condition, counted separately");
        }
    }

    private static void assertTagPredicateFormat(JsonObject predicate, Path file) {
        switch (tagLootFormat()) {
            case TAG -> {
                assertTrue(predicate.has("tag"),
                        "harvestable predicate must use the 'tag' key on " + MC_VERSION + ": " + file);
                assertFalse(predicate.has("items"),
                        "'items' key on " + MC_VERSION + ": " + file);
            }
            case ITEMS -> {
                assertTrue(predicate.has("items"),
                        "harvestable predicate must use the 'items' key on " + MC_VERSION + ": " + file);
                assertFalse(predicate.has("tag"),
                        "legacy 'tag' key on " + MC_VERSION + ": " + file);
            }
        }
    }

    @Test
    void blockstatesReferenceExistingGeodesModels() throws IOException {
        for (Path root : RESOURCE_ROOTS) {
            Path blockstates = root.resolve("assets/geodes/blockstates");
            if (!Files.isDirectory(blockstates)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(blockstates)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    if (file.getFileName().toString().endsWith("_crystal_echo_locator.json")) {
                        continue;
                    }
                    JsonObject json = parse(file).getAsJsonObject();
                    for (String model : collectModelRefs(json)) {
                        if (model.startsWith("geodes:")) {
                            Path modelFile = root.resolve("assets/geodes/models/" + model.substring("geodes:".length()) + ".json");
                            assertTrue(Files.isRegularFile(modelFile),
                                    "blockstate " + file.getFileName() + " references missing model " + model);
                        }
                    }
                }
            }
        }
    }

    private static List<String> collectModelRefs(JsonElement element) {
        List<String> refs = new ArrayList<>();
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if (entry.getKey().equals("model") && entry.getValue().isJsonPrimitive()) {
                    refs.add(entry.getValue().getAsString());
                } else {
                    refs.addAll(collectModelRefs(entry.getValue()));
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                refs.addAll(collectModelRefs(child));
            }
        }
        return refs;
    }

    private static boolean modelExistsInAnyRoot(String model) throws IOException {
        for (Path root : RESOURCE_ROOTS) {
            if (Files.isRegularFile(root.resolve("assets/geodes/models/" + model.substring("geodes:".length()) + ".json"))) {
                return true;
            }
        }
        return false;
    }

    @Test
    void tuningDescriptionLangKeysExistInEnglish() throws IOException {
        Set<String> keys = new HashSet<>();
        for (Path root : RESOURCE_ROOTS) {
            Path tunings = root.resolve("data/geodes/geodes/tunings");
            if (!Files.isDirectory(tunings)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(tunings)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    JsonObject tuning = parse(file).getAsJsonObject();
                    keys.add(tuning.getAsJsonObject("description").get("translate").getAsString());
                }
            }
        }
        assertFalse(keys.isEmpty());
        JsonObject lang = null;
        for (Path root : RESOURCE_ROOTS) {
            Path langFile = root.resolve("assets/geodes/lang/en_us.json");
            if (Files.isRegularFile(langFile)) {
                lang = parse(langFile).getAsJsonObject();
                break;
            }
        }
        for (String key : keys) {
            assertTrue(lang.has(key), "tuning description key missing from en_us.json: " + key);
        }
    }

    @Test
    void itemModelDefinitionsExistAndReferenceModels() throws IOException {
        if (!hasItemModelDefinitions()) {
            boolean foundLocator = false;
            boolean foundShard = false;
            for (Path root : RESOURCE_ROOTS) {
                if (Files.isRegularFile(root.resolve("assets/geodes/models/item/crystal_locator.json"))) {
                    foundLocator = true;
                }
                if (Files.isRegularFile(root.resolve("assets/geodes/models/item/emerald_shard.json"))) {
                    foundShard = true;
                }
            }
            assertTrue(foundLocator, "pre-1.21.4 targets must ship a classic crystal_locator item model");
            assertTrue(foundShard, "pre-1.21.4 targets must ship a classic emerald_shard item model");
            return;
        }
        int found = 0;
        for (Path root : RESOURCE_ROOTS) {
            Path itemDefs = root.resolve("assets/geodes/items");
            if (!Files.isDirectory(itemDefs)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(itemDefs)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    JsonObject json = parse(file).getAsJsonObject();
                    for (String model : collectModelRefs(json)) {
                        if (model.startsWith("geodes:")) {
                            assertTrue(modelExistsInAnyRoot(model),
                                    "item definition " + file.getFileName() + " references missing model " + model);
                        }
                    }
                    found++;
                }
            }
        }
        assertTrue(found >= 2, "expected item model definitions (locators, shards), found " + found);
        boolean foundLocatorDef = false;
        boolean foundShardDef = false;
        for (Path root : RESOURCE_ROOTS) {
            if (Files.isRegularFile(root.resolve("assets/geodes/items/crystal_locator.json"))) {
                foundLocatorDef = true;
            }
            if (Files.isRegularFile(root.resolve("assets/geodes/items/emerald_shard.json"))) {
                foundShardDef = true;
            }
        }
        assertTrue(foundLocatorDef, "missing item model definition for crystal_locator");
        assertTrue(foundShardDef, "missing item model definition for emerald_shard");
    }

    @Test
    void texturesReferencedByGeodesModelsExist() throws IOException {
        Set<String> textures = new HashSet<>();
        int modelCount = 0;
        for (Path root : RESOURCE_ROOTS) {
            Path models = root.resolve("assets/geodes/models");
            if (!Files.isDirectory(models)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(models)) {
                for (Path file : walk.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                    modelCount++;
                    collectTextureRefs(parse(file).getAsJsonObject(), textures);
                }
            }
        }
        assertTrue(modelCount > 0, "no models found");
        assertFalse(textures.isEmpty(), "no textures referenced by models");
        Set<String> knownUpstreamMissing = Set.of("block/echo_locator/echo_material");
        for (String texture : textures) {
            if (knownUpstreamMissing.contains(texture)) {
                continue;
            }
            boolean exists = false;
            for (Path root : RESOURCE_ROOTS) {
                if (Files.isRegularFile(root.resolve("assets/geodes/textures/" + texture + ".png"))) {
                    exists = true;
                    break;
                }
            }
            assertTrue(exists, "model references missing texture geodes:" + texture);
        }
    }

    private static void collectTextureRefs(JsonElement element, Set<String> refs) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("textures") && obj.get("textures").isJsonObject()) {
                for (Map.Entry<String, JsonElement> e : obj.getAsJsonObject("textures").entrySet()) {
                    String texture = e.getValue().getAsString();
                    if (texture.startsWith("geodes:")) {
                        refs.add(texture.substring("geodes:".length()));
                    }
                }
            }
            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                collectTextureRefs(e.getValue(), refs);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectTextureRefs(child, refs);
            }
        }
    }
}
