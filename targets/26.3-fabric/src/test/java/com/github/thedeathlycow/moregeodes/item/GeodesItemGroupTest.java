package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeodesItemGroupTest {
    private static final class CollectingOutput implements CreativeModeTab.Output {
        final List<ItemStack> stacks = new ArrayList<>();

        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack);
        }
    }

    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    @Test
    void tabListsEveryModItemExactlyOnce() {
        CollectingOutput output = new CollectingOutput();
        GeodesItemGroup.addTabEntries(output);

        Set<Item> seen = new HashSet<>();
        List<Item> duplicates = new ArrayList<>();
        for (ItemStack stack : output.stacks) {
            if (stack.is(ModItems.TUNED_CRYSTAL_LOCATOR)) {
                seen.add(stack.getItem());
                continue;
            }
            if (!seen.add(stack.getItem())) {
                duplicates.add(stack.getItem());
            }
        }
        assertEquals(List.of(), duplicates, "tab must not list an item twice");

        for (Item item : new Item[]{
                ModItems.EMERALD_CRYSTAL_BLOCK, ModItems.BUDDING_EMERALD,
                ModItems.SMALL_EMERALD_BUD, ModItems.MEDIUM_EMERALD_BUD, ModItems.LARGE_EMERALD_BUD,
                ModItems.EMERALD_CLUSTER,
                ModItems.QUARTZ_CRYSTAL_BLOCK, ModItems.BUDDING_QUARTZ, ModItems.QUARTZ_CLUSTER,
                ModItems.SMALL_QUARTZ_BUD, ModItems.MEDIUM_QUARTZ_BUD, ModItems.LARGE_QUARTZ_BUD,
                ModItems.DIAMOND_CRYSTAL_BLOCK, ModItems.DIAMOND_CLUSTER,
                ModItems.ECHO_BLOCK, ModItems.BUDDING_ECHO_BLOCK, ModItems.ECHO_CLUSTER,
                ModItems.SMALL_ECHO_BUD, ModItems.MEDIUM_ECHO_BUD, ModItems.LARGE_ECHO_BUD,
                ModItems.LAPIS_CRYSTAL_BLOCK, ModItems.BUDDING_LAPIS, ModItems.LAPIS_CLUSTER,
                ModItems.SMALL_LAPIS_BUD, ModItems.MEDIUM_LAPIS_BUD, ModItems.LARGE_LAPIS_BUD,
                ModItems.GYPSUM_CRYSTAL_BLOCK, ModItems.BUDDING_GYPSUM, ModItems.GYPSUM_ROSE,
                ModItems.SMALL_GYPSUM_BUD, ModItems.MEDIUM_GYPSUM_BUD, ModItems.LARGE_GYPSUM_BUD,
                ModItems.GYPSUM_SHARD,
                ModItems.PYRITE, ModItems.PYRITE_CHUNK, ModItems.PYRITE_STAIRS, ModItems.PYRITE_SLAB,
                ModItems.PYRITE_WALL,
                ModItems.CALCITE_STAIRS, ModItems.CALCITE_SLAB, ModItems.CALCITE_WALL,
                ModItems.GABRRO,
                ModItems.CHISELED_GYPSUM, ModItems.GYPSUM_PILLAR, ModItems.POLISHED_GYPSUM_BLOCK,
                ModItems.POLISHED_GYPSUM_STAIRS, ModItems.POLISHED_GYPSUM_SLAB, ModItems.POLISHED_GYPSUM_WALL,
                ModItems.SMOOTH_GYPSUM_BLOCK, ModItems.SMOOTH_GYPSUM_STAIRS, ModItems.SMOOTH_GYPSUM_SLAB,
                ModItems.SMOOTH_GYPSUM_WALL,
                ModItems.CRYSTAL_LOCATOR, ModItems.ECHO_LOCATOR, ModItems.TUNED_CRYSTAL_LOCATOR
        }) {
            assertTrue(seen.contains(item), "tab is missing item " + item);
        }
    }

    @Test
    void tabShipsOnePretunedLocatorPerVanillaTuning() {
        CollectingOutput output = new CollectingOutput();
        GeodesItemGroup.addTabEntries(output);

        List<net.minecraft.resources.Identifier> tuningIds = output.stacks.stream()
                .filter(stack -> stack.is(ModItems.TUNED_CRYSTAL_LOCATOR))
                .map(stack -> stack.get(TunedLocatorData.TUNING_COMPONENT))
                .toList();

        assertEquals(
                List.of(
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:amethyst"),
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:emerald"),
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:quartz"),
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:diamond"),
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:echo"),
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:lapis"),
                        com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:gypsum")
                ),
                tuningIds,
                "the tab must ship exactly one pre-tuned locator per vanilla tuning"
        );
    }

    @Test
    void tunedLocatorStackCarriesItsTuningComponent() {
        ItemStack stack = GeodesItemGroup.makeTunedLocatorStack("geodes:emerald");
        assertTrue(stack.is(ModItems.TUNED_CRYSTAL_LOCATOR));
        assertEquals(com.github.thedeathlycow.moregeodes.util.GeodesIds.parse("geodes:emerald"),
                stack.get(TunedLocatorData.TUNING_COMPONENT));
    }

    @Test
    void untunedLocatorStackHasNoTuningComponent() {
        ItemStack stack = new ItemStack(ModItems.CRYSTAL_LOCATOR);
        assertFalse(stack.has(TunedLocatorData.TUNING_COMPONENT));
    }
}
