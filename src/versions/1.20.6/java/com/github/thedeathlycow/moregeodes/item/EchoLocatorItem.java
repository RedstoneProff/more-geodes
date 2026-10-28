package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EchoLocatorItem extends CrystalLocatorItem {

    public static final int BLOCK_RANGE = 6;

    public static final int ECHO_LOCATOR_COOL_DOWN = CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN * 4;

    public EchoLocatorItem(Properties settings, int range, int coolDown) {
        super(settings, range, coolDown);
    }

    @Override
    public boolean isTuned(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        return true;
    }

    @Override
    protected boolean isPingableCrystal(ItemStack locatorStack, ServerLevel world, BlockPos pos) {
        return world.getBlockState(pos).is(ModBlockTags.ECHO_LOCATABLE);
    }

    @Override
    protected void setCooldown(ItemCooldowns cooldownManager, int ticks) {
        cooldownManager.addCooldown(this, ticks);
    }
}
