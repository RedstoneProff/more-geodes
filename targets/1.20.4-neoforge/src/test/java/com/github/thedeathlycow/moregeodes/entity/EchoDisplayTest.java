package com.github.thedeathlycow.moregeodes.entity;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.sounds.GeodesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class EchoDisplayTest {
    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
    }

    private static EchoDisplay newDisplay(Level level) {
        return new EchoDisplay(GeodesEntityTypes.ECHO_DISPLAY, level);
    }

    private static int getTimeToLive(EchoDisplay display) throws Exception {
        Method m = EchoDisplay.class.getDeclaredMethod("getTimeToLive");
        m.setAccessible(true);
        return (int) m.invoke(display);
    }

    private static int getGlowTime(EchoDisplay display) throws Exception {
        Method m = EchoDisplay.class.getDeclaredMethod("getGlowTime");
        m.setAccessible(true);
        return (int) m.invoke(display);
    }

    @Test
    void displaySpawnsInvisibleAndInvulnerable() {
        Level level = mock(Level.class);
        EchoDisplay display = newDisplay(level);
        assertTrue(display.isInvisible());
        assertTrue(display.isInvulnerable());
    }

    @Test
    void glowDelayExtendsTimeToLive() throws Exception {
        Level level = mock(Level.class);
        EchoDisplay display = newDisplay(level);
        int before = getTimeToLive(display);
        display.setGlowDelay(7);
        assertEquals(before + 7, getTimeToLive(display));
        assertEquals(before, getGlowTime(display), "the glow time must stay at the pre-delay ttl");
    }

    @Test
    void tickPingsOnceAfterGlowDelayThenDiscards() {
        Level level = mock(Level.class);
        EchoDisplay display = newDisplay(level);
        display.setPos(0.5, 1.0, 0.5);
        display.setGlowDelay(2);

        display.tick();
        assertTrue(display.isInvisible(), "must stay invisible until the glow delay elapses");
        verify(level, never()).gameEvent(any(), any(), any(BlockPos.class));

        display.tick();
        assertFalse(display.isInvisible());
        verify(level).gameEvent(eq(display), any(), any(BlockPos.class));
        verify(level).playSound(any(), any(BlockPos.class),
                eq(GeodesSoundEvents.BLOCK_ECHO_LOCATOR_RESONATE), eq(net.minecraft.sounds.SoundSource.BLOCKS),
                anyFloat(), anyFloat());

        for (int i = 0; i < 64 && !display.isRemoved(); i++) {
            display.tick();
        }
        assertTrue(display.isRemoved(), "the ping must discard itself after its ttl");
    }

    @Test
    void saveAndLoadRoundTripsThePingState() throws Exception {
        Level level = mock(Level.class);
        org.mockito.Mockito.when(level.holderLookup(net.minecraft.core.registries.Registries.BLOCK))
                .thenReturn(org.mockito.Mockito.mock(
                        net.minecraft.core.HolderLookup.RegistryLookup.class));
        EchoDisplay display = newDisplay(level);
        display.setGlowDelay(3);

        CompoundTag nbt = new CompoundTag();
        Method add = EchoDisplay.class.getDeclaredMethod("addAdditionalSaveData", CompoundTag.class);
        add.setAccessible(true);
        add.invoke(display, nbt);

        EchoDisplay restored = newDisplay(level);
        Method read = EchoDisplay.class.getDeclaredMethod("readAdditionalSaveData", CompoundTag.class);
        read.setAccessible(true);
        read.invoke(restored, nbt);

        assertEquals(getTimeToLive(display), getTimeToLive(restored));
        assertEquals(getGlowTime(display), getGlowTime(restored));
    }

    @Test
    void displayIsABlockDisplay() {
        Level level = mock(Level.class);
        EchoDisplay display = newDisplay(level);
        assertTrue(display instanceof Display.BlockDisplay);
        assertEquals(GeodesEntityTypes.ECHO_DISPLAY, display.getType());
    }
}
