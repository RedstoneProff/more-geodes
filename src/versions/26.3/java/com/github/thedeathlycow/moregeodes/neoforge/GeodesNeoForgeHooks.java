package com.github.thedeathlycow.moregeodes.neoforge;

import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;

public final class GeodesNeoForgeHooks {
    public static void registerComposting() {
    }

    public static void registerDatapackRegistries(IEventBus modEventBus) {
        modEventBus.addListener((NewDatapackRegistryEvent event) -> {
            event.worldRegistry(Tunings.REGISTRY_KEY, Tuning.DATAPACK_CODEC, Tuning.NETWORK_CODEC);
        });
    }

    private GeodesNeoForgeHooks() {
    }
}
