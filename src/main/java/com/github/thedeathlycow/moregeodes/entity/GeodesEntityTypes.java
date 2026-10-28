package com.github.thedeathlycow.moregeodes.entity;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class GeodesEntityTypes {

    public static final ResourceKey<EntityType<?>> ECHO_DISPLAY_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            GeodesIds.of(MoreGeodes.MODID, "echo_display")
    );

    public static final EntityType<EchoDisplay> ECHO_DISPLAY = GeodesEntityTypeBuilder.build(
            EntityType.Builder.of(
                    EchoDisplay::new,
                    MobCategory.CREATURE
            )
                    .sized(0.0f, 0.0f),
            ECHO_DISPLAY_KEY
    );

    public static void registerAll() {
        register("echo_display", ECHO_DISPLAY);
    }

    private static <T extends Entity> void register(String id, EntityType<T> type) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, GeodesIds.of(MoreGeodes.MODID, id), type);
    }

    private GeodesEntityTypes() {

    }
}
