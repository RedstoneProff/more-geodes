package com.github.thedeathlycow.moregeodes.item.recipe;

import com.github.thedeathlycow.moregeodes.item.GeodesItemGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class TuningRecipe extends ShapelessRecipe {
    private final ResourceLocation tuningId;
    private final List<Ingredient> ingredients;

    public TuningRecipe(
            String group,
            CraftingBookCategory category,
            List<Ingredient> input,
            ResourceLocation tuningId
    ) {
        super(group, category, GeodesItemGroup.makeTunedLocatorStack(tuningId.toString()), input);
        this.tuningId = tuningId;
        this.ingredients = List.copyOf(input);
    }

    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        // the serializer type is pinned to ShapelessRecipe's here; the cast returns this recipe's own
        @SuppressWarnings("unchecked")
        RecipeSerializer<ShapelessRecipe> serializer =
                (RecipeSerializer<ShapelessRecipe>) (RecipeSerializer<?>) GeodesRecipeSerializers.CRYSTAL_TUNING;
        return serializer;
    }

    public ResourceLocation getTuningId() {
        return tuningId;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public static RecipeSerializer<TuningRecipe> createSerializer() {
        return new Serializer();
    }

    public static class Serializer implements RecipeSerializer<TuningRecipe> {
        private static final MapCodec<TuningRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(TuningRecipe::group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                                .forGetter(TuningRecipe::category),
                        Ingredient.CODEC
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
                                                        : DataResult.success(List.of(aingredient));
                                            }
                                        },
                                        DataResult::success
                                )
                                .forGetter(TuningRecipe::getIngredients),
                        ResourceLocation.CODEC.fieldOf("tuning").forGetter(TuningRecipe::getTuningId)
                ).apply(instance, TuningRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, TuningRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::writeToStream,
                Serializer::readFromStream
        );

        private static void writeToStream(RegistryFriendlyByteBuf buf, TuningRecipe recipe) {
            buf.writeUtf(recipe.group());
            buf.writeEnum(recipe.category());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            buf.writeResourceLocation(recipe.getTuningId());
        }

        private static TuningRecipe readFromStream(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();

            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);

            int numIngredients = buf.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(numIngredients);
            for (int i = 0; i < numIngredients; ++i) {
                ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
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
        public MapCodec<TuningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TuningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
