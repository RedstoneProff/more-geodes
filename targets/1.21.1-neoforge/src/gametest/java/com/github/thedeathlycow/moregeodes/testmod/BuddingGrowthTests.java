package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class BuddingGrowthTests {
    private static final BlockPos BASE = new BlockPos(2, 2, 2);

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
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

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void budding_emerald_grows_a_waterlogged_bud_into_water(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());
        level.setBlockAndUpdate(absBase.above(), Blocks.WATER.defaultBlockState());

        context.succeedWhen(() -> {
            context.randomTick(BASE);
            BlockState above = level.getBlockState(absBase.above());
            context.assertTrue(above.getBlock() == ModBlocks.SMALL_EMERALD_BUD,
                    "no small emerald bud grew into the water above the budding block");
            context.assertTrue(above.getValue(CrystalClusterBlock.WATERLOGGED),
                    "a bud grown into water must be waterlogged");
        });
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void budding_emerald_never_grows_into_solid_blocks(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());
        level.setBlockAndUpdate(absBase.above(), Blocks.STONE.defaultBlockState());

        for (int i = 0; i < 300; i++) {
            context.randomTick(BASE);
        }
        context.assertTrue(level.getBlockState(absBase.above()).is(Blocks.STONE),
                "the budding block replaced a solid neighbour block");
        context.assertTrue(level.getBlockState(absBase).getBlock() == ModBlocks.BUDDING_EMERALD,
                "the budding block did not survive");
        context.succeed();
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void bud_grows_through_the_full_chain_and_stops_at_cluster(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        BlockPos absAbove = absBase.above();
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());
        level.setBlockAndUpdate(absAbove, ModBlocks.SMALL_EMERALD_BUD.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

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

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void cluster_is_terminal_and_budding_block_survives(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absBase = context.absolutePos(BASE);
        BlockPos absAbove = absBase.above();
        level.setBlockAndUpdate(absBase, ModBlocks.BUDDING_EMERALD.defaultBlockState());
        level.setBlockAndUpdate(absAbove, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

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

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
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
