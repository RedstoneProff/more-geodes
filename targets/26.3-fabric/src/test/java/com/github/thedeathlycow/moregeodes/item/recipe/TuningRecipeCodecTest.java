package com.github.thedeathlycow.moregeodes.item.recipe;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningRecipeCodecTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    private static final String RECIPE_JSON = """
            {
              "type": "geodes:crystal_tuning",
              "category": "misc",
              "ingredients": [
                "geodes:emerald_shard"
              ],
              "tuning": "geodes:emerald"
            }
            """;

    @Test
    void serializerIsRegistered() {
        assertEquals(GeodesRecipeSerializers.CRYSTAL_TUNING,
                net.minecraft.core.registries.BuiltInRegistries.RECIPE_SERIALIZER
                        .getValue(GeodesIds.of("geodes", "crystal_tuning")));
    }

    @Test
    void codecParsesADatapackTuningRecipe() {
        var codec = GeodesRecipeSerializers.CRYSTAL_TUNING.codec();
        TuningRecipe recipe = codec.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(RECIPE_JSON))
                .getOrThrow();

        assertEquals(GeodesIds.parse("geodes:emerald"), recipe.getTuningId());
        assertEquals(1, recipe.getIngredients().size());
        assertEquals(CraftingBookCategory.MISC, recipe.category());
        assertEquals("", recipe.group());
    }

    @Test
    void codecRejectsARecipeWithoutIngredients() {
        var codec = GeodesRecipeSerializers.CRYSTAL_TUNING.codec();
        var result = codec.codec().parse(JsonOps.INSTANCE, JsonParser.parseString("""
                {"type": "geodes:crystal_tuning", "ingredients": [], "tuning": "geodes:emerald"}
                """));
        assertTrue(result.isError(), "a tuning recipe with no ingredients must not parse");
    }

    @Test
    void codecRejectsMoreThanNineIngredients() {
        var codec = GeodesRecipeSerializers.CRYSTAL_TUNING.codec();
        StringBuilder ingredients = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i > 0) {
                ingredients.append(',');
            }
            ingredients.append("\"geodes:emerald_shard\"");
        }
        ingredients.append(']');
        var result = codec.codec().parse(JsonOps.INSTANCE, JsonParser.parseString("""
                {"type": "geodes:crystal_tuning", "ingredients": %s, "tuning": "geodes:emerald"}
                """.formatted(ingredients)));
        assertTrue(result.isError(), "a tuning recipe with more than 9 ingredients must not parse");
    }

    @Test
    void codecRejectsAMissingTuningId() {
        var codec = GeodesRecipeSerializers.CRYSTAL_TUNING.codec();
        var result = codec.codec().parse(JsonOps.INSTANCE, JsonParser.parseString("""
                {"type": "geodes:crystal_tuning", "ingredients": ["geodes:emerald_shard"]}
                """));
        assertTrue(result.isError(), "a tuning recipe without a tuning id must not parse");
    }
}
