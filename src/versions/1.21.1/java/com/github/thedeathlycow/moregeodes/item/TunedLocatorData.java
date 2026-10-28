package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class TunedLocatorData {
    public static final DataComponentType<ResourceLocation> TUNING_COMPONENT = DataComponentType.<ResourceLocation>builder()
            .persistent(ResourceLocation.CODEC)
            .networkSynchronized(ResourceLocation.STREAM_CODEC)
            .build();

    public static void register() {
        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                GeodesIds.of(MoreGeodes.MODID, "tuning"),
                TUNING_COMPONENT
        );
    }

    public static ItemStack makeTunedLocatorStack(String tuningId) {
        var stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        stack.set(TUNING_COMPONENT, GeodesIds.parse(tuningId));
        return stack;
    }

    private TunedLocatorData() {
    }
}
