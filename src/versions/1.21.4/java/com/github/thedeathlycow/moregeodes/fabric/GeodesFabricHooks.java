package com.github.thedeathlycow.moregeodes.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;

public final class GeodesFabricHooks {
    public static CreativeModeTab.Builder creativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    public static void registerComposting() {
        CompostingChanceRegistry.INSTANCE.add(ModItems.GYPSUM_SHARD, 1.0f);
    }

    private GeodesFabricHooks() {
    }
}
