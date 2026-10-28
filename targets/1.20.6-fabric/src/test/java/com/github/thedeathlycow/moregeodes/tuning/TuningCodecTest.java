package com.github.thedeathlycow.moregeodes.tuning;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.FastColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningCodecTest {
    private static RegistryOps<com.google.gson.JsonElement> registryOps;

    @BeforeAll
    static void bootStrap() {
        TestBootstrap.init();
        registryOps = RegistryOps.create(
                JsonOps.INSTANCE,
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
        );
        for (String gem : new String[]{"bismuth", "citrine", "moonstone", "topaz"}) {
            BuiltInRegistries.BLOCK.getOrCreateTag(TagKey.create(Registries.BLOCK,
                    new ResourceLocation("geodes", "crystal_locatable/" + gem)));
        }
    }

    @Test
    void networkCodec_roundTripsColorAndDescription() {
        Tuning tuning = Tuning.UNTUNED;

        DataResult<com.google.gson.JsonElement> encoded = Tuning.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, tuning);
        assertTrue(encoded.result().isPresent(), "network codec should encode a tuning");

        DataResult<Tuning> decoded = Tuning.NETWORK_CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "network codec should decode its own output");
        assertEquals(tuning.getColor(), decoded.result().get().getColor());
        assertEquals(tuning.getDescription(), decoded.result().get().getDescription());
    }

    @Test
    void datapackCodec_parsesMinimalTuningWithoutPredicate() {
        var json = JsonParser.parseString("""
                {
                    "color": 1,
                    "description": {"translate": "geodes.tunings.untuned"},
                    "tuned_to": {}
                }
                """);

        DataResult<Tuning> result = Tuning.DATAPACK_CODEC.parse(registryOps, json);
        assertTrue(result.result().isPresent(), "an empty tuned_to predicate is valid (the all-empty predicate)");

        Tuning tuning = result.result().get();
        assertEquals(1, tuning.getColor());
        DataResult<com.google.gson.JsonElement> predicate = net.minecraft.advancements.critereon.LocationPredicate.CODEC
                .encodeStart(registryOps, tuning.getIsTunedToBlock());
        assertTrue(predicate.result().isPresent());
        assertEquals(0, predicate.result().get().getAsJsonObject().entrySet().size(),
                "the all-empty predicate must encode to {}");
    }

    @Test
    void datapackCodec_missingColorFails() {
        var json = JsonParser.parseString("""
                {
                    "description": {"translate": "geodes.tunings.untuned"},
                    "tuned_to": {}
                }
                """);

        DataResult<Tuning> result = Tuning.DATAPACK_CODEC.parse(registryOps, json);
        assertTrue(result.error().isPresent(), "a tuning without a color must fail to parse");
    }

    @Test
    void datapackCodec_parsesShippedAmethystTuningFixture() throws IOException {
        Path fixture = Path.of(
                "../../src/versions/1.20.6/resources/data/geodes/geodes/tunings/amethyst.json"
        );
        assertTrue(Files.exists(fixture), "the shipped amethyst tuning fixture must exist");
        var json = JsonParser.parseString(Files.readString(fixture));

        DataResult<Tuning> result = Tuning.DATAPACK_CODEC.parse(registryOps, json);
        assertTrue(result.result().isPresent(),
                "the shipped amethyst tuning must parse with the 1.20.6 codec: " + result.error().map(Object::toString));

        Tuning tuning = result.result().get();
        assertEquals(13144304, tuning.getColor());
        assertTrue(tuning.getDescription().getContents() instanceof TranslatableContents);
        assertEquals("geodes.tunings.geodes.amethyst",
                ((TranslatableContents) tuning.getDescription().getContents()).getKey());
    }

    @Test
    void datapackCodec_blockPredicateAcceptsTagStringForm() {
        var json = JsonParser.parseString("""
                {
                    "color": 8806911,
                    "description": {"translate": "geodes.tunings.spectrum.bismuth"},
                    "tuned_to": {
                        "block": {
                            "blocks": "#geodes:crystal_locatable/bismuth"
                        }
                    }
                }
                """);

        DataResult<Tuning> result = Tuning.DATAPACK_CODEC.parse(registryOps, json);
        assertTrue(result.result().isPresent(),
                "a '#tag' string in tuned_to.block.blocks must parse: " + result.error().map(Object::toString));

        DataResult<com.google.gson.JsonElement> encoded = Tuning.DATAPACK_CODEC.encodeStart(registryOps, result.result().get());
        assertTrue(encoded.result().isPresent());
        String encodedString = encoded.result().get().toString();
        assertTrue(encodedString.contains("#geodes:crystal_locatable/bismuth"),
                "the tag form must survive a round trip, got: " + encodedString);
    }

    @Test
    void datapackCodec_legacyTagFieldIsSilentlyIgnored() {
        var json = JsonParser.parseString("""
                {
                    "color": 8806911,
                    "description": {"translate": "geodes.tunings.spectrum.bismuth"},
                    "tuned_to": {
                        "block": {
                            "tag": "geodes:crystal_locatable/bismuth"
                        }
                    }
                }
                """);

        DataResult<Tuning> result = Tuning.DATAPACK_CODEC.parse(registryOps, json);
        assertTrue(result.result().isPresent(), "the legacy form parses (unknown keys are ignored)");

        Tuning tuning = result.result().get();
        DataResult<com.google.gson.JsonElement> predicate = net.minecraft.advancements.critereon.LocationPredicate.CODEC
                .encodeStart(registryOps, tuning.getIsTunedToBlock());
        assertTrue(predicate.result().isPresent());
        var encoded = predicate.result().get().getAsJsonObject();
        var block = encoded.has("block") ? encoded.getAsJsonObject("block") : new com.google.gson.JsonObject();
        assertFalse(block.has("blocks"),
                "the legacy 'tag' key must be silently dropped, leaving a block predicate with no block set");
    }
}
