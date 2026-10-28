package com.github.thedeathlycow.moregeodes.item.tuning;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningCodecTest {
    private static RegistryOps<JsonElement> registryOps;

    @BeforeAll
    static void bootStrap() {
        TestBootstrap.init();
        registryOps = RegistryOps.create(
                JsonOps.INSTANCE,
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
        );
        for (String gem : new String[]{"bismuth", "citrine", "moonstone", "topaz"}) {
            ((net.minecraft.core.MappedRegistry<net.minecraft.world.level.block.Block>) BuiltInRegistries.BLOCK)
                    .bindTag(TagKey.create(Registries.BLOCK,
                            ResourceLocation.fromNamespaceAndPath("geodes", "crystal_locatable/" + gem)), List.of());
        }
    }

    @Test
    void networkCodec_roundTripsColorAndDescription() {
        Tuning tuning = Tuning.UNTUNED;

        DataResult<JsonElement> encoded = Tuning.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, tuning);
        assertTrue(encoded.result().isPresent(), "network codec should encode a tuning");

        DataResult<Tuning> decoded = Tuning.NETWORK_CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "network codec should decode its own output");
        assertEquals(tuning.getColor(), decoded.result().get().getColor());
        assertEquals(tuning.getDescription(), decoded.result().get().getDescription());
    }

    @Test
    void datapackCodec_parsesMinimalTuningWithoutPredicate() {
        var json = com.google.gson.JsonParser.parseString("""
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
        DataResult<JsonElement> predicate = LocationPredicate.CODEC
                .encodeStart(registryOps, tuning.getIsTunedToBlock());
        assertTrue(predicate.result().isPresent());
        assertEquals(0, predicate.result().get().getAsJsonObject().entrySet().size(),
                "the all-empty predicate must encode to {}");
    }

    @Test
    void datapackCodec_missingColorFails() {
        var json = com.google.gson.JsonParser.parseString("""
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
                "../../src/versions/1.21.4/resources/data/geodes/geodes/tunings/amethyst.json"
        );
        assertTrue(Files.exists(fixture), "the shipped amethyst tuning fixture must exist");
        var json = com.google.gson.JsonParser.parseString(Files.readString(fixture));

        DataResult<Tuning> result = Tuning.DATAPACK_CODEC.parse(registryOps, json);
        assertTrue(result.result().isPresent(),
                "the shipped amethyst tuning must parse with the 1.21.4 codec: " + result.error().map(Object::toString));

        Tuning tuning = result.result().get();
        assertEquals(13144304, tuning.getColor());
        assertTrue(tuning.getDescription().getContents() instanceof TranslatableContents);
        assertEquals("geodes.tunings.geodes.amethyst",
                ((TranslatableContents) tuning.getDescription().getContents()).getKey());
    }

    @Test
    void datapackCodec_blockPredicateAcceptsTagStringForm() {
        var json = com.google.gson.JsonParser.parseString("""
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

        DataResult<JsonElement> encoded = Tuning.DATAPACK_CODEC.encodeStart(registryOps, result.result().get());
        assertTrue(encoded.result().isPresent());
        String encodedString = encoded.result().get().toString();
        assertTrue(encodedString.contains("#geodes:crystal_locatable/bismuth"),
                "the tag form must survive a round trip, got: " + encodedString);
    }

    @Test
    void datapackCodec_legacyTagFieldIsSilentlyIgnored() {
        var json = com.google.gson.JsonParser.parseString("""
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
        DataResult<JsonElement> predicate = LocationPredicate.CODEC
                .encodeStart(registryOps, tuning.getIsTunedToBlock());
        assertTrue(predicate.result().isPresent());
        JsonObject encoded = predicate.result().get().getAsJsonObject();
        JsonObject block = encoded.has("block") ? encoded.getAsJsonObject("block") : new JsonObject();
        assertFalse(block.has("blocks"),
                "the legacy 'tag' key must be silently dropped, leaving a block predicate with no block set");
    }
}
