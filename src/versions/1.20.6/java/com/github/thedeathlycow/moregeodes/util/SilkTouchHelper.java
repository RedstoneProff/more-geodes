package com.github.thedeathlycow.moregeodes.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public final class SilkTouchHelper {
    public static int getLevel(Level world, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack);
    }

    private SilkTouchHelper() {
    }
}
