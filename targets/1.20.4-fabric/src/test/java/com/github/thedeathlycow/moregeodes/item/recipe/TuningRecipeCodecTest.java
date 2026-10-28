package com.github.thedeathlycow.moregeodes.item.recipe;

import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningRecipeCodecTest {
    @BeforeAll
    static void bootstrap() {
        com.github.thedeathlycow.moregeodes.TestBootstrap.registerModContent(
                java.nio.file.Path.of("build", "test-config-recipe"));
    }

    private static JsonObject readShippedJson(String path) {
        try (InputStream in = TuningRecipeCodecTest.class.getResourceAsStream(path)) {
            assertTrue(in != null, "shipped resource not on the test classpath: " + path);
            return JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new AssertionError("could not read " + path, e);
        }
    }

    @Test
    void parsesTheShippedAmethystTuningRecipe() {
        JsonObject json = readShippedJson("/data/geodes/recipes/tuning/amethyst_tuning.json");

        var decoded = GeodesRecipeSerializers.CRYSTAL_TUNING.codec().parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isPresent(), "shipped amethyst tuning recipe failed to decode: " + decoded);
        TuningRecipe recipe = decoded.result().get();

        assertEquals(new ResourceLocation("geodes", "amethyst"), recipe.getTuningId());
        assertEquals("geodes_tunings", recipe.getGroup());
        assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    void craftedResultIsATunedLocatorWithTheRightTuningNbt() {
        JsonObject json = readShippedJson("/data/geodes/recipes/tuning/amethyst_tuning.json");
        TuningRecipe recipe = GeodesRecipeSerializers.CRYSTAL_TUNING.codec()
                .parse(JsonOps.INSTANCE, json).result().orElseThrow();

        ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY);
        assertNotNull(result, "the recipe must produce a result item");
        CompoundTag tuningNbt = result.getTag().getCompound(TunedLocatorData.TUNING_NBT_KEY);
        assertEquals("geodes:amethyst", tuningNbt.getString(TunedLocatorData.TUNING_ID_NBT_KEY),
                "the crafted locator must be tuned to geodes:amethyst");
    }

    @Test
    void emptyIngredientsFailToDecode() {
        JsonObject json = JsonParser.parseString("""
                {
                  "type": "geodes:crystal_tuning",
                  "ingredients": [],
                  "tuning": "geodes:amethyst"
                }
                """).getAsJsonObject();

        var decoded = GeodesRecipeSerializers.CRYSTAL_TUNING.codec().parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isEmpty(), "a tuning recipe without ingredients must not decode");
    }

    @Test
    void tooManyIngredientsFailToDecode() {
        StringBuilder ingredients = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i > 0) {
                ingredients.append(',');
            }
            ingredients.append("{\"item\":\"minecraft:amethyst_shard\"}");
        }
        JsonObject json = JsonParser.parseString("""
                {
                  "type": "geodes:crystal_tuning",
                  "ingredients": [%s],
                  "tuning": "geodes:amethyst"
                }
                """.formatted(ingredients)).getAsJsonObject();

        var decoded = GeodesRecipeSerializers.CRYSTAL_TUNING.codec().parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isEmpty(), "a tuning recipe with more than 9 ingredients must not decode");
    }

    @Test
    void missingTuningIdFailsToDecode() {
        JsonObject json = JsonParser.parseString("""
                {
                  "type": "geodes:crystal_tuning",
                  "ingredients": [{"item": "minecraft:amethyst_shard"}]
                }
                """).getAsJsonObject();

        var decoded = GeodesRecipeSerializers.CRYSTAL_TUNING.codec().parse(JsonOps.INSTANCE, json);
        assertTrue(decoded.result().isEmpty(), "a tuning recipe without a tuning id must not decode");
    }

    @Test
    void networkRoundTripPreservesTuningIdGroupAndIngredients() {
        TuningRecipe recipe = new TuningRecipe(
                "geodes_tunings",
                CraftingBookCategory.MISC,
                List.of(
                        Ingredient.of(Items.AMETHYST_SHARD),
                        Ingredient.of(com.github.thedeathlycow.moregeodes.item.ModItems.CRYSTAL_LOCATOR)
                ),
                new ResourceLocation("geodes", "amethyst")
        );

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        GeodesRecipeSerializers.CRYSTAL_TUNING.toNetwork(buf, recipe);
        TuningRecipe decoded = GeodesRecipeSerializers.CRYSTAL_TUNING.fromNetwork(buf);

        assertEquals(recipe.getTuningId(), decoded.getTuningId());
        assertEquals(recipe.getGroup(), decoded.getGroup());
        assertEquals(recipe.getIngredients().size(), decoded.getIngredients().size());
        assertEquals(0, buf.readableBytes(), "the network buffer must be fully consumed");
    }

    @Test
    void getTuningIdReadsTheNbtWrittenByMakeTunedLocatorStack() {
        ItemStack stack = TunedLocatorData.makeTunedLocatorStack("geodes:quartz");
        assertEquals(new ResourceLocation("geodes", "quartz"), TunedLocatorData.getTuningId(stack));
    }

    @Test
    void getTuningIdReturnsNullForAnUntunedStack() {
        assertNull(TunedLocatorData.getTuningId(new ItemStack(Items.AMETHYST_SHARD)));
        assertNull(TunedLocatorData.getTuningId(new ItemStack(com.github.thedeathlycow.moregeodes.item.ModItems.CRYSTAL_LOCATOR)));
    }
}
