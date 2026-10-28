package com.github.thedeathlycow.moregeodes.item.tuning;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningCodecTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    private static final String JSON = """
            {
              "color": 16744448,
              "description": {"translate": "geodes.tunings.emerald"},
              "tuned_to": {"blocks": ["geodes:emerald_crystal_block"]}
            }
            """;

    @Test
    void datapackCodecParsesATuningEntry() {
        Tuning tuning = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(JSON))
                .getOrThrow();

        assertEquals(16744448, tuning.getColor());
        assertEquals(Component.translatable("geodes.tunings.emerald").getString(),
                tuning.getDescription().getString());
    }

    @Test
    void datapackCodecRoundTrips() {
        Tuning parsed = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(JSON))
                .getOrThrow();
        var encoded = Tuning.DATAPACK_CODEC.encodeStart(JsonOps.INSTANCE, parsed).getOrThrow();
        Tuning reparsed = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(parsed.getColor(), reparsed.getColor());
        assertEquals(parsed.getDescription(), reparsed.getDescription());
    }

    @Test
    void networkCodecCarriesColorAndDescriptionOnly() {
        Tuning parsed = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(JSON))
                .getOrThrow();

        var encoded = Tuning.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, parsed).getOrThrow();
        assertTrue(encoded.isJsonObject());
        assertTrue(encoded.getAsJsonObject().has("color"));
        assertTrue(encoded.getAsJsonObject().has("description"));
        assertTrue(!encoded.getAsJsonObject().has("tuned_to"));

        Tuning decoded = Tuning.NETWORK_CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();
        assertEquals(parsed.getColor(), decoded.getColor());
        assertEquals(parsed.getDescription(), decoded.getDescription());
    }

    @Test
    void untunedHasTheGrayDescriptionAndADarkColor() {
        assertEquals(ARGB.color(0xff, 0x44, 0x44, 0x44), Tuning.UNTUNED.getColor());
        assertEquals(Component.translatable("geodes.tunings.untuned").withStyle(ChatFormatting.GRAY),
                Tuning.UNTUNED.getDescription());
    }

    @Test
    void datapackCodecRejectsEntriesWithoutATunedToPredicate() {
        var result = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("""
                {"color": 1, "description": {"translate": "x"}}
                """));
        assertTrue(result.isError(), "a tuning without tuned_to must not parse");
    }

    @Test
    void twoTuningsWithDifferentColorsAreNotEqual() {
        Tuning a = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(JSON)).getOrThrow();
        Tuning b = new Tuning(1, a.getDescription(), a.getIsTunedToBlock());
        assertNotEquals(a.getColor(), b.getColor());
    }
}
