package com.github.thedeathlycow.moregeodes.neoforge;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.item.GeodesItemGroup;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

final class GeodesCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoreGeodes.MODID);

    static void register(IEventBus modEventBus) {
        TABS.register("main", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.EMERALD_CLUSTER))
                .title(Component.translatable("itemGroup.geodes.more_geodes"))
                .displayItems((parameters, output) -> GeodesItemGroup.addTabEntries(output))
                .build()
        );
        TABS.register(modEventBus);
    }

    private GeodesCreativeTabs() {
    }
}
