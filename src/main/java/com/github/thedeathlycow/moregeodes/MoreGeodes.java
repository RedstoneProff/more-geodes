package com.github.thedeathlycow.moregeodes;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.blocks.entity.ModBlockEntities;
import com.github.thedeathlycow.moregeodes.config.GeodesConfig;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.recipe.GeodesRecipeSerializers;
import com.github.thedeathlycow.moregeodes.sounds.GeodesSoundEvents;
import com.github.thedeathlycow.moregeodes.world.GeodesGameEvents;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.Predicate;

public class MoreGeodes {
    public static final String MODID = "geodes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final GeodesConfig CONFIG = new GeodesConfig();

    private static Predicate<String> modLoadedChecker = id -> false;

    public static void setModLoadedChecker(Predicate<String> checker) {
        modLoadedChecker = checker;
    }

    public static boolean isModLoaded(String modId) {
        return modLoadedChecker.test(modId);
    }

    public static boolean isCarpetLoaded() {
        return isModLoaded("carpet");
    }

    public static boolean isAE2Loaded() {
        return isModLoaded("ae2");
    }

    public static boolean isSpectrumLoaded() {
        return isModLoaded("spectrum");
    }

    private static volatile boolean movableAmethystEnabled = false;
    private static volatile boolean movableAmethystResolved = false;

    public static boolean isMovableAmethystEnabled() {
        if (!isCarpetLoaded()) {
            return false;
        }
        if (!movableAmethystResolved) {
            try {
                Class<?> carpetSettings = Class.forName("carpet.CarpetSettings");
                Field field = carpetSettings.getField("movableAmethyst");
                movableAmethystEnabled = field.getBoolean(null);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                LOGGER.warn("Could not read Carpet movableAmethyst setting, assuming disabled", e);
                movableAmethystEnabled = false;
            }
            movableAmethystResolved = true;
        }
        return movableAmethystEnabled;
    }

    public static void init(Path configDir) {
        LOGGER.info("Beginning More Geodes initialization");
        CONFIG.loadConfig(configDir);

        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModBlockEntities.registerBlockEntities();
        GeodesEntityTypes.registerAll();
        GeodesSoundEvents.registerSoundEvents();
        GeodesGameEvents.registerEvents();
        GeodesMemoryModules.registerModules();
        GeodesRecipeSerializers.registerAll();

        LOGGER.info("More Geodes initialized!");
    }

    private MoreGeodes() {
    }
}
