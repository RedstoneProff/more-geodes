package com.github.thedeathlycow.moregeodes;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.server.Bootstrap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

public final class TestBootstrap {
    private static boolean initted;

    private TestBootstrap() {
    }

    public static synchronized void init() {
        if (initted) {
            return;
        }
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreezeBuiltInRegistries();
        initted = true;
    }

    private static boolean modContentRegistered;

    public static synchronized void registerModContent(java.nio.file.Path configDir) {
        init();
        if (modContentRegistered) {
            return;
        }
        try {
            java.nio.file.Files.createDirectories(configDir);
            // getConfigFile() asserts the file exists before the default-config fallback can run
            java.nio.file.Files.writeString(configDir.resolve("geodes.properties"), "");
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
        MoreGeodes.init(configDir);
        modContentRegistered = true;
    }

    private static void unfreezeBuiltInRegistries() {
        try {
            Field frozen = MappedRegistry.class.getDeclaredField("frozen");
            frozen.setAccessible(true);
            Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            intrusive.setAccessible(true);

            for (Object registry : new Object[]{
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK,
                    net.minecraft.core.registries.BuiltInRegistries.ITEM,
                    net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE,
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT,
                    net.minecraft.core.registries.BuiltInRegistries.FLUID}) {
                frozen.setBoolean(registry, false);
                if (intrusive.get(registry) == null) {
                    intrusive.set(registry, new HashMap<>());
                }
            }
            Iterator<Object> registries = (Iterator<Object>) (Iterator<?>) net.minecraft.core.registries.BuiltInRegistries.REGISTRY.iterator();
            while (registries.hasNext()) {
                Object registry = registries.next();
                if (registry instanceof MappedRegistry<?> mapped) {
                    frozen.setBoolean(mapped, false);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not unfreeze built-in registries for unit tests", e);
        }
    }
}
