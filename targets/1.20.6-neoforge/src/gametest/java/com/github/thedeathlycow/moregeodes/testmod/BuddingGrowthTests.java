package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class BuddingGrowthTests {
    private static final BlockPos CENTER = MoreGeodesTestHelper.CENTER;

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_016_budding_emerald_grows_bud_on_random_tick(GameTestHelper context) {
        context.setBlock(CENTER, ModBlocks.BUDDING_EMERALD);

        MoreGeodesTestHelper.randomTickTimes(context, CENTER, 300);

        context.assertBlock(
                CENTER,
                block -> block == ModBlocks.BUDDING_EMERALD,
                "budding emerald must survive its own growth"
        );
        MoreGeodesTestHelper.assertAnyNeighborIs(
                context,
                CENTER,
                "no emerald bud grew after 300 random ticks",
                ModBlocks.SMALL_EMERALD_BUD,
                ModBlocks.MEDIUM_EMERALD_BUD,
                ModBlocks.LARGE_EMERALD_BUD,
                ModBlocks.EMERALD_CLUSTER
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_016_budding_quartz_grows_quartz_bud_on_random_tick(GameTestHelper context) {
        context.setBlock(CENTER, ModBlocks.BUDDING_QUARTZ);

        MoreGeodesTestHelper.randomTickTimes(context, CENTER, 300);

        context.assertBlock(
                CENTER,
                block -> block == ModBlocks.BUDDING_QUARTZ,
                "budding quartz must survive its own growth"
        );
        MoreGeodesTestHelper.assertAnyNeighborIs(
                context,
                CENTER,
                "no quartz bud grew after 300 random ticks",
                ModBlocks.SMALL_QUARTZ_BUD,
                ModBlocks.MEDIUM_QUARTZ_BUD,
                ModBlocks.LARGE_QUARTZ_BUD,
                ModBlocks.QUARTZ_CLUSTER
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_016_growth_chain_advances_to_cluster_and_budding_survives(GameTestHelper context) {
        context.setBlock(CENTER, ModBlocks.BUDDING_EMERALD);

        MoreGeodesTestHelper.randomTickTimes(context, CENTER, 2000);

        context.assertBlock(
                CENTER,
                block -> block == ModBlocks.BUDDING_EMERALD,
                "budding emerald must survive its own growth"
        );
        context.assertBlock(
                CENTER,
                block -> MoreGeodesTestHelper.anyNeighborIs(
                        context,
                        CENTER,
                        ModBlocks.EMERALD_CLUSTER
                ),
                "no emerald cluster formed after 2000 random ticks"
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_007_budding_gypsum_grows_only_upward(GameTestHelper context) {
        context.setBlock(CENTER, ModBlocks.BUDDING_GYPSUM);

        MoreGeodesTestHelper.randomTickTimes(context, CENTER, 2000);

        context.assertBlock(
                CENTER,
                block -> block == ModBlocks.BUDDING_GYPSUM,
                "budding gypsum must survive its own growth"
        );
        context.assertBlock(
                CENTER.above(),
                block -> block == ModBlocks.SMALL_GYPSUM_BUD
                        || block == ModBlocks.MEDIUM_GYPSUM_BUD
                        || block == ModBlocks.LARGE_GYPSUM_BUD
                        || block == ModBlocks.GYPSUM_ROSE,
                "budding gypsum did not grow upward"
        );
        MoreGeodesTestHelper.assertAllNeighborsAreAir(
                context,
                CENTER,
                "budding gypsum grew outside its growth direction",
                Direction.UP, Direction.DOWN
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_016_bud_grows_waterlogged_in_water(GameTestHelper context) {
        context.setBlock(CENTER, ModBlocks.BUDDING_EMERALD);
        for (Direction direction : MoreGeodesTestHelper.DIRECTIONS) {
            context.setBlock(CENTER.relative(direction), Blocks.WATER);
        }

        MoreGeodesTestHelper.randomTickTimes(context, CENTER, 2000);

        boolean grewWaterlogged = false;
        for (Direction direction : MoreGeodesTestHelper.DIRECTIONS) {
            BlockState state = context.getBlockState(CENTER.relative(direction));
            if (state.getBlock() instanceof CrystalClusterBlock
                    && state.getValue(CrystalClusterBlock.WATERLOGGED)) {
                grewWaterlogged = true;
                break;
            }
        }
        context.assertTrue(grewWaterlogged, "no waterlogged emerald bud grew in water");
        context.succeed();
    }
}
