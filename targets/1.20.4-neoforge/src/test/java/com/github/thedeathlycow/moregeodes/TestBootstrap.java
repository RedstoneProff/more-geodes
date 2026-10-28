package com.github.thedeathlycow.moregeodes;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TestBootstrap {
    private static final AtomicBoolean DONE = new AtomicBoolean(false);

    private TestBootstrap() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerIntoRoot(net.minecraft.core.Registry root, ResourceKey key, Object registry) {
        net.minecraft.core.Registry.register(root, key, registry);
    }

    public static void ensureBootstrapped() throws Exception {
        if (!DONE.compareAndSet(false, true)) {
            return;
        }
        SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        for (var registry : BuiltInRegistries.REGISTRY) {
            if (registry instanceof MappedRegistry<?> mapped) {
                frozenField.setBoolean(mapped, false);
            }
        }
        frozenField.setBoolean((MappedRegistry<?>) BuiltInRegistries.REGISTRY, false);
        registerIntoRoot(BuiltInRegistries.REGISTRY,
                net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.FLUID_TYPES,
                net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES);
        var fluidTypes = net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES;
        for (String name : new String[]{"minecraft:empty", "minecraft:water", "minecraft:lava"}) {
            var id = new net.minecraft.resources.ResourceLocation(name);
            if (!fluidTypes.containsKey(id)) {
                registerIntoRoot(fluidTypes,
                        net.minecraft.resources.ResourceKey.create(fluidTypes.key(), id),
                        new net.neoforged.neoforge.fluids.FluidType(
                                net.neoforged.neoforge.fluids.FluidType.Properties.create()));
            }
        }
        Path configDir = Files.createTempDirectory("geodes-test-config");
        Files.writeString(configDir.resolve("geodes.properties"), "");
        MoreGeodes.init(configDir);
    }
}
