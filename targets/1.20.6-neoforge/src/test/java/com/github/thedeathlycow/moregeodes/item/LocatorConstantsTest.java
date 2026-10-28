package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocatorConstantsTest {
    @TempDir
    static Path configDir;

    @BeforeAll
    static void bootStrap() {
        TestBootstrap.registerModContent(configDir);
    }

    @Test
    void crystalLocatorRangeAndCooldownMatchUpstream() {
        assertEquals(48, CrystalLocatorItem.CRYSTAL_LOCATOR_RANGE);
        assertEquals(20, CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN);
    }

    @Test
    void echoLocatorRangeIsSixBlocksAndCooldownIsFourTimesTheCrystalLocator() {
        assertEquals(6, EchoLocatorItem.BLOCK_RANGE);
        assertEquals(CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN * 4, EchoLocatorItem.ECHO_LOCATOR_COOL_DOWN);
        assertEquals(80, EchoLocatorItem.ECHO_LOCATOR_COOL_DOWN);
    }

    @Test
    void locatorDurabilities() {
        assertEquals(16, ModItems.CRYSTAL_LOCATOR.components()
                .getOrDefault(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 0));
        assertEquals(16, ModItems.TUNED_CRYSTAL_LOCATOR.components()
                .getOrDefault(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 0));
        assertEquals(32, ModItems.ECHO_LOCATOR.components()
                .getOrDefault(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 0));
    }

    @Test
    void echoLocatorIsAlwaysTuned() {
        assertTrue(ModItems.ECHO_LOCATOR instanceof EchoLocatorItem);
        assertTrue(((EchoLocatorItem) ModItems.ECHO_LOCATOR).isTuned(ItemStack.EMPTY, null),
                "the echo locator needs no tuning component");
    }

    @Test
    void untunedLocatorIsNotTuned() {
        assertFalse(ModItems.CRYSTAL_LOCATOR instanceof TunedCrystalLocatorItem);
        assertTrue(ModItems.TUNED_CRYSTAL_LOCATOR instanceof TunedCrystalLocatorItem);
        assertFalse(((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR)
                        .isTuned(new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR), null),
                "no registries -> cannot resolve a tuning -> not tuned");
    }

    @Test
    void tuningComponentIsRegisteredUnderGeodesNamespace() {
        ResourceLocation id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(TunedLocatorData.TUNING_COMPONENT);
        assertEquals(new ResourceLocation("geodes", "tuning"), id,
                "the tuning data component must be registered as geodes:tuning");
    }

    @Test
    void makeTunedLocatorStackCarriesComponent() {
        ItemStack stack = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        assertEquals(new ResourceLocation("geodes", "amethyst"),
                stack.get(TunedLocatorData.TUNING_COMPONENT));
        assertTrue(stack.getItem() instanceof TunedCrystalLocatorItem);
    }

    @Test
    void locatorsAreRegistered() {
        assertEquals(new ResourceLocation("geodes", "crystal_locator"),
                BuiltInRegistries.ITEM.getKey(ModItems.CRYSTAL_LOCATOR));
        assertEquals(new ResourceLocation("geodes", "tuned_crystal_locator"),
                BuiltInRegistries.ITEM.getKey(ModItems.TUNED_CRYSTAL_LOCATOR));
        assertEquals(new ResourceLocation("geodes", "echo_locator"),
                BuiltInRegistries.ITEM.getKey(ModItems.ECHO_LOCATOR));
    }
}
