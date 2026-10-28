package com.github.thedeathlycow.moregeodes.testmod;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class MoreGeodesGameTests {
    @GameTest(template = "large_crystal_break_tests.top.piston", templateNamespace = "geodes-test")
    public void large_crystal_breaks_base_when_broke_by_piston(GameTestHelper context) {
        context.pressButton(1, 2, 0);
        context.succeedWhenBlockPresent(Blocks.AIR, 0, 2, 1);
    }
}
