package com.github.thedeathlycow.moregeodes.neoforge;

import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class GeodesNeoForgeHooks {
    public static void registerComposting() {
        ComposterBlock.COMPOSTABLES.put(ModItems.GYPSUM_SHARD, 1.0F);
    }

    public static void registerDatapackRegistries(IEventBus modEventBus) {
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            event.dataPackRegistry(Tunings.REGISTRY_KEY, Tuning.DATAPACK_CODEC, Tuning.NETWORK_CODEC);
        });
    }

    private GeodesNeoForgeHooks() {
    }
}
