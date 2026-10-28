package com.github.thedeathlycow.moregeodes;

import net.minecraft.SharedConstants;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class TestBootstrap {
    private static boolean bootstrapped;

    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        List<Registry<?>> registries = List.of(
                BuiltInRegistries.BLOCK,
                BuiltInRegistries.ITEM,
                BuiltInRegistries.SOUND_EVENT,
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                BuiltInRegistries.ENTITY_TYPE,
                BuiltInRegistries.GAME_EVENT,
                BuiltInRegistries.MEMORY_MODULE_TYPE,
                BuiltInRegistries.RECIPE_SERIALIZER,
                BuiltInRegistries.DATA_COMPONENT_TYPE
        );
        for (Registry<?> registry : registries) {
            unfreeze(registry);
        }

        MoreGeodes.init(Path.of("build", "test-config"));

        bindItemComponents();

        for (Registry<?> registry : registries) {
            try {
                java.lang.reflect.Method unbound = Class.forName("net.minecraft.core.MappedRegistry$TagSet")
                        .getDeclaredMethod("unbound");
                unbound.setAccessible(true);
                Object tagSet = unbound.invoke(null);
                setField(registry, "allTags", tagSet);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("could not reset tags on " + registry.key(), e);
            }
            registry.freeze();
        }
        bootstrapped = true;
    }

    private static void bindItemComponents() {
        HolderLookup.Provider base = RegistryAccess.fromRegistryOfRegistries(
                (Registry<Registry<?>>) BuiltInRegistries.REGISTRY
        );
        HolderLookup.Provider provider = new HolderLookup.Provider() {
            @Override
            public <T> java.util.Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
                if (key == (ResourceKey<? extends Registry<? extends T>>) (ResourceKey<?>) net.minecraft.core.registries.Registries.DAMAGE_TYPE) {
                    return java.util.Optional.of(wrapDamageTypes(new HolderLookup.RegistryLookup<>() {
                        @Override
                        public ResourceKey<? extends Registry<? extends T>> key() {
                            return (ResourceKey<? extends Registry<? extends T>>) (ResourceKey<?>) net.minecraft.core.registries.Registries.DAMAGE_TYPE;
                        }

                        @Override
                        public com.mojang.serialization.Lifecycle registryLifecycle() {
                            return com.mojang.serialization.Lifecycle.stable();
                        }

                        @Override
                        public java.util.Optional<net.minecraft.core.Holder.Reference<T>> get(ResourceKey<T> elementKey) {
                            return java.util.Optional.empty();
                        }

                        @Override
                        public java.util.Optional<net.minecraft.core.HolderSet.Named<T>> get(net.minecraft.tags.TagKey<T> tag) {
                            return java.util.Optional.empty();
                        }

                        @Override
                        public java.util.stream.Stream<net.minecraft.core.Holder.Reference<T>> listElements() {
                            return java.util.stream.Stream.empty();
                        }

                        @Override
                        public java.util.stream.Stream<net.minecraft.core.HolderSet.Named<T>> listTags() {
                            return java.util.stream.Stream.empty();
                        }
                    }));
                }
                return base.lookup(key).map(k -> (HolderLookup.RegistryLookup<T>) k);
            }

            private <T> HolderLookup.RegistryLookup<T> wrapDamageTypes(HolderLookup.RegistryLookup<T> real) {
                    return new HolderLookup.RegistryLookup<>() {
                        @Override
                        public ResourceKey<? extends Registry<? extends T>> key() {
                            return real.key();
                        }

                        @Override
                        public com.mojang.serialization.Lifecycle registryLifecycle() {
                            return real.registryLifecycle();
                        }

                        @Override
                        public java.util.Optional<net.minecraft.core.Holder.Reference<T>> get(ResourceKey<T> elementKey) {
                            return real.get(elementKey);
                        }

                        @Override
                        public java.util.Optional<net.minecraft.core.HolderSet.Named<T>> get(net.minecraft.tags.TagKey<T> tag) {
                            return java.util.Optional.of(net.minecraft.core.HolderSet.emptyNamed(real, tag));
                        }

                        @Override
                        public java.util.stream.Stream<net.minecraft.core.Holder.Reference<T>> listElements() {
                            return real.listElements();
                        }

                        @Override
                        public java.util.stream.Stream<net.minecraft.core.HolderSet.Named<T>> listTags() {
                            return real.listTags();
                        }
                    };
            }

            @Override
            public java.util.stream.Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
                return base.listRegistryKeys();
            }
        };
        try {
            Field initializersField = BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.getClass()
                    .getDeclaredField("initializers");
            initializersField.setAccessible(true);
            List<?> entries = (List<?>) initializersField.get(BuiltInRegistries.DATA_COMPONENT_INITIALIZERS);
            for (Object entry : entries) {
                java.lang.reflect.Method keyGetter = entry.getClass().getDeclaredMethod("key");
                keyGetter.setAccessible(true);
                ResourceKey<?> key = (ResourceKey<?>) keyGetter.invoke(entry);
                if (!net.minecraft.core.registries.Registries.ITEM.identifier().equals(key.registry())) {
                    continue;
                }
                var builder = net.minecraft.core.component.DataComponentMap.builder();
                try {
                    java.lang.reflect.Method run = entry.getClass().getDeclaredMethod("run", builder.getClass(), HolderLookup.Provider.class);
                    run.setAccessible(true);
                    run.invoke(entry, builder, provider);
                    BuiltInRegistries.ITEM.get((ResourceKey<net.minecraft.world.item.Item>) key).orElseThrow()
                            .bindComponents(builder.build());
                } catch (Exception | AssertionError e) {
                    BuiltInRegistries.ITEM.get((ResourceKey<net.minecraft.world.item.Item>) key).orElseThrow()
                            .bindComponents(net.minecraft.core.component.DataComponentMap.EMPTY);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not bind mod item components", e);
        }
        for (var holder : BuiltInRegistries.ITEM.asHolderIdMap()) {
            if (holder instanceof net.minecraft.core.Holder.Reference<net.minecraft.world.item.Item> ref
                    && ref.key().identifier().getNamespace().equals("geodes")
                    && !ref.areComponentsBound()) {
                ref.bindComponents(net.minecraft.core.component.DataComponentMap.EMPTY);
            }
        }
    }

    private static void unfreeze(Registry<?> registry) {
        if (!(registry instanceof MappedRegistry<?> mapped)) {
            throw new IllegalStateException("unexpected registry implementation: " + registry.getClass());
        }
        setField(mapped, "frozen", false);
        if (hasIntrusiveHolders(registry)) {
            setField(mapped, "unregisteredIntrusiveHolders", new IdentityHashMap<>());
        }
    }

    private static boolean hasIntrusiveHolders(Registry<?> registry) {
        return registry == BuiltInRegistries.BLOCK
                || registry == BuiltInRegistries.ITEM
                || registry == BuiltInRegistries.ENTITY_TYPE
                || registry == BuiltInRegistries.BLOCK_ENTITY_TYPE;
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field field = findField(target.getClass(), name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not set field " + name + " on " + target.getClass(), e);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new NoSuchFieldException(name);
    }

    private TestBootstrap() {
    }
}
