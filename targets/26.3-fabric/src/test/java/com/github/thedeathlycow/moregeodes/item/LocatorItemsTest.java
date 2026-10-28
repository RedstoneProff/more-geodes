package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocatorItemsTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    @Test
    void crystalLocatorConstants() {
        assertEquals(48, CrystalLocatorItem.CRYSTAL_LOCATOR_RANGE);
        assertEquals(20, CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN);
        assertEquals(16, new ItemStack(ModItems.CRYSTAL_LOCATOR).getMaxDamage());
        assertEquals(16, new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR).getMaxDamage());
    }

    @Test
    void echoLocatorConstants() {
        assertEquals(6, EchoLocatorItem.BLOCK_RANGE);
        assertEquals(80, EchoLocatorItem.ECHO_LOCATOR_COOL_DOWN);
        assertEquals(32, new ItemStack(ModItems.ECHO_LOCATOR).getMaxDamage());
    }

    @Test
    void plainCrystalLocatorIsNeverTuned() {
        ItemStack stack = new ItemStack(ModItems.CRYSTAL_LOCATOR);
        assertTrue(!((CrystalLocatorItem) ModItems.CRYSTAL_LOCATOR).isTuned(stack, null));
    }

    @Test
    void echoLocatorIsAlwaysTuned() {
        assertTrue(((EchoLocatorItem) ModItems.ECHO_LOCATOR).isTuned(new ItemStack(ModItems.ECHO_LOCATOR), null));
    }

    @Test
    void tunedLocatorWithoutComponentIsNotTuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        // the tunings registry is not available before the dynamic registry sync
        assertTrue(!((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR).isTuned(stack, null));
    }
}
