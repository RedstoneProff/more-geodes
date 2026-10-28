package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item EMERALD_SHARD = new Item(GeodesItemProperties.of("emerald_shard"));
    public static final Item PYRITE_CHUNK = new Item(GeodesItemProperties.of("pyrite_chunk"));
    public static final Item GYPSUM_SHARD = new Item(GeodesItemProperties.of("gypsum_shard"));

    public static final Item EMERALD_CRYSTAL_BLOCK = new BlockItem(ModBlocks.EMERALD_CRYSTAL_BLOCK, GeodesItemProperties.of("emerald_crystal_block"));
    public static final Item BUDDING_EMERALD = new BlockItem(ModBlocks.BUDDING_EMERALD, GeodesItemProperties.of("budding_emerald"));
    public static final Item SMALL_EMERALD_BUD = new BlockItem(ModBlocks.SMALL_EMERALD_BUD, GeodesItemProperties.of("small_emerald_bud"));
    public static final Item MEDIUM_EMERALD_BUD = new BlockItem(ModBlocks.MEDIUM_EMERALD_BUD, GeodesItemProperties.of("medium_emerald_bud"));
    public static final Item LARGE_EMERALD_BUD = new BlockItem(ModBlocks.LARGE_EMERALD_BUD, GeodesItemProperties.of("large_emerald_bud"));
    public static final Item EMERALD_CLUSTER = new BlockItem(ModBlocks.EMERALD_CLUSTER, GeodesItemProperties.of("emerald_cluster"));

    public static final Item QUARTZ_CRYSTAL_BLOCK = new BlockItem(ModBlocks.QUARTZ_CRYSTAL_BLOCK, GeodesItemProperties.of("quartz_crystal_block"));
    public static final Item BUDDING_QUARTZ = new BlockItem(ModBlocks.BUDDING_QUARTZ, GeodesItemProperties.of("budding_quartz"));
    public static final Item SMALL_QUARTZ_BUD = new BlockItem(ModBlocks.SMALL_QUARTZ_BUD, GeodesItemProperties.of("small_quartz_bud"));
    public static final Item MEDIUM_QUARTZ_BUD = new BlockItem(ModBlocks.MEDIUM_QUARTZ_BUD, GeodesItemProperties.of("medium_quartz_bud"));
    public static final Item LARGE_QUARTZ_BUD = new BlockItem(ModBlocks.LARGE_QUARTZ_BUD, GeodesItemProperties.of("large_quartz_bud"));
    public static final Item QUARTZ_CLUSTER = new BlockItem(ModBlocks.QUARTZ_CLUSTER, GeodesItemProperties.of("quartz_cluster"));

    public static final Item DIAMOND_CRYSTAL_BLOCK = new BlockItem(ModBlocks.DIAMOND_CRYSTAL_BLOCK, GeodesItemProperties.of("diamond_crystal_block"));
    public static final Item DIAMOND_CLUSTER = new BlockItem(ModBlocks.DIAMOND_CLUSTER, GeodesItemProperties.of("diamond_cluster"));

    public static final Item ECHO_BLOCK = new BlockItem(ModBlocks.ECHO_BLOCK, GeodesItemProperties.of("echo_block"));
    public static final Item BUDDING_ECHO_BLOCK = new BlockItem(ModBlocks.BUDDING_ECHO_BLOCK, GeodesItemProperties.of("budding_echo_block"));
    public static final Item SMALL_ECHO_BUD = new BlockItem(ModBlocks.SMALL_ECHO_BUD, GeodesItemProperties.of("small_echo_bud"));
    public static final Item MEDIUM_ECHO_BUD = new BlockItem(ModBlocks.MEDIUM_ECHO_BUD, GeodesItemProperties.of("medium_echo_bud"));
    public static final Item LARGE_ECHO_BUD = new BlockItem(ModBlocks.LARGE_ECHO_BUD, GeodesItemProperties.of("large_echo_bud"));
    public static final Item ECHO_CLUSTER = new BlockItem(ModBlocks.ECHO_CLUSTER, GeodesItemProperties.of("echo_cluster"));

    public static final Item LAPIS_CRYSTAL_BLOCK = new BlockItem(ModBlocks.LAPIS_CRYSTAL_BLOCK, GeodesItemProperties.of("lapis_crystal_block"));

    public static final Item BUDDING_LAPIS = new BlockItem(ModBlocks.BUDDING_LAPIS, GeodesItemProperties.of("budding_lapis"));

    public static final Item SMALL_LAPIS_BUD = new BlockItem(ModBlocks.SMALL_LAPIS_BUD, GeodesItemProperties.of("small_lapis_bud"));
    public static final Item MEDIUM_LAPIS_BUD = new BlockItem(ModBlocks.MEDIUM_LAPIS_BUD, GeodesItemProperties.of("medium_lapis_bud"));
    public static final Item LARGE_LAPIS_BUD = new BlockItem(ModBlocks.LARGE_LAPIS_BUD, GeodesItemProperties.of("large_lapis_bud"));
    public static final Item LAPIS_CLUSTER = new BlockItem(ModBlocks.LAPIS_CLUSTER, GeodesItemProperties.of("lapis_cluster"));

    public static final Item GYPSUM_CRYSTAL_BLOCK = new BlockItem(ModBlocks.GYPSUM_CRYSTAL_BLOCK, GeodesItemProperties.of("gypsum_crystal_block"));
    public static final Item BUDDING_GYPSUM = new BlockItem(ModBlocks.BUDDING_GYPSUM, GeodesItemProperties.of("budding_gypsum"));
    public static final Item SMALL_GYPSUM_BUD = new BlockItem(ModBlocks.SMALL_GYPSUM_BUD, GeodesItemProperties.of("small_gypsum_bud"));
    public static final Item MEDIUM_GYPSUM_BUD = new BlockItem(ModBlocks.MEDIUM_GYPSUM_BUD, GeodesItemProperties.of("medium_gypsum_bud"));
    public static final Item LARGE_GYPSUM_BUD = new BlockItem(ModBlocks.LARGE_GYPSUM_BUD, GeodesItemProperties.of("large_gypsum_bud"));
    public static final Item GYPSUM_ROSE = new BlockItem(ModBlocks.GYPSUM_ROSE, GeodesItemProperties.of("gypsum_rose"));


    public static final Item PYRITE = new BlockItem(ModBlocks.PYRITE, GeodesItemProperties.of("pyrite"));
    public static final Item PYRITE_STAIRS = new BlockItem(ModBlocks.PYRITE_STAIRS, GeodesItemProperties.of("pyrite_stairs"));
    public static final Item PYRITE_SLAB = new BlockItem(ModBlocks.PYRITE_SLAB, GeodesItemProperties.of("pyrite_slab"));
    public static final Item PYRITE_WALL = new BlockItem(ModBlocks.PYRITE_WALL, GeodesItemProperties.of("pyrite_wall"));

    public static final Item CALCITE_STAIRS = new BlockItem(ModBlocks.CALCITE_STAIRS, GeodesItemProperties.of("calcite_stairs"));
    public static final Item CALCITE_SLAB = new BlockItem(ModBlocks.CALCITE_SLAB, GeodesItemProperties.of("calcite_slab"));
    public static final Item CALCITE_WALL = new BlockItem(ModBlocks.CALCITE_WALL, GeodesItemProperties.of("calcite_wall"));

    public static final Item CRYSTAL_LOCATOR = new CrystalLocatorItem(
            GeodesItemProperties.of("crystal_locator").durability(16),
            CrystalLocatorItem.CRYSTAL_LOCATOR_RANGE,
            CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN
    );

    public static final Item TUNED_CRYSTAL_LOCATOR = new TunedCrystalLocatorItem(
            GeodesItemProperties.of("tuned_crystal_locator").durability(16),
            CrystalLocatorItem.CRYSTAL_LOCATOR_RANGE,
            CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN
    );

    public static final Item ECHO_LOCATOR = new EchoLocatorItem(
            GeodesItemProperties.of("echo_locator").durability(32),
            EchoLocatorItem.BLOCK_RANGE,
            EchoLocatorItem.ECHO_LOCATOR_COOL_DOWN
    );

    public static final Item GABRRO = new BlockItem(ModBlocks.GABBRO, GeodesItemProperties.of("gabbro"));

    public static final Item CHISELED_GYPSUM = new BlockItem(ModBlocks.CHISELED_GYPSUM, GeodesItemProperties.of("chiseled_gypsum"));
    public static final Item GYPSUM_PILLAR = new BlockItem(ModBlocks.GYPSUM_PILLAR, GeodesItemProperties.of("gypsum_pillar"));

    public static final Item POLISHED_GYPSUM_BLOCK = new BlockItem(ModBlocks.POLISHED_GYPSUM_BLOCK, GeodesItemProperties.of("polished_gypsum_block"));
    public static final Item POLISHED_GYPSUM_STAIRS = new BlockItem(ModBlocks.POLISHED_GYPSUM_STAIRS, GeodesItemProperties.of("polished_gypsum_stairs"));
    public static final Item POLISHED_GYPSUM_SLAB = new BlockItem(ModBlocks.POLISHED_GYPSUM_SLAB, GeodesItemProperties.of("polished_gypsum_slab"));
    public static final Item POLISHED_GYPSUM_WALL = new BlockItem(ModBlocks.POLISHED_GYPSUM_WALL, GeodesItemProperties.of("polished_gypsum_wall"));

    public static final Item SMOOTH_GYPSUM_BLOCK = new BlockItem(ModBlocks.SMOOTH_GYPSUM_BLOCK, GeodesItemProperties.of("smooth_gypsum_block"));
    public static final Item SMOOTH_GYPSUM_STAIRS = new BlockItem(ModBlocks.SMOOTH_GYPSUM_STAIRS, GeodesItemProperties.of("smooth_gypsum_stairs"));
    public static final Item SMOOTH_GYPSUM_SLAB = new BlockItem(ModBlocks.SMOOTH_GYPSUM_SLAB, GeodesItemProperties.of("smooth_gypsum_slab"));
    public static final Item SMOOTH_GYPSUM_WALL = new BlockItem(ModBlocks.SMOOTH_GYPSUM_WALL, GeodesItemProperties.of("smooth_gypsum_wall"));

    public static void registerItems() {
        TunedLocatorData.register();

        register("emerald_shard", EMERALD_SHARD);
        register("pyrite_chunk", PYRITE_CHUNK);
        register("gypsum_shard", GYPSUM_SHARD);
        register("emerald_crystal_block", EMERALD_CRYSTAL_BLOCK);
        register("budding_emerald", BUDDING_EMERALD);
        register("small_emerald_bud", SMALL_EMERALD_BUD);
        register("medium_emerald_bud", MEDIUM_EMERALD_BUD);
        register("large_emerald_bud", LARGE_EMERALD_BUD);
        register("emerald_cluster", EMERALD_CLUSTER);
        register("quartz_crystal_block", QUARTZ_CRYSTAL_BLOCK);
        register("budding_quartz", BUDDING_QUARTZ);
        register("small_quartz_bud", SMALL_QUARTZ_BUD);
        register("medium_quartz_bud", MEDIUM_QUARTZ_BUD);
        register("large_quartz_bud", LARGE_QUARTZ_BUD);
        register("quartz_cluster", QUARTZ_CLUSTER);
        register("diamond_crystal_block", DIAMOND_CRYSTAL_BLOCK);
        register("diamond_cluster", DIAMOND_CLUSTER);
        register("echo_block", ECHO_BLOCK);
        register("budding_echo_block", BUDDING_ECHO_BLOCK);
        register("small_echo_bud", SMALL_ECHO_BUD);
        register("medium_echo_bud", MEDIUM_ECHO_BUD);
        register("large_echo_bud", LARGE_ECHO_BUD);
        register("echo_cluster", ECHO_CLUSTER);
        register("lapis_crystal_block", LAPIS_CRYSTAL_BLOCK);
        register("small_lapis_bud", SMALL_LAPIS_BUD);
        register("medium_lapis_bud", MEDIUM_LAPIS_BUD);
        register("large_lapis_bud", LARGE_LAPIS_BUD);
        register("lapis_cluster", LAPIS_CLUSTER);
        register("budding_lapis", BUDDING_LAPIS);
        register("gypsum_crystal_block", GYPSUM_CRYSTAL_BLOCK);
        register("budding_gypsum", BUDDING_GYPSUM);
        register("small_gypsum_bud", SMALL_GYPSUM_BUD);
        register("medium_gypsum_bud", MEDIUM_GYPSUM_BUD);
        register("large_gypsum_bud", LARGE_GYPSUM_BUD);
        register("gypsum_rose", GYPSUM_ROSE);
        register("crystal_locator", CRYSTAL_LOCATOR);
        register("tuned_crystal_locator", TUNED_CRYSTAL_LOCATOR);
        register("echo_locator", ECHO_LOCATOR);
        register("pyrite", PYRITE);
        register("pyrite_stairs", PYRITE_STAIRS);
        register("pyrite_slab", PYRITE_SLAB);
        register("pyrite_wall", PYRITE_WALL);
        register("calcite_stairs", CALCITE_STAIRS);
        register("calcite_slab", CALCITE_SLAB);
        register("calcite_wall", CALCITE_WALL);
        register("gabbro", GABRRO);
        register("chiseled_gypsum", CHISELED_GYPSUM);
        register("gypsum_pillar", GYPSUM_PILLAR);
        register("polished_gypsum_block", POLISHED_GYPSUM_BLOCK);
        register("polished_gypsum_stairs", POLISHED_GYPSUM_STAIRS);
        register("polished_gypsum_slab", POLISHED_GYPSUM_SLAB);
        register("polished_gypsum_wall", POLISHED_GYPSUM_WALL);
        register("smooth_gypsum_block", SMOOTH_GYPSUM_BLOCK);
        register("smooth_gypsum_stairs", SMOOTH_GYPSUM_STAIRS);
        register("smooth_gypsum_slab", SMOOTH_GYPSUM_SLAB);
        register("smooth_gypsum_wall", SMOOTH_GYPSUM_WALL);
    }

    private static void register(String name, Item item) {
        Registry.register(BuiltInRegistries.ITEM, GeodesIds.of(MoreGeodes.MODID, name), item);
    }
}
