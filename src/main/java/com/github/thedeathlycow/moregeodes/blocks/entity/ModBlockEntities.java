package com.github.thedeathlycow.moregeodes.blocks.entity;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static void registerBlockEntities() {
        // no block entities anymore!
        // I am keeping the code so that I can use it in the future if needed
    }

    private static void register(String name, BlockEntityType<?> blockEntityType) {
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, GeodesIds.of(MoreGeodes.MODID, name), blockEntityType);
    }
}
