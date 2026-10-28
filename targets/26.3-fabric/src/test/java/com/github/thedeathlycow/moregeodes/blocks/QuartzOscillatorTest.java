package com.github.thedeathlycow.moregeodes.blocks;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuartzOscillatorTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    private static int onTimeOf(Object block) {
        try {
            Field field = block.getClass().getDeclaredField("ON_TIME");
            field.setAccessible(true);
            return field.getInt(block);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void onTimesMatchUpstream() {
        assertEquals(20, onTimeOf(ModBlocks.SMALL_QUARTZ_BUD), "small quartz bud must be a 0.5 Hz clock");
        assertEquals(10, onTimeOf(ModBlocks.MEDIUM_QUARTZ_BUD), "medium quartz bud must be a 1 Hz clock");
        assertEquals(5, onTimeOf(ModBlocks.LARGE_QUARTZ_BUD), "large quartz bud must be a 2 Hz clock");
        assertEquals(1, onTimeOf(ModBlocks.QUARTZ_CLUSTER), "quartz cluster must be a 10 Hz clock");
    }

    @Test
    void everyQuartzStageIsASignalSourceEmitting15WhenPowered() {
        for (Object block : new Object[]{
                ModBlocks.SMALL_QUARTZ_BUD, ModBlocks.MEDIUM_QUARTZ_BUD,
                ModBlocks.LARGE_QUARTZ_BUD, ModBlocks.QUARTZ_CLUSTER
        }) {
            QuartzClusterBlock quartz = (QuartzClusterBlock) block;
            BlockState unpowered = quartz.defaultBlockState().setValue(QuartzClusterBlock.POWERED, false);
            BlockState powered = quartz.defaultBlockState().setValue(QuartzClusterBlock.POWERED, true);

            assertTrue(quartz.isSignalSource(unpowered), "quartz stage must be a signal source");
            assertEquals(15, quartz.getSignal(powered, null, BlockPos.ZERO, Direction.UP),
                    "powered quartz stage must emit strength 15");
            assertEquals(0, quartz.getSignal(unpowered, null, BlockPos.ZERO, Direction.UP),
                    "unpowered quartz stage must emit no signal");
        }
    }

    @Test
    void quartzBudsDefaultToUnpowered() {
        for (Object block : new Object[]{
                ModBlocks.SMALL_QUARTZ_BUD, ModBlocks.MEDIUM_QUARTZ_BUD,
                ModBlocks.LARGE_QUARTZ_BUD, ModBlocks.QUARTZ_CLUSTER
        }) {
            QuartzClusterBlock quartz = (QuartzClusterBlock) block;
            assertFalse(quartz.defaultBlockState().getValue(QuartzClusterBlock.POWERED),
                    quartz + " must default to unpowered");
        }
    }

    @Test
    void buddingQuartzIsNotHarvestableButQuartzClusterIs() {
        assertTrue(ModBlocks.BUDDING_QUARTZ instanceof GeodeBuddingBlock);
        assertTrue(ModBlocks.QUARTZ_CLUSTER instanceof QuartzClusterBlock);
    }
}
