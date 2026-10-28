package com.github.thedeathlycow.moregeodes.neoforge;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.neoforge.world.GeodesBiomeModifiers;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(MoreGeodes.MODID)
public class MoreGeodesNeoForge {
    public MoreGeodesNeoForge(IEventBus modEventBus) {
        MoreGeodes.setModLoadedChecker(ModList.get()::isLoaded);

        modEventBus.addListener(this::onRegister);
        GeodesNeoForgeHooks.registerDatapackRegistries(modEventBus);
        GeodesBiomeModifiers.register(modEventBus);
        GeodesCreativeTabs.register(modEventBus);
    }

    private void onRegister(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.BLOCK) {
            MoreGeodes.init(FMLPaths.CONFIGDIR.get());

            // not in the constructor: touching ModItems earlier class-loads the blocks while the registries are frozen
            GeodesNeoForgeHooks.registerComposting();
        }
    }
}
