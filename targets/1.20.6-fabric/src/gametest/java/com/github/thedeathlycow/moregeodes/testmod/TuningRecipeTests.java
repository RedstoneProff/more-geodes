package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.github.thedeathlycow.moregeodes.item.recipe.GeodesRecipeSerializers;
import com.github.thedeathlycow.moregeodes.item.recipe.TuningRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Optional;

public class TuningRecipeTests {
    private static final List<String> TUNING_IDS = List.of(
            "amethyst", "diamond", "echo", "emerald", "gypsum", "lapis", "quartz"
    );

    private static Optional<RecipeHolder<?>> getTuningRecipe(GameTestHelper context, String tuningId) {
        RecipeManager recipeManager = context.getLevel().getRecipeManager();
        ResourceLocation id = new ResourceLocation(
                "geodes", "tuning/" + tuningId + "_tuning"
        );
        return recipeManager.byKey(id);
    }

    @GameTest(template = "geodes-test:empty15")
    public void F_011_all_shipped_tuning_recipes_load(GameTestHelper context) {
        for (String tuningId : TUNING_IDS) {
            Optional<RecipeHolder<?>> recipe = getTuningRecipe(context, tuningId);
            context.assertTrue(
                    recipe.isPresent(),
                    "missing tuning recipe geodes:tuning/" + tuningId + "_tuning"
            );
            context.assertTrue(
                    recipe.get().value().getSerializer() == GeodesRecipeSerializers.CRYSTAL_TUNING,
                    "recipe geodes:tuning/" + tuningId + "_tuning has the wrong serializer"
            );
        }
        context.succeed();
    }

    @GameTest(template = "geodes-test:empty15")
    public void F_011_amethyst_tuning_recipe_assembles_tuned_locator(GameTestHelper context) {
        Optional<RecipeHolder<?>> recipe = getTuningRecipe(context, "amethyst");
        context.assertTrue(recipe.isPresent(), "missing tuning recipe geodes:tuning/amethyst_tuning");

        TuningRecipe tuningRecipe = (TuningRecipe) recipe.get().value();
        var result = tuningRecipe.assemble(null, context.getLevel().registryAccess());

        context.assertTrue(
                result.is(ModItems.TUNED_CRYSTAL_LOCATOR),
                "amethyst tuning recipe must assemble into a tuned crystal locator"
        );
        ResourceLocation tuningId = result.get(TunedLocatorData.TUNING_COMPONENT);
        context.assertTrue(
                new ResourceLocation("geodes", "amethyst").equals(tuningId),
                "assembled locator must carry the geodes:amethyst tuning component, got "
                        + tuningId
        );
        context.succeed();
    }
}
