package com.github.thedeathlycow.moregeodes.fabric;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;

public final class GeodesFabricHooks {
    public static CreativeModeTab.Builder creativeTabBuilder() {
        return FabricCreativeModeTab.builder();
    }

    public static void registerComposting() {
    }

    private GeodesFabricHooks() {
    }
}
