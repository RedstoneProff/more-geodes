package com.github.thedeathlycow.moregeodes.blocks;

import org.junit.jupiter.api.BeforeAll;import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuartzOscillatorTimingTest {
    @BeforeAll
    static void bootstrap() {
        com.github.thedeathlycow.moregeodes.TestBootstrap.registerModContent(
                java.nio.file.Path.of("build", "test-config-oscillator"));
    }

    private static int intField(Class<?> clazz, String name, Object instance) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.getInt(instance);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not read field " + name, e);
        }
    }

    @Test
    void quartzClusterOnTimeIsOneTick() {
        assertEquals(1, intField(QuartzClusterBlock.class, "ON_TIME", ModBlocks.QUARTZ_CLUSTER));
    }

    @Test
    void largeQuartzBudOnTimeIsFiveTicks() {
        assertEquals(5, intField(QuartzClusterBlock.class, "ON_TIME", ModBlocks.LARGE_QUARTZ_BUD));
    }

    @Test
    void mediumQuartzBudOnTimeIsTenTicks() {
        assertEquals(10, intField(QuartzClusterBlock.class, "ON_TIME", ModBlocks.MEDIUM_QUARTZ_BUD));
    }

    @Test
    void smallQuartzBudOnTimeIsTwentyTicks() {
        assertEquals(20, intField(QuartzClusterBlock.class, "ON_TIME", ModBlocks.SMALL_QUARTZ_BUD));
    }

    @Test
    void allQuartzOscillatorsEmitSignalFifteen() {
        for (Object block : new Object[]{
                ModBlocks.QUARTZ_CLUSTER, ModBlocks.LARGE_QUARTZ_BUD,
                ModBlocks.MEDIUM_QUARTZ_BUD, ModBlocks.SMALL_QUARTZ_BUD}) {
            assertEquals(15, intField(QuartzClusterBlock.class, "SIGNAL_STRENGTH", block),
                    "oscillator signal strength must be 15");
        }
    }

    @Test
    void quartzClustersAreSignalSources() {
        assertTrue(ModBlocks.QUARTZ_CLUSTER.isSignalSource(ModBlocks.QUARTZ_CLUSTER.defaultBlockState()));
        assertTrue(ModBlocks.SMALL_QUARTZ_BUD.isSignalSource(ModBlocks.SMALL_QUARTZ_BUD.defaultBlockState()));
    }

    @Test
    void unpoweredOscillatorsEmitNoSignal() {
        assertEquals(0, ModBlocks.QUARTZ_CLUSTER.getSignal(
                ModBlocks.QUARTZ_CLUSTER.defaultBlockState(), null, null, net.minecraft.core.Direction.NORTH));
    }
}
