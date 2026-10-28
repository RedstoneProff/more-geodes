package com.github.thedeathlycow.moregeodes.item.tuning;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningCodecTest {
    @BeforeAll
    static void bootstrap() {
        com.github.thedeathlycow.moregeodes.TestBootstrap.init();
    }

    private static JsonObject readShippedJson(String path) {
        try (InputStream in = TuningCodecTest.class.getResourceAsStream(path)) {
            assertTrue(in != null, "shipped resource not on the test classpath: " + path);
            return JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new AssertionError("could not read " + path, e);
        }
    }

    @Test
    void networkCodecRoundTripsColorAndDescription() {
        JsonObject json = readShippedJson("/data/geodes/geodes/tunings/amethyst.json");
        Tuning tuning = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        var encoded = Tuning.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, tuning);
        assertTrue(encoded.result().isPresent(), "network encode failed: " + encoded);

        var decoded = Tuning.NETWORK_CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "network decode failed: " + decoded);

        Tuning roundTripped = decoded.result().get();
        assertEquals(tuning.getColor(), roundTripped.getColor());
        assertEquals(tuning.getDescription(), roundTripped.getDescription());
    }

    @Test
    void networkCodecRoundTripsUntuned() {
        Tuning tuning = Tuning.UNTUNED;

        var encoded = Tuning.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, tuning);
        assertTrue(encoded.result().isPresent(), "network encode failed: " + encoded);

        var decoded = Tuning.NETWORK_CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "network decode failed: " + decoded);

        Tuning roundTripped = decoded.result().get();
        assertEquals(tuning.getColor(), roundTripped.getColor());
        assertEquals(tuning.getDescription(), roundTripped.getDescription());
    }

    @Test
    void datapackCodecParsesTheShippedAmethystTuning() {
        JsonObject json = readShippedJson("/data/geodes/geodes/tunings/amethyst.json");

        var decoded = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isPresent(), "shipped amethyst tuning failed to decode: " + decoded);
        Tuning tuning = decoded.result().get();

        assertEquals(13144304, tuning.getColor());
        assertTrue(tuning.getDescription().getContents()
                        instanceof net.minecraft.network.chat.contents.TranslatableContents,
                "description must be a translatable component");
        assertEquals("geodes.tunings.geodes.amethyst",
                ((net.minecraft.network.chat.contents.TranslatableContents) tuning.getDescription().getContents()).getKey(),
                "description must be the translatable tuning key");
        assertTrue(tuning.getIsTunedToBlock() != null, "amethyst tuning must have a tuned_to predicate");
    }

    @Test
    void shippedAmethystTuningPredicateTargetsTheAmethystClusterSet() {
        JsonObject json = readShippedJson("/data/geodes/geodes/tunings/amethyst.json");
        var decoded = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        Tuning tuning = decoded.result().orElseThrow();

        var predicateJson = net.minecraft.advancements.critereon.LocationPredicate.CODEC
                .encodeStart(JsonOps.INSTANCE, tuning.getIsTunedToBlock());
        assertTrue(predicateJson.result().isPresent());
        JsonObject predicate = predicateJson.result().get().getAsJsonObject();
        assertTrue(predicate.has("block"),
                "the amethyst tuning must carry a block test, got: " + predicate);
        JsonObject block = predicate.getAsJsonObject("block");
        assertTrue(block.has("blocks"),
                "the amethyst tuning must be tuned to a block set, got: " + predicate);
        assertTrue(block.get("blocks").getAsJsonArray().toString().contains("minecraft:amethyst_cluster"),
                "the amethyst tuning must include the amethyst cluster");
    }

    @Test
    void datapackCodecParsesTheShippedSpectrumTuningWithTagForm() {
        JsonObject json = readShippedJson("/data/geodes/geodes/tunings/spectrum/bismuth.json");

        var decoded = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isPresent(), "shipped bismuth tuning failed to decode: " + decoded);
        Tuning tuning = decoded.result().get();

        var predicateJson = net.minecraft.advancements.critereon.LocationPredicate.CODEC
                .encodeStart(JsonOps.INSTANCE, tuning.getIsTunedToBlock());
        JsonObject predicate = predicateJson.result().orElseThrow().getAsJsonObject();
        assertTrue(predicate.has("block"),
                "the bismuth tuning must carry a block test, got: " + predicate);
        JsonObject block = predicate.getAsJsonObject("block");
        assertTrue(block.has("tag"),
                "the bismuth tuning must be tuned to a block tag, got: " + predicate);
        assertEquals("geodes:crystal_locatable/bismuth", block.get("tag").getAsString());
    }

    @Test
    void missingColorFailsToDecode() {
        JsonObject json = new JsonObject();
        json.add("description", JsonParser.parseString("{\"translate\":\"x\"}"));
        json.add("tuned_to", JsonParser.parseString("{\"block\":{\"blocks\":[\"minecraft:amethyst_cluster\"]}}"));

        var decoded = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isEmpty(), "a tuning without a color must not decode");
    }

    @Test
    void missingDescriptionFailsToDecode() {
        JsonObject json = new JsonObject();
        json.addProperty("color", 13144304);
        json.add("tuned_to", JsonParser.parseString("{\"block\":{\"blocks\":[\"minecraft:amethyst_cluster\"]}}"));

        var decoded = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isEmpty(), "a tuning without a description must not decode");
    }

    @Test
    void missingPredicateFailsToDecode() {
        JsonObject json = new JsonObject();
        json.addProperty("color", 13144304);
        json.add("description", JsonParser.parseString("{\"translate\":\"x\"}"));

        var decoded = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isEmpty(), "a tuning without tuned_to must not decode");
    }

    @Test
    void untunedHasTheUntunedColorAndNoPredicate() {
        assertEquals(0xff444444, Tuning.UNTUNED.getColor());

        var encoded = Tuning.DATAPACK_CODEC.encodeStart(JsonOps.INSTANCE, Tuning.UNTUNED);
        assertTrue(encoded.result().isPresent());
        JsonObject json = encoded.result().get().getAsJsonObject();
        assertTrue(json.has("tuned_to"), "UNTUNED must still serialize a tuned_to field");
        assertTrue(json.get("tuned_to").getAsJsonObject().entrySet().isEmpty(),
                "UNTUNED's predicate must be the all-empty one, got: " + json.get("tuned_to"));
        assertFalse(Tuning.UNTUNED.getDescription().getString().isEmpty(),
                "UNTUNED must have a description");
    }
}
