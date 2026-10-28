package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BuddingGrowthTests {
    private static final BlockPos BASE = new BlockPos(2, 2, 2);

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void budding_emerald_grows_a_small_bud_on_an_adjacent_face(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());

        context.succeedWhen(() -> {
            context.randomTick(BASE);
            boolean grew = false;
            for (Direction direction : Direction.values()) {
                if (level.getBlockState(absBase.relative(direction)).getBlock() == ModBlocks.SMALL_EMERALD_BUD) {
                    grew = true;
                }
            }
            context.assertTrue(grew, "no small emerald bud grew on any adjacent face");
            context.assertTrue(
                    level.getBlockState(absBase).getBlock() == ModBlocks.BUDDING_EMERALD,
                    "the budding block itself was consumed by growth");
        });
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void bud_grows_through_the_full_chain_and_stops_at_cluster(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        BlockPos absAbove = absBase.above();
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());
        level.setBlockAndUpdate(absAbove, ModBlocks.SMALL_EMERALD_BUD.defaultBlockState()
                .setValue(com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock.FACING, Direction.UP));

        for (int i = 0; i < 1000; i++) {
            context.randomTick(BASE);
        }
        context.assertTrue(
                level.getBlockState(absBase).getBlock() == ModBlocks.BUDDING_EMERALD,
                "the budding block itself was consumed by growth");
        context.assertTrue(
                level.getBlockState(absAbove).getBlock() == ModBlocks.EMERALD_CLUSTER,
                "bud above the budding block did not advance to the cluster");
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void cluster_is_terminal_and_budding_block_survives(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        BlockPos absAbove = absBase.above();
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());
        level.setBlockAndUpdate(absAbove, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock.FACING, Direction.UP));

        for (int i = 0; i < 200; i++) {
            context.randomTick(BASE);
        }
        context.assertTrue(
                level.getBlockState(absAbove).getBlock() == ModBlocks.EMERALD_CLUSTER,
                "the cluster changed under repeated random ticks");
        context.assertTrue(
                level.getBlockState(absBase).getBlock() == ModBlocks.BUDDING_EMERALD,
                "the budding block did not survive");
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void budding_gypsum_grows_only_upward(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_GYPSUM.defaultBlockState());

        for (int i = 0; i < 300; i++) {
            context.randomTick(BASE);
        }

        context.assertTrue(isGypsumGrowth(level.getBlockState(absBase.above())),
                "budding gypsum did not grow upward");
        for (Direction direction : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
            context.assertTrue(level.getBlockState(absBase.relative(direction)).isAir(),
                    "budding gypsum grew on a non-upward face: " + direction);
        }
        context.assertTrue(!isGypsumGrowth(level.getBlockState(absBase.below())),
                "budding gypsum grew downward into its support block");
        context.succeed();
    }

    private static boolean isGypsumGrowth(BlockState state) {
        return state.getBlock() == ModBlocks.SMALL_GYPSUM_BUD
                || state.getBlock() == ModBlocks.MEDIUM_GYPSUM_BUD
                || state.getBlock() == ModBlocks.LARGE_GYPSUM_BUD
                || state.getBlock() == ModBlocks.GYPSUM_ROSE;
    }
}
