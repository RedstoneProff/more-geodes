package com.github.thedeathlycow.moregeodes.client;

import com.github.thedeathlycow.moregeodes.item.TunedCrystalLocatorItem;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TuningTintSource implements ItemTintSource {
    public static final TuningTintSource INSTANCE = new TuningTintSource();

    public static final MapCodec<TuningTintSource> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (level == null) {
            return Tuning.UNTUNED.getColor();
        }
        Tuning tuning = TunedCrystalLocatorItem.getTuning(level.registryAccess(), stack);
        return tuning != null
                ? tuning.getColor()
                : Tuning.UNTUNED.getColor();
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
