package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.item.recipe.GeodesRecipeSerializers;
import com.github.thedeathlycow.moregeodes.item.recipe.TuningRecipe;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuningAndItemGroupTest {
    @TempDir
    static Path configDir;

    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
    }

    @Test
    void tuningDatapackCodecParsesTheShippedAmethystFixture() throws Exception {
        String json = java.nio.file.Files.readString(
                java.nio.file.Path.of("../../src/versions/1.20.4/resources/data/geodes/geodes/tunings/amethyst.json"));
        var parsed = com.google.gson.JsonParser.parseString(json);
        var result = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, parsed);
        assertTrue(result.result().isPresent(), "shipped amethyst tuning must parse: " + result);
        Tuning tuning = result.result().get();
        assertEquals(13144304, tuning.getColor());
        assertNotNull(tuning.getDescription());
        assertNotNull(tuning.getIsTunedToBlock());
    }

    @Test
    void tuningDatapackCodecRejectsMissingFields() {
        var json = com.google.gson.JsonParser.parseString("{\"color\": 1}");
        var result = Tuning.DATAPACK_CODEC.parse(JsonOps.INSTANCE, json);
        assertTrue(result.error().isPresent(), "a tuning without description/predicate must fail to parse");
    }

    @Test
    void tuningNetworkCodecRoundTripsColorAndDescription() {
        Tuning tuning = Tuning.UNTUNED;
        var encoded = Tuning.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, tuning);
        assertTrue(encoded.result().isPresent());
        var decoded = Tuning.NETWORK_CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent());
        assertEquals(tuning.getColor(), decoded.result().get().getColor());
        assertEquals(tuning.getDescription().getString(), decoded.result().get().getDescription().getString());
    }

    private static final String VALID_RECIPE_JSON = """
            {
              "type": "geodes:crystal_tuning",
              "category": "misc",
              "ingredients": [{"item": "geodes:crystal_locator"}, {"item": "minecraft:amethyst_shard"}],
              "tuning": "geodes:amethyst"
            }
            """;

    @Test
    void tuningRecipeCodecParsesAValidRecipe() {
        var json = com.google.gson.JsonParser.parseString(VALID_RECIPE_JSON);
        var result = ((com.github.thedeathlycow.moregeodes.item.recipe.TuningRecipe.Serializer)
                GeodesRecipeSerializers.CRYSTAL_TUNING).codec().parse(JsonOps.INSTANCE, json);
        assertTrue(result.result().isPresent(), "valid tuning recipe must parse: " + result);
        TuningRecipe recipe = result.result().get();
        assertEquals(new ResourceLocation(MoreGeodes.MODID, "amethyst"), recipe.getTuningId());
        assertEquals(2, recipe.getIngredients().size());
        assertEquals(GeodesRecipeSerializers.CRYSTAL_TUNING, recipe.getSerializer());
    }

    @Test
    void tuningRecipeCodecRejectsMissingTuning() {
        var json = com.google.gson.JsonParser.parseString("""
                {"type": "geodes:crystal_tuning", "ingredients": [{"item": "minecraft:stick"}]}
                """);
        var result = ((TuningRecipe.Serializer) GeodesRecipeSerializers.CRYSTAL_TUNING)
                .codec().parse(JsonOps.INSTANCE, json);
        assertTrue(result.error().isPresent(), "a recipe without a tuning id must fail");
    }

    @Test
    void tuningRecipeCodecRejectsEmptyIngredients() {
        var json = com.google.gson.JsonParser.parseString("""
                {"type": "geodes:crystal_tuning", "ingredients": [], "tuning": "geodes:amethyst"}
                """);
        var result = ((TuningRecipe.Serializer) GeodesRecipeSerializers.CRYSTAL_TUNING)
                .codec().parse(JsonOps.INSTANCE, json);
        assertTrue(result.error().isPresent(), "a recipe with no ingredients must fail");
    }

    @Test
    void tuningRecipeCodecRejectsMoreThanNineIngredients() {
        StringBuilder ingredients = new StringBuilder("[{\"item\": \"minecraft:stick\"}");
        for (int i = 1; i < 10; i++) {
            ingredients.append(",{\"item\": \"minecraft:stick\"}");
        }
        ingredients.append("]");
        var json = com.google.gson.JsonParser.parseString(
                "{\"type\": \"geodes:crystal_tuning\", \"ingredients\": " + ingredients
                        + ", \"tuning\": \"geodes:amethyst\"}");
        var result = ((TuningRecipe.Serializer) GeodesRecipeSerializers.CRYSTAL_TUNING)
                .codec().parse(JsonOps.INSTANCE, json);
        assertTrue(result.error().isPresent(), "a recipe with 10 ingredients must fail");
    }

    @Test
    void tuningRecipeNetworkRoundTrip() {
        TuningRecipe recipe = new TuningRecipe(
                "group", CraftingBookCategory.MISC,
                List.of(Ingredient.of(net.minecraft.world.item.Items.AMETHYST_SHARD)),
                new ResourceLocation(MoreGeodes.MODID, "amethyst"));

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ((TuningRecipe.Serializer) GeodesRecipeSerializers.CRYSTAL_TUNING).toNetwork(buf, recipe);
        TuningRecipe decoded = ((TuningRecipe.Serializer) GeodesRecipeSerializers.CRYSTAL_TUNING)
                .fromNetwork(buf);

        assertEquals(recipe.getTuningId(), decoded.getTuningId());
        assertEquals(recipe.getGroup(), decoded.getGroup());
        assertEquals(recipe.getIngredients().size(), decoded.getIngredients().size());
        assertEquals(0, buf.readableBytes(), "the buffer must be fully consumed");
    }

    @Test
    void tunedLocatorStackCarriesTheTuningIdInNbt() {
        ItemStack stack = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        assertEquals(new ResourceLocation(MoreGeodes.MODID, "amethyst"),
                TunedLocatorData.getTuningId(stack));
        CompoundTag tuning = stack.getTag().getCompound(TunedLocatorData.TUNING_NBT_KEY);
        assertEquals("geodes:amethyst", tuning.getString(TunedLocatorData.TUNING_ID_NBT_KEY));
    }

    @Test
    void plainLocatorStackHasNoTuningId() {
        ItemStack stack = new ItemStack(ModItems.CRYSTAL_LOCATOR);
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void locatorStackWithGarbageTuningNbtYieldsNull() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        stack.getOrCreateTag().putString(TunedLocatorData.TUNING_NBT_KEY, "not-a-compound");
        assertNull(TunedLocatorData.getTuningId(stack), "a non-compound tuning key must be ignored");
    }

    @Test
    void locatorStackWithEmptyTuningIdYieldsNull() {
        ItemStack stack = TunedLocatorData.makeTunedLocatorStack("");
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    private static final class CollectingOutput implements CreativeModeTab.Output {
        final List<ItemStack> stacks = new ArrayList<>();

        @Override
        public void accept(ItemStack stack) {
            stacks.add(stack);
        }

        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack);
        }
    }

    @Test
    void creativeTabContainsEveryBlockAndItemPlusTunedLocators() {
        CollectingOutput output = new CollectingOutput();
        GeodesItemGroup.addTabEntries(output);

        assertTrue(output.stacks.size() >= 55, "creative tab too small: " + output.stacks.size());

        java.util.Set<String> shardIds = java.util.Set.of(
                "emerald_shard", "quartz_shard", "echo_shard", "lapis_shard");
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals(MoreGeodes.MODID) || shardIds.contains(id.getPath())) {
                continue;
            }
            long expected = id.getPath().equals("tuned_crystal_locator") ? 7 : 1;
            long count = output.stacks.stream().filter(s -> s.getItem() == item).count();
            assertEquals(expected, count, "item " + id + " has the wrong tab count");
        }
        for (String shard : shardIds) {
            long count = output.stacks.stream().filter(s ->
                    BuiltInRegistries.ITEM.getKey(s.getItem()).getPath().equals(shard)).count();
            assertEquals(0, count, "shard " + shard + " must not be in the tab (upstream parity)");
        }

        for (String tuning : new String[]{
                "geodes:amethyst", "geodes:emerald", "geodes:quartz", "geodes:diamond",
                "geodes:echo", "geodes:lapis", "geodes:gypsum"}) {
            ItemStack tuned = TunedLocatorData.makeTunedLocatorStack(tuning);
            assertTrue(output.stacks.stream().anyMatch(s ->
                            s.getItem() == tuned.getItem()
                                    && TunedLocatorData.getTuningId(s).equals(TunedLocatorData.getTuningId(tuned))),
                    "missing pre-tuned locator for " + tuning);
        }

        assertFalse(output.stacks.stream().anyMatch(s ->
                        TunedLocatorData.getTuningId(s) != null
                                && TunedLocatorData.getTuningId(s).getNamespace().equals("spectrum")),
                "spectrum tunings must not appear when Spectrum is not loaded");
    }
}
