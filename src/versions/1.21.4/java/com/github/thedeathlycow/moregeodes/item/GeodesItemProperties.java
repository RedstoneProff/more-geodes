package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class GeodesItemProperties {
    public static Item.Properties of(String name) {
        return new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, GeodesIds.of(MoreGeodes.MODID, name))
        );
    }

    private GeodesItemProperties() {
    }
}
