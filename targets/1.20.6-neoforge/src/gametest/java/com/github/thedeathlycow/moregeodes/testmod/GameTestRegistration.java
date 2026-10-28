package com.github.thedeathlycow.moregeodes.testmod;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

// the modid must match the containing mod; FML ignores subscribers of other mods
@EventBusSubscriber(modid = "geodes_gametest", bus = EventBusSubscriber.Bus.MOD)
public class GameTestRegistration {
    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        event.register(MoreGeodesGameTests.class);
        event.register(BuddingGrowthTests.class);
        event.register(QuartzOscillatorTests.class);
        event.register(CrystalLootTests.class);
        event.register(LocatorTests.class);
        event.register(PiglinFoolsGoldTests.class);
        event.register(EchoDisplayTests.class);
        event.register(TuningRecipeTests.class);
        event.register(CrystalSoundTests.class);
        GlobalTestReporter.replaceWith(new GametestXmlReporter(
                MoreGeodesGameTests.class,
                BuddingGrowthTests.class,
                QuartzOscillatorTests.class,
                CrystalLootTests.class,
                LocatorTests.class,
                PiglinFoolsGoldTests.class,
                EchoDisplayTests.class,
                TuningRecipeTests.class,
                CrystalSoundTests.class
        ));
    }
}
