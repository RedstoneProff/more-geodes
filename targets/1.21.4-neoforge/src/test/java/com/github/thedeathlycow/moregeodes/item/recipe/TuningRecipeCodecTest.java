package com.github.thedeathlycow.moregeodes.item.recipe;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.item.TunedCrystalLocatorItem;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningRecipeCodecTest {
    private static RegistryOps<com.google.gson.JsonElement> registryOps;

    @TempDir
    static Path configDir;

    @BeforeAll
    static void bootStrapAndRegisterMod() {
        TestBootstrap.registerModContent(configDir);
        registryOps = RegistryOps.create(
                JsonOps.INSTANCE,
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
        );
    }

    private static DataResult<TuningRecipe> decode(String json) {
        return GeodesRecipeSerializers.CRYSTAL_TUNING.codec().codec()
                .parse(registryOps, JsonParser.parseString(json));
    }

    @Test
    void validJson_parsesToRecipeWithTuningId() {
        DataResult<TuningRecipe> result = decode("""
                {
                    "group": "geodes_tunings",
                    "ingredients": [
                        "minecraft:amethyst_shard",
                        "geodes:crystal_locator"
                    ],
                    "tuning": "geodes:amethyst"
                }
                """);

        assertTrue(result.result().isPresent(), "a valid tuning recipe must parse: " + result.error().map(Object::toString));
        TuningRecipe recipe = result.result().get();
        assertEquals(ResourceLocation.fromNamespaceAndPath("geodes", "amethyst"), recipe.getTuningId());
        assertEquals("geodes_tunings", recipe.group());
        assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    void emptyIngredientsFails() {
        DataResult<TuningRecipe> result = decode("""
                {
                    "ingredients": [],
                    "tuning": "geodes:amethyst"
                }
                """);

        assertTrue(result.error().isPresent(), "a recipe with no ingredients must fail to parse");
    }

    @Test
    void tooManyIngredientsFails() {
        StringBuilder ingredients = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i > 0) {
                ingredients.append(',');
            }
            ingredients.append("\"minecraft:amethyst_shard\"");
        }
        DataResult<TuningRecipe> result = decode("""
                {
                    "ingredients": [%s],
                    "tuning": "geodes:amethyst"
                }
                """.formatted(ingredients));

        assertTrue(result.error().isPresent(), "a recipe with more than 9 ingredients must fail to parse");
    }

    @Test
    void missingTuningFails() {
        DataResult<TuningRecipe> result = decode("""
                {
                    "ingredients": [
                        "minecraft:amethyst_shard"
                    ]
                }
                """);

        assertTrue(result.error().isPresent(), "a recipe without a tuning id must fail to parse");
    }

    @Test
    void assembleProducesLocatorWithTuningComponent() {
        DataResult<TuningRecipe> result = decode("""
                {
                    "ingredients": [
                        "minecraft:amethyst_shard",
                        "geodes:crystal_locator"
                    ],
                    "tuning": "geodes:amethyst"
                }
                """);
        TuningRecipe recipe = result.result().orElseThrow();

        ItemStack assembled = recipe.assemble(null,
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        assertNotNull(assembled, "assembling must produce a result stack");

        ResourceLocation tuningId = assembled.get(TunedLocatorData.TUNING_COMPONENT);
        assertEquals(ResourceLocation.fromNamespaceAndPath("geodes", "amethyst"), tuningId,
                "the assembled stack must carry the tuning data component");
        assertTrue(assembled.getItem() instanceof TunedCrystalLocatorItem,
                "the assembled stack must be a tuned crystal locator");
    }

    @Test
    void shippedAmethystTuningRecipeJson_parses() throws Exception {
        Path fixture = Path.of(
                "../../src/versions/1.21.4/resources/data/geodes/recipe/tuning/amethyst_tuning.json"
        );
        assertTrue(Files.exists(fixture), "the shipped amethyst tuning recipe must exist");

        DataResult<TuningRecipe> result = GeodesRecipeSerializers.CRYSTAL_TUNING.codec().codec()
                .parse(registryOps, JsonParser.parseString(Files.readString(fixture)));
        assertTrue(result.result().isPresent(),
                "the shipped amethyst tuning recipe must parse: " + result.error().map(Object::toString));
        assertEquals(ResourceLocation.fromNamespaceAndPath("geodes", "amethyst"), result.result().get().getTuningId());
    }

    @Test
    void ingredientCodec_acceptsPlainItemIdStringForm() {
        DataResult<Ingredient> result = Ingredient.CODEC.parse(
                registryOps,
                JsonParser.parseString("\"minecraft:amethyst_shard\"")
        );
        assertTrue(result.result().isPresent(),
                "the plain item-id string ingredient form must parse: " + result.error().map(Object::toString));
        assertTrue(result.result().get().test(new ItemStack(net.minecraft.world.item.Items.AMETHYST_SHARD)));
    }

    @Test
    void ingredientCodec_rejectsLegacyItemObjectForm() {
        DataResult<Ingredient> result = Ingredient.CODEC.parse(
                registryOps,
                JsonParser.parseString("{\"item\": \"minecraft:amethyst_shard\"}")
        );
        assertTrue(result.error().isPresent(),
                "the legacy {\"item\": ...} object form must fail on 1.21.4");
    }
}
