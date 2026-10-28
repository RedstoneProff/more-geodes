package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public final class GeodesItemProperties {
    public static Item.Properties of(String name) {
        Item.Properties properties = new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, GeodesIds.of(MoreGeodes.MODID, name))
        );
        if ("gypsum_shard".equals(name)) {
            properties = properties.component(
                    DataComponents.COMPOSTABLE,
                    new Compostable(new ResolvableInt.Constant(1))
            );
        }
        return properties;
    }

    private GeodesItemProperties() {
    }
}
