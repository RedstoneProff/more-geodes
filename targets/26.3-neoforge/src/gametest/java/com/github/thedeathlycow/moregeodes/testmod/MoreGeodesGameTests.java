package com.github.thedeathlycow.moregeodes.testmod;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

public class MoreGeodesGameTests {
    public static void large_crystal_breaks_base_when_broke_by_piston(GameTestHelper context) {
        context.pressButton(1, 1, 0);
        context.succeedWhenBlockPresent(Blocks.AIR, 0, 1, 1);
    }
}
