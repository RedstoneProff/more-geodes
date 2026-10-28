package com.github.thedeathlycow.moregeodes.fabric;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.features.ModFeatures;
import com.github.thedeathlycow.moregeodes.item.GeodesItemGroup;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MoreGeodesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MoreGeodes.setModLoadedChecker(FabricLoader.getInstance()::isModLoaded);
        MoreGeodes.init(FabricLoader.getInstance().getConfigDir());

        ModFeatures.placeFeaturesInBiomes();

        DynamicRegistries.registerSynced(
                Tunings.REGISTRY_KEY,
                Tuning.DATAPACK_CODEC,
                Tuning.NETWORK_CODEC
        );

        GeodesFabricHooks.registerComposting();

        this.registerItemGroup();
    }

    private void registerItemGroup() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                GeodesItemGroup.MORE_GEODES,
                GeodesFabricHooks.creativeTabBuilder()
                        .icon(() -> new ItemStack(ModItems.EMERALD_CLUSTER))
                        .title(Component.translatable("itemGroup.geodes.more_geodes"))
                        .displayItems((context, entries) -> GeodesItemGroup.addTabEntries(entries))
                        .build()
        );
    }
}
