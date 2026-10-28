package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TunedCrystalLocatorItem extends CrystalLocatorItem {

    public TunedCrystalLocatorItem(Properties settings, int range, int coolDown) {
        super(settings, range, coolDown);
    }

    @Override
    public boolean isTuned(ItemStack stack, @Nullable RegistryAccess registries) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag type) {
        Tuning tuning = null;
        if (world != null) {
            tuning = getTuning(world.registryAccess(), stack);
        }

        if (tuning != null) {
            tooltip.add(tuning.getDescription());
        }

        super.appendHoverText(stack, world, tooltip, type);
    }

    @Nullable
    public static Tuning getTuning(RegistryAccess registries, ItemStack stack) {
        ResourceLocation tuningId = TunedLocatorData.getTuningId(stack);
        if (tuningId == null) {
            return null;
        }

        Registry<Tuning> tuningRegistry = registries.registryOrThrow(Tunings.REGISTRY_KEY);
        return tuningRegistry.get(tuningId);
    }

    @Nullable
    public static Tuning getTuning(Registry<Tuning> tuningRegistry, ItemStack stack) {
        ResourceLocation tuningId = TunedLocatorData.getTuningId(stack);
        if (tuningId == null) {
            return null;
        }

        return tuningRegistry.get(tuningId);
    }
}
