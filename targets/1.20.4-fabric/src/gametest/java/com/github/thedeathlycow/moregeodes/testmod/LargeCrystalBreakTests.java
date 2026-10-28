package com.github.thedeathlycow.moregeodes.testmod;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

public class LargeCrystalBreakTests implements FabricGameTest {

    @GameTest(template = "geodes-test:large_crystal_break_tests.top.piston")
    public void large_crystal_breaks_base_when_broke_by_piston(GameTestHelper context) {
        context.pressButton(1, 2, 0);
        context.succeedWhenBlockPresent(Blocks.AIR, 0, 2, 1);
    }
}
