package com.github.thedeathlycow.moregeodes.item.recipe;

import com.github.thedeathlycow.moregeodes.item.GeodesItemGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class TuningRecipe extends ShapelessRecipe {
    private final ResourceLocation tuningId;

    public TuningRecipe(
            String group,
            CraftingBookCategory category,
            List<Ingredient> input,
            ResourceLocation tuningId
    ) {
        super(group, category, GeodesItemGroup.makeTunedLocatorStack(tuningId.toString()), of(input));
        this.tuningId = tuningId;
    }

    private static NonNullList<Ingredient> of(List<Ingredient> input) {
        return NonNullList.of(Ingredient.EMPTY, input.toArray(new Ingredient[0]));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GeodesRecipeSerializers.CRYSTAL_TUNING;
    }

    public ResourceLocation getTuningId() {
        return tuningId;
    }

    public static RecipeSerializer<TuningRecipe> createSerializer() {
        return new Serializer();
    }

    public static class Serializer implements RecipeSerializer<TuningRecipe> {
        private static final Codec<TuningRecipe> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(TuningRecipe::getGroup),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(TuningRecipe::category),
                        Ingredient.CODEC_NONEMPTY
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(
                                        ingredients -> {
                                            Ingredient[] aingredient = ingredients.stream()
                                                    .filter(ingredient -> !ingredient.isEmpty())
                                                    .toArray(Ingredient[]::new);
                                            if (aingredient.length == 0) {
                                                return DataResult.error(() -> "No ingredients for tuning recipe");
                                            } else {
                                                return aingredient.length > 9
                                                        ? DataResult.error(() -> "Too many ingredients for tuning recipe. The maximum is 9")
                                                        : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                            }
                                        },
                                        DataResult::success
                                )
                                .forGetter(TuningRecipe::getIngredients),
                        ResourceLocation.CODEC.fieldOf("tuning").forGetter(TuningRecipe::getTuningId)
                ).apply(instance, TuningRecipe::new)
        );

        @Override
        public Codec<TuningRecipe> codec() {
            return CODEC;
        }

        @Override
        public TuningRecipe fromNetwork(FriendlyByteBuf buf) {
            String group = buf.readUtf();

            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);

            int numIngredients = buf.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(numIngredients);
            for (int i = 0; i < numIngredients; ++i) {
                ingredients.add(Ingredient.fromNetwork(buf));
            }

            ResourceLocation tuning = buf.readResourceLocation();

            return new TuningRecipe(
                    group,
                    category,
                    ingredients,
                    tuning
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TuningRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
            buf.writeResourceLocation(recipe.getTuningId());
        }
    }
}
