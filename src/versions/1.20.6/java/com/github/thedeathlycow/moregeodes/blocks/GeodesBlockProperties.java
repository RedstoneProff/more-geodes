package com.github.thedeathlycow.moregeodes.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class GeodesBlockProperties {
    public static Properties of(String name) {
        return Properties.of();
    }

    public static Properties ofFullCopy(String name, Block block) {
        return Properties.ofFullCopy(block);
    }

    private GeodesBlockProperties() {
    }
}
