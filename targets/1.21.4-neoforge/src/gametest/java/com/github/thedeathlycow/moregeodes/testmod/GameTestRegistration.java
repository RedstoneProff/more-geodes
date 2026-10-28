package com.github.thedeathlycow.moregeodes.testmod;

import java.util.List;

import net.minecraft.gametest.framework.GlobalTestReporter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

// the modid must match the containing mod; FML ignores subscribers of other mods
@EventBusSubscriber(modid = "geodes_gametest")
public class GameTestRegistration {
    private static final List<Class<?>> TEST_CLASSES = List.of(
            MoreGeodesGameTests.class,
            BuddingGrowthTests.class,
            ClusterHarvestTests.class,
            QuartzOscillatorTests.class,
            LocatorTests.class,
            PiglinFoolsGoldTests.class
    );

    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        TEST_CLASSES.forEach(event::register);
        GlobalTestReporter.replaceWith(new GametestXmlReporter(TEST_CLASSES));
    }
}
