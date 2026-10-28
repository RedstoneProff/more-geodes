package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.HashSet;
import java.util.Set;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class QuartzOscillatorTests {
    private static final BlockPos BASE = MoreGeodesTestHelper.CENTER;
    private static final BlockPos CRYSTAL = BASE.above();
    private static final BlockPos POWER = CRYSTAL.north();

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_003_quartz_cluster_oscillates_when_powered(GameTestHelper context) {
        context.setBlock(CRYSTAL, ModBlocks.QUARTZ_CLUSTER);
        context.setBlock(BASE, Blocks.REDSTONE_BLOCK);

        Set<Boolean> seenStates = new HashSet<>();
        context.onEachTick(() -> seenStates.add(
                context.getBlockState(CRYSTAL).getValue(QuartzClusterBlock.POWERED)
        ));

        context.startSequence()
                .thenIdle(10)
                .thenExecute(() -> context.assertTrue(
                        seenStates.contains(true) && seenStates.contains(false),
                        "quartz cluster did not oscillate while powered, saw states: " + seenStates
                ))
                .thenSucceed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_003_power_removed_resets_oscillator(GameTestHelper context) {
        context.setBlock(CRYSTAL, ModBlocks.QUARTZ_CLUSTER);
        context.setBlock(BASE, Blocks.STONE);
        context.setBlock(POWER, Blocks.REDSTONE_BLOCK);

        context.startSequence()
                .thenIdle(3)
                .thenExecute(() -> context.setBlock(POWER, Blocks.AIR))
                .thenIdle(2)
                .thenExecute(() -> context.assertBlockProperty(
                        CRYSTAL, QuartzClusterBlock.POWERED, false
                ))
                .thenIdle(10)
                .thenExecute(() -> context.assertBlockProperty(
                        CRYSTAL, QuartzClusterBlock.POWERED, false
                ))
                .thenSucceed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_003_small_quartz_bud_oscillates_every_20_ticks(GameTestHelper context) {
        context.setBlock(CRYSTAL, ModBlocks.SMALL_QUARTZ_BUD);
        context.setBlock(BASE, Blocks.REDSTONE_BLOCK);

        context.startSequence()
                .thenIdle(21)
                .thenExecute(() -> context.assertBlockProperty(
                        CRYSTAL, QuartzClusterBlock.POWERED, true
                ))
                .thenIdle(20)
                .thenExecute(() -> context.assertBlockProperty(
                        CRYSTAL, QuartzClusterBlock.POWERED, false
                ))
                .thenSucceed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_003_medium_quartz_bud_oscillates_every_10_ticks(GameTestHelper context) {
        context.setBlock(CRYSTAL, ModBlocks.MEDIUM_QUARTZ_BUD);
        context.setBlock(BASE, Blocks.REDSTONE_BLOCK);

        context.startSequence()
                .thenIdle(11)
                .thenExecute(() -> context.assertBlockProperty(
                        CRYSTAL, QuartzClusterBlock.POWERED, true
                ))
                .thenIdle(10)
                .thenExecute(() -> context.assertBlockProperty(
                        CRYSTAL, QuartzClusterBlock.POWERED, false
                ))
                .thenSucceed();
    }
}
