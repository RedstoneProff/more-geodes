package com.github.thedeathlycow.moregeodes.world;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.GameEvent;

public class GeodesGameEvents {
    public static final GameEvent CRYSTAL_RESONATE = of("crystal_resonate");

    public static void registerEvents() {
    }

    public static GameEvent of(String name) {
        return of(name, 16);
    }

    public static GameEvent of(String name, int range) {
        ResourceLocation id = GeodesIds.of(MoreGeodes.MODID, name);
        return Registry.register(
                BuiltInRegistries.GAME_EVENT,
                id,
                new GameEvent(range)
        );
    }
}
