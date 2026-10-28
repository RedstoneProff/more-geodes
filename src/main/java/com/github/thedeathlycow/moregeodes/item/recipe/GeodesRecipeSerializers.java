package com.github.thedeathlycow.moregeodes.item.recipe;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GeodesRecipeSerializers {

    public static final RecipeSerializer<TuningRecipe> CRYSTAL_TUNING = TuningRecipe.createSerializer();

    public static void registerAll() {
        Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                GeodesIds.of(MoreGeodes.MODID, "crystal_tuning"),
                CRYSTAL_TUNING
        );
    }


    private GeodesRecipeSerializers() {
    }
}
