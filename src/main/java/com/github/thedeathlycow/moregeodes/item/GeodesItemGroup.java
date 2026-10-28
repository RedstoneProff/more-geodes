package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class GeodesItemGroup {
    public static final ResourceKey<CreativeModeTab> MORE_GEODES = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            GeodesIds.of(MoreGeodes.MODID, "main")
    );

    public static void addTabEntries(CreativeModeTab.Output entries) {
        // emerald
        entries.accept(new ItemStack(ModItems.EMERALD_CRYSTAL_BLOCK));
        entries.accept(new ItemStack(ModItems.BUDDING_EMERALD));
        entries.accept(new ItemStack(ModItems.EMERALD_CLUSTER));
        entries.accept(new ItemStack(ModItems.LARGE_EMERALD_BUD));
        entries.accept(new ItemStack(ModItems.MEDIUM_EMERALD_BUD));
        entries.accept(new ItemStack(ModItems.SMALL_EMERALD_BUD));

        // quartz
        entries.accept(new ItemStack(ModItems.QUARTZ_CRYSTAL_BLOCK));
        entries.accept(new ItemStack(ModItems.BUDDING_QUARTZ));
        entries.accept(new ItemStack(ModItems.QUARTZ_CLUSTER));
        entries.accept(new ItemStack(ModItems.LARGE_QUARTZ_BUD));
        entries.accept(new ItemStack(ModItems.MEDIUM_QUARTZ_BUD));
        entries.accept(new ItemStack(ModItems.SMALL_QUARTZ_BUD));

        // diamond
        entries.accept(new ItemStack(ModItems.DIAMOND_CRYSTAL_BLOCK));
        entries.accept(new ItemStack(ModItems.DIAMOND_CLUSTER));

        // echo
        entries.accept(new ItemStack(ModItems.ECHO_BLOCK));
        entries.accept(new ItemStack(ModItems.BUDDING_ECHO_BLOCK));
        entries.accept(new ItemStack(ModItems.ECHO_CLUSTER));
        entries.accept(new ItemStack(ModItems.LARGE_ECHO_BUD));
        entries.accept(new ItemStack(ModItems.MEDIUM_ECHO_BUD));
        entries.accept(new ItemStack(ModItems.SMALL_ECHO_BUD));

        // lapis
        entries.accept(new ItemStack(ModItems.LAPIS_CRYSTAL_BLOCK));
        entries.accept(new ItemStack(ModItems.BUDDING_LAPIS));
        entries.accept(new ItemStack(ModItems.LAPIS_CLUSTER));
        entries.accept(new ItemStack(ModItems.LARGE_LAPIS_BUD));
        entries.accept(new ItemStack(ModItems.MEDIUM_LAPIS_BUD));
        entries.accept(new ItemStack(ModItems.SMALL_LAPIS_BUD));

        // gypsum
        entries.accept(new ItemStack(ModItems.GYPSUM_CRYSTAL_BLOCK));
        entries.accept(new ItemStack(ModItems.BUDDING_GYPSUM));
        entries.accept(new ItemStack(ModItems.GYPSUM_ROSE));
        entries.accept(new ItemStack(ModItems.LARGE_GYPSUM_BUD));
        entries.accept(new ItemStack(ModItems.MEDIUM_GYPSUM_BUD));
        entries.accept(new ItemStack(ModItems.SMALL_GYPSUM_BUD));
        entries.accept(new ItemStack(ModItems.GYPSUM_SHARD));

        // pyrite and calcite
        entries.accept(new ItemStack(ModItems.PYRITE_CHUNK));
        entries.accept(new ItemStack(ModItems.PYRITE));
        entries.accept(new ItemStack(ModItems.PYRITE_STAIRS));
        entries.accept(new ItemStack(ModItems.PYRITE_SLAB));
        entries.accept(new ItemStack(ModItems.PYRITE_WALL));
        entries.accept(new ItemStack(Items.CALCITE));
        entries.accept(new ItemStack(ModItems.CALCITE_STAIRS));
        entries.accept(new ItemStack(ModItems.CALCITE_SLAB));
        entries.accept(new ItemStack(ModItems.CALCITE_WALL));

        entries.accept(new ItemStack(ModItems.GABRRO));

        // gypsum blocks
        entries.accept(new ItemStack(ModItems.CHISELED_GYPSUM));
        entries.accept(new ItemStack(ModItems.GYPSUM_PILLAR));
        entries.accept(new ItemStack(ModItems.POLISHED_GYPSUM_BLOCK));
        entries.accept(new ItemStack(ModItems.POLISHED_GYPSUM_STAIRS));
        entries.accept(new ItemStack(ModItems.POLISHED_GYPSUM_SLAB));
        entries.accept(new ItemStack(ModItems.POLISHED_GYPSUM_WALL));
        entries.accept(new ItemStack(ModItems.SMOOTH_GYPSUM_BLOCK));
        entries.accept(new ItemStack(ModItems.SMOOTH_GYPSUM_STAIRS));
        entries.accept(new ItemStack(ModItems.SMOOTH_GYPSUM_SLAB));
        entries.accept(new ItemStack(ModItems.SMOOTH_GYPSUM_WALL));

        // locators
        entries.accept(new ItemStack(ModItems.CRYSTAL_LOCATOR));
        entries.accept(new ItemStack(ModItems.ECHO_LOCATOR));

        for (ItemStack stack : getTunedCrystalLocators()) {
            entries.accept(stack);
        }
    }

    private static List<ItemStack> getTunedCrystalLocators() {
        List<ItemStack> entries = new ArrayList<>(
                List.of(
                        makeTunedLocatorStack("geodes:amethyst"),
                        makeTunedLocatorStack("geodes:emerald"),
                        makeTunedLocatorStack("geodes:quartz"),
                        makeTunedLocatorStack("geodes:diamond"),
                        makeTunedLocatorStack("geodes:echo"),
                        makeTunedLocatorStack("geodes:lapis"),
                        makeTunedLocatorStack("geodes:gypsum")
                )
        );

        if (MoreGeodes.isSpectrumLoaded()) {
            entries.add(makeTunedLocatorStack("geodes:spectrum/topaz"));
            entries.add(makeTunedLocatorStack("geodes:spectrum/citrine"));
            entries.add(makeTunedLocatorStack("geodes:spectrum/moonstone"));
            entries.add(makeTunedLocatorStack("geodes:spectrum/bismuth"));
        }

        return entries;
    }

    public static ItemStack makeTunedLocatorStack(String tuningId) {
        return TunedLocatorData.makeTunedLocatorStack(tuningId);
    }
}
