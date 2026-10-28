package com.github.thedeathlycow.moregeodes.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class TunedLocatorData {
    public static final String TUNING_NBT_KEY = "tuning";

    public static final String TUNING_ID_NBT_KEY = "id";

    public static void register() {
    }

    public static ItemStack makeTunedLocatorStack(String tuningId) {
        var stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);

        var nbt = new CompoundTag();
        nbt.putString(TUNING_ID_NBT_KEY, tuningId);
        stack.getOrCreateTag().put(TUNING_NBT_KEY, nbt);

        return stack;
    }

    @Nullable
    public static ResourceLocation getTuningId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TUNING_NBT_KEY, CompoundTag.TAG_COMPOUND)) {
            return null;
        }
        String id = tag.getCompound(TUNING_NBT_KEY).getString(TUNING_ID_NBT_KEY);
        if (id.isEmpty()) {
            return null;
        }
        return new ResourceLocation(id);
    }

    private TunedLocatorData() {
    }
}
