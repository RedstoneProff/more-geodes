package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.NoSuchElementException;

public class TunedCrystalLocatorItem extends CrystalLocatorItem {
    public TunedCrystalLocatorItem(Properties settings, int range, int coolDown) {
        super(settings, range, coolDown);
    }

    @Override
    public boolean isTuned(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        return registries != null && getTuning(registries, stack) != null;
    }

    @Override
    protected boolean isPingableCrystal(ItemStack locatorStack, ServerLevel world, BlockPos pos) {
        Tuning tuning = getTuning(world.registryAccess(), locatorStack);
        if (tuning == null) {
            return super.isPingableCrystal(locatorStack, world, pos);
        }
        return tuning.test(world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        Tuning tuning = getTuning(context.registries(), stack);

        if (tuning != null) {
            tooltip.add(tuning.getDescription());
        }

        super.appendHoverText(stack, context, tooltip, type);
    }

    @Nullable
    public static Tuning getTuning(HolderLookup.Provider registries, ItemStack stack) {
        ResourceLocation tuningId = stack.get(TunedLocatorData.TUNING_COMPONENT);
        if (tuningId == null) {
            return null;
        }

        try {
            return registries.lookupOrThrow(Tunings.REGISTRY_KEY)
                    .get(ResourceKey.create(Tunings.REGISTRY_KEY, tuningId))
                    .map(Holder::value)
                    .orElse(null);
        } catch (NoSuchElementException e) {
            // the tunings registry is not available before the dynamic registry sync
            return null;
        }
    }

    @Nullable
    public static Tuning getTuning(Registry<Tuning> tuningRegistry, ItemStack stack) {
        ResourceLocation tuningId = stack.get(TunedLocatorData.TUNING_COMPONENT);
        if (tuningId == null) {
            return null;
        }

        return tuningRegistry.get(tuningId);
    }
}
