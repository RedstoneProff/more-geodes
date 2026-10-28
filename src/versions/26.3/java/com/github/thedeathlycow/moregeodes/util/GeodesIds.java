package com.github.thedeathlycow.moregeodes.util;

import net.minecraft.resources.Identifier;

public final class GeodesIds {
    public static Identifier of(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier parse(String id) {
        return Identifier.parse(id);
    }

    private GeodesIds() {
    }
}
