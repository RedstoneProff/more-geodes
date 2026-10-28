package com.github.thedeathlycow.moregeodes.sounds;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public final class SoundEventHelper {
    public static Identifier location(SoundEvent event) {
        return event.location();
    }

    private SoundEventHelper() {
    }
}
