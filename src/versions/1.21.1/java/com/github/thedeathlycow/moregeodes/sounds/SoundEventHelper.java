package com.github.thedeathlycow.moregeodes.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class SoundEventHelper {
    public static ResourceLocation location(SoundEvent event) {
        return event.getLocation();
    }

    private SoundEventHelper() {
    }
}
