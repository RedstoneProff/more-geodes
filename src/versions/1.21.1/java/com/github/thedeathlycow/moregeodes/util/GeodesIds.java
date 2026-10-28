package com.github.thedeathlycow.moregeodes.util;

import net.minecraft.resources.ResourceLocation;

public final class GeodesIds {
    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation parse(String id) {
        return ResourceLocation.parse(id);
    }

    private GeodesIds() {
    }
}
