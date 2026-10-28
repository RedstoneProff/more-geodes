package com.github.thedeathlycow.moregeodes.blocks;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrowthChainTest {
    @BeforeAll
    static void bootStrap() {
        TestBootstrap.registerModContent(java.nio.file.Path.of("build", "test-config"));
    }

    private static Optional<Block> next(GeodeBuddingBlock budding, BlockState current, Direction dir) {
        return budding.getNextBlockForGrowth(current, dir);
    }

    @Test
    void airGrowsTheFirstBudOfTheChain() {
        Optional<Block> next = next(ModBlocks.BUDDING_EMERALD, Blocks.AIR.defaultBlockState(), Direction.UP);
        assertTrue(next.isPresent(), "air must grow a new bud");
        assertEquals(ModBlocks.SMALL_EMERALD_BUD, next.get(), "the first bud of the emerald chain");
    }

    @Test
    void fullWaterSourceGrowsTheFirstBudOfTheChain() {
        Optional<Block> next = next(ModBlocks.BUDDING_EMERALD, Blocks.WATER.defaultBlockState(), Direction.UP);
        assertTrue(next.isPresent(), "a full water source must grow a new bud");
        assertEquals(ModBlocks.SMALL_EMERALD_BUD, next.get());
    }

    @Test
    void matchingFacingBudAdvancesAlongTheChain() {
        BlockState small = ModBlocks.SMALL_EMERALD_BUD.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        Optional<Block> next = next(ModBlocks.BUDDING_EMERALD, small, Direction.UP);
        assertTrue(next.isPresent(), "a matching small bud must advance");
        assertEquals(ModBlocks.MEDIUM_EMERALD_BUD, next.get());

        BlockState medium = ModBlocks.MEDIUM_EMERALD_BUD.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        assertEquals(Optional.of(ModBlocks.LARGE_EMERALD_BUD),
                next(ModBlocks.BUDDING_EMERALD, medium, Direction.UP));

        BlockState large = ModBlocks.LARGE_EMERALD_BUD.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        assertEquals(Optional.of(ModBlocks.EMERALD_CLUSTER),
                next(ModBlocks.BUDDING_EMERALD, large, Direction.UP));
    }

    @Test
    void terminalClusterGrowsNothing() {
        BlockState cluster = ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        Optional<Block> next = next(ModBlocks.BUDDING_EMERALD, cluster, Direction.UP);
        assertTrue(next.isEmpty(), "the terminal cluster must not grow further");
    }

    @Test
    void mismatchedFacingBudGrowsNothing() {
        BlockState small = ModBlocks.SMALL_EMERALD_BUD.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        Optional<Block> next = next(ModBlocks.BUDDING_EMERALD, small, Direction.NORTH);
        assertTrue(next.isEmpty(), "a bud facing another direction must not advance");
    }

    @Test
    void foreignBlockGrowsNothing() {
        Optional<Block> next = next(ModBlocks.BUDDING_EMERALD, Blocks.STONE.defaultBlockState(), Direction.UP);
        assertTrue(next.isEmpty(), "a solid non-crystal block must not be grown over");
    }

    @Test
    void emeraldChainIsSmallMediumLargeCluster() {
        assertEquals(
                java.util.List.of(ModBlocks.SMALL_EMERALD_BUD, ModBlocks.MEDIUM_EMERALD_BUD,
                        ModBlocks.LARGE_EMERALD_BUD, ModBlocks.EMERALD_CLUSTER),
                ModBlocks.BUDDING_EMERALD.getClusters(),
                "the emerald budding block must own the emerald bud chain in growth order"
        );
    }

    @Test
    void gypsumBuddingBlockIsDirectedUpward() {
        assertTrue(ModBlocks.BUDDING_GYPSUM instanceof DirectedBuddingBlock,
                "the gypsum budding block must only grow upward (DirectedBuddingBlock)");
        assertEquals(
                java.util.List.of(ModBlocks.SMALL_GYPSUM_BUD, ModBlocks.MEDIUM_GYPSUM_BUD,
                        ModBlocks.LARGE_GYPSUM_BUD, ModBlocks.GYPSUM_ROSE),
                ((GeodeBuddingBlock) ModBlocks.BUDDING_GYPSUM).getClusters()
        );
    }

    @Test
    void buddingBlocksAreNotPushableByPistons() {
        assertEquals(net.minecraft.world.level.material.PushReaction.DESTROY,
                ModBlocks.BUDDING_EMERALD.defaultBlockState().getPistonPushReaction(),
                "budding blocks must have a destroy push reaction");
        assertEquals(net.minecraft.world.level.material.PushReaction.DESTROY,
                ModBlocks.BUDDING_GYPSUM.defaultBlockState().getPistonPushReaction(),
                "budding blocks must have a destroy push reaction");
    }
}
