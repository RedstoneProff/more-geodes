package com.github.thedeathlycow.moregeodes;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ModBootstrapTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    @Test
    void initRegistersAllBlocks() {
        assertNotNull(ModBlocks.EMERALD_CLUSTER);
        assertNotNull(ModBlocks.BUDDING_EMERALD);
        assertNotNull(ModBlocks.QUARTZ_CLUSTER);
        assertNotNull(ModBlocks.BUDDING_QUARTZ);
        assertNotNull(ModBlocks.DIAMOND_CLUSTER);
        assertNotNull(ModBlocks.BUDDING_ECHO_BLOCK);
        assertNotNull(ModBlocks.ECHO_CLUSTER);
        assertNotNull(ModBlocks.BUDDING_LAPIS);
        assertNotNull(ModBlocks.LAPIS_CLUSTER);
        assertNotNull(ModBlocks.BUDDING_GYPSUM);
        assertNotNull(ModBlocks.GYPSUM_ROSE);
        assertNotNull(ModBlocks.PYRITE);
        assertNotNull(ModBlocks.CALCITE_STAIRS);
        assertNotNull(ModBlocks.GABBRO);
    }

    @Test
    void initRegistersAllItems() {
        assertNotNull(com.github.thedeathlycow.moregeodes.item.ModItems.EMERALD_SHARD);
        assertNotNull(com.github.thedeathlycow.moregeodes.item.ModItems.CRYSTAL_LOCATOR);
        assertNotNull(com.github.thedeathlycow.moregeodes.item.ModItems.TUNED_CRYSTAL_LOCATOR);
        assertNotNull(com.github.thedeathlycow.moregeodes.item.ModItems.ECHO_LOCATOR);
        assertNotNull(com.github.thedeathlycow.moregeodes.item.ModItems.GYPSUM_SHARD);
        assertNotNull(com.github.thedeathlycow.moregeodes.item.ModItems.PYRITE_CHUNK);
    }

    @Test
    void registeredBlocksAreReachableThroughTheBlockRegistry() {
        assertEquals(ModBlocks.EMERALD_CLUSTER,
                net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        .getValue(com.github.thedeathlycow.moregeodes.util.GeodesIds.of(MoreGeodes.MODID, "emerald_cluster")));
        assertEquals(ModBlocks.BUDDING_GYPSUM,
                net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        .getValue(com.github.thedeathlycow.moregeodes.util.GeodesIds.of(MoreGeodes.MODID, "budding_gypsum")));
    }

    @Test
    void modLoadedCheckerDefaultsToFalse() {
        MoreGeodes.setModLoadedChecker(id -> false);
        assertSame(false, MoreGeodes.isCarpetLoaded());
        assertSame(false, MoreGeodes.isAE2Loaded());
        assertSame(false, MoreGeodes.isSpectrumLoaded());
        assertSame(false, MoreGeodes.isMovableAmethystEnabled());
    }

    @Test
    void modLoadedCheckerIsPluggable() {
        MoreGeodes.setModLoadedChecker("carpet"::equals);
        assertSame(true, MoreGeodes.isCarpetLoaded());
        assertSame(false, MoreGeodes.isAE2Loaded());
        assertSame(false, MoreGeodes.isMovableAmethystEnabled());
        MoreGeodes.setModLoadedChecker(id -> false);
    }
}
