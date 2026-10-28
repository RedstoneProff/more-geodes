package com.github.thedeathlycow.moregeodes.item.tuning;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;

public class Tunings {

    public static final ResourceKey<Registry<Tuning>> REGISTRY_KEY = ResourceKey.createRegistryKey(
            GeodesIds.of(MoreGeodes.MODID, "tunings")
    );

    private Tunings() {

    }
}
