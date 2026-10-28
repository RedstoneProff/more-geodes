package com.github.thedeathlycow.moregeodes.blocks;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class GeodesBlockProperties {
    public static Properties of(String name) {
        return Properties.of().setId(blockKey(name));
    }

    public static Properties ofFullCopy(String name, Block block) {
        return Properties.ofFullCopy(block).setId(blockKey(name));
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, GeodesIds.of(MoreGeodes.MODID, name));
    }

    private GeodesBlockProperties() {
    }
}
