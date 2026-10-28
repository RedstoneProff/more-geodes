package com.github.thedeathlycow.moregeodes.testmod;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

// the modid must match the containing mod; FML ignores subscribers of other mods
@Mod.EventBusSubscriber(modid = "geodes_gametest", bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameTestRegistration {
    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        event.register(MoreGeodesGameTests.class);
        GlobalTestReporter.replaceWith(new GametestXmlReporter(MoreGeodesGameTests.class));
    }
}
