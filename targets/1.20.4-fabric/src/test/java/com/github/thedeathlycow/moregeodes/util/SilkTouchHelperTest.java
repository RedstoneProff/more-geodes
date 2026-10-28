package com.github.thedeathlycow.moregeodes.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SilkTouchHelperTest {
    @BeforeAll
    static void bootstrap() {
        com.github.thedeathlycow.moregeodes.TestBootstrap.init();
    }

    @Test
    void plainStackHasNoSilkTouch() {
        assertEquals(0, SilkTouchHelper.getLevel(null, new ItemStack(Items.DIAMOND_PICKAXE)));
    }

    @Test
    void emptyStackHasNoSilkTouch() {
        assertEquals(0, SilkTouchHelper.getLevel(null, ItemStack.EMPTY));
    }

    @Test
    void silkTouchEnchantedStackReadsItsLevel() {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        stack.enchant(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH, 1);
        assertEquals(1, SilkTouchHelper.getLevel(null, stack));
    }
}
