package com.github.thedeathlycow.moregeodes.edgecases;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.features.ModPlacedFeatures;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedCrystalLocatorItem;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.github.thedeathlycow.moregeodes.item.recipe.TuningRecipe;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCaseTests {
    @TempDir
    static Path configDir;

    @BeforeAll
    static void bootStrap() {
        TestBootstrap.registerModContent(configDir);
    }

    private static TuningRecipe amethystRecipe() {
        return new TuningRecipe(
                "",
                CraftingBookCategory.MISC,
                List.of(
                        Ingredient.of(Items.AMETHYST_SHARD),
                        Ingredient.of(ModItems.CRYSTAL_LOCATOR)
                ),
                ResourceLocation.fromNamespaceAndPath("geodes", "amethyst")
        );
    }

    @Test
    void emptyCraftingGridDoesNotMatchAndDoesNotCrash() {
        TuningRecipe recipe = amethystRecipe();

        assertFalse(recipe.matches(CraftingInput.EMPTY, null),
                "an empty crafting grid must not match the tuning recipe");
    }

    @Test
    void gridWithoutTheLocatorDoesNotMatch() {
        TuningRecipe recipe = amethystRecipe();

        CraftingInput input = CraftingInput.of(1, 1, List.of(new ItemStack(Items.AMETHYST_SHARD)));
        assertFalse(recipe.matches(input, null),
                "a grid with only the tuning material and no locator must not match");
    }

    @Test
    void correctGridMatches() {
        TuningRecipe recipe = amethystRecipe();

        CraftingInput input = CraftingInput.of(2, 1, List.of(
                new ItemStack(Items.AMETHYST_SHARD),
                new ItemStack(ModItems.CRYSTAL_LOCATOR)
        ));
        assertTrue(recipe.matches(input, null),
                "a grid with the tuning material and an untuned locator must match");
    }

    @Test
    void unknownTuningComponentIsNotTuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        stack.set(TunedLocatorData.TUNING_COMPONENT,
                ResourceLocation.fromNamespaceAndPath("geodes", "does_not_exist"));

        RegistryAccess registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        assertFalse(((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR).isTuned(stack, registries),
                "an unknown tuning id must not count as tuned");
        assertNull(TunedCrystalLocatorItem.getTuning(registries, stack),
                "an unknown tuning id must resolve to no tuning");
    }

    @Test
    void unknownTuningTooltipDoesNotCrash() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        stack.set(TunedLocatorData.TUNING_COMPONENT,
                ResourceLocation.fromNamespaceAndPath("geodes", "does_not_exist"));

        List<net.minecraft.network.chat.Component> tooltip = new ArrayList<>();
        assertDoesNotThrow(() -> ((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR)
                        .appendHoverText(stack, net.minecraft.world.item.Item.TooltipContext.of(
                                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)),
                                tooltip, TooltipFlag.NORMAL),
                "a tooltip for an unknown tuning id must not crash");
        assertEquals(1, tooltip.size(),
                "an unknown tuning id must only add the untuned hint line");
        assertEquals(com.github.thedeathlycow.moregeodes.item.tuning.Tuning.UNTUNED.getDescription().getString(),
                tooltip.get(0).getString(),
                "the added line must be the untuned hint, not a tuning description");
    }

    @Test
    void emptyStacksDoNotCrash() {
        RegistryAccess registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

        assertFalse(((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR).isTuned(ItemStack.EMPTY, registries),
                "the empty stack must never count as tuned");
        assertNull(TunedCrystalLocatorItem.getTuning(registries, ItemStack.EMPTY),
                "the empty stack must resolve to no tuning");
        assertNull(stackComponentOf(ItemStack.EMPTY),
                "the empty stack must carry no tuning component");
    }

    private static ResourceLocation stackComponentOf(ItemStack stack) {
        return stack.get(TunedLocatorData.TUNING_COMPONENT);
    }

    @Test
    void everyCodeReferencedPlacedFeatureHasAShippedJson() throws Exception {
        Path placedDir = Path.of("../../src/versions/1.21.4/resources/data/geodes/worldgen/placed_feature");
        Path configuredDir = Path.of("../../src/versions/1.21.4/resources/data/geodes/worldgen/configured_feature");
        assertTrue(Files.isDirectory(placedDir), "the placed_feature data folder must exist");
        assertTrue(Files.isDirectory(configuredDir), "the configured_feature data folder must exist");

        List<String> placedIds = new ArrayList<>();
        for (var field : ModPlacedFeatures.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != ResourceKey.class) {
                continue;
            }
            field.setAccessible(true);
            ResourceKey<?> key = (ResourceKey<?>) field.get(null);
            String id = key.location().getPath();
            placedIds.add(id);

            Path json = placedDir.resolve(id + ".json");
            assertTrue(Files.exists(json),
                    "placed feature " + id + " referenced by ModPlacedFeatures must ship a JSON");

            JsonObject placed = JsonParser.parseString(Files.readString(json)).getAsJsonObject();
            assertTrue(placed.has("feature"), "placed feature " + id + " must reference a configured feature");
            String configuredId = placed.get("feature").getAsString();
            assertTrue(Files.exists(configuredDir.resolve(configuredId.substring(configuredId.indexOf(':') + 1) + ".json")),
                    "placed feature " + id + " references configured feature " + configuredId + " which has no shipped JSON");
        }

        assertEquals(10, placedIds.size(),
                "ModPlacedFeatures must declare exactly the shipped placed features");
    }
}
