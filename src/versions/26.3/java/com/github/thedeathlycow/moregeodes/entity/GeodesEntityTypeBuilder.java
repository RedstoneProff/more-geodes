package com.github.thedeathlycow.moregeodes.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class GeodesEntityTypeBuilder {
    public static <T extends Entity> EntityType<T> build(
            EntityType.Builder<T> builder,
            ResourceKey<EntityType<?>> key
    ) {
        return builder.build(key);
    }

    private GeodesEntityTypeBuilder() {
    }
}
