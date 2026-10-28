package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

public class QuartzOscillatorTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final BlockPos POWER_SOURCE = POS.south();

    private static void placePoweredBud(GameTestHelper context, com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock block) {
        context.setBlock(POS, block.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(QuartzClusterBlock.POWERED, false));
        context.setBlock(POWER_SOURCE, Blocks.REDSTONE_BLOCK);
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void small_quartz_bud_oscillates_at_half_hertz(GameTestHelper context) {
        placePoweredBud(context, (QuartzClusterBlock) ModBlocks.SMALL_QUARTZ_BUD);

        context.runAtTickTime(25, () -> {
            context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, true);
            int signal = context.getBlockState(POS).getSignal(context.getLevel(), context.absolutePos(POS), Direction.UP);
            context.assertTrue(signal == 15, "powered quartz bud must emit signal 15, got " + signal);
        });
        context.runAtTickTime(45, () -> {
            context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, false);
            context.succeed();
        });
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void quartz_cluster_oscillates_at_ten_hertz(GameTestHelper context) {
        placePoweredBud(context, (QuartzClusterBlock) ModBlocks.QUARTZ_CLUSTER);

        context.runAtTickTime(1, () -> context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, true));
        context.runAtTickTime(2, () -> context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, false));
        context.runAtTickTime(3, () -> {
            context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, true);
            context.succeed();
        });
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void removing_power_resets_the_oscillator_to_off(GameTestHelper context) {
        placePoweredBud(context, (QuartzClusterBlock) ModBlocks.SMALL_QUARTZ_BUD);

        context.runAtTickTime(25, () -> context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, true));
        context.runAtTickTime(30, () -> context.setBlock(POWER_SOURCE, Blocks.AIR));
        context.runAtTickTime(55, () -> {
            context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, false);
            context.succeed();
        });
    }
}
