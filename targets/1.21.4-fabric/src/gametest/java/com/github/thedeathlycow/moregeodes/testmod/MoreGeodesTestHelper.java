package com.github.thedeathlycow.moregeodes.testmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class MoreGeodesTestHelper {
    public static final BlockPos CENTER = new BlockPos(7, 2, 7);

    public static final Direction[] DIRECTIONS = Direction.values();

    private MoreGeodesTestHelper() {
    }

    public static boolean anyNeighborIs(GameTestHelper context, BlockPos pos, Block... blocks) {
        for (Direction direction : DIRECTIONS) {
            BlockState state = context.getBlockState(pos.relative(direction));
            for (Block block : blocks) {
                if (state.is(block)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void assertAnyNeighborIs(GameTestHelper context, BlockPos pos, String message, Block... blocks) {
        context.assertTrue(anyNeighborIs(context, pos, blocks), message);
    }

    public static void assertNoNeighborIs(GameTestHelper context, BlockPos pos, String message, Block... blocks) {
        context.assertFalse(anyNeighborIs(context, pos, blocks), message);
    }

    public static void randomTickTimes(GameTestHelper context, BlockPos pos, int ticks) {
        for (int i = 0; i < ticks; i++) {
            context.randomTick(pos);
        }
    }

    public static void assertAllNeighborsAreAir(GameTestHelper context, BlockPos pos, String message, Direction... exclude) {
        List<Direction> excluded = List.of(exclude);
        for (Direction direction : DIRECTIONS) {
            if (excluded.contains(direction)) {
                continue;
            }
            context.assertTrue(
                    context.getBlockState(pos.relative(direction)).isAir(),
                    message + " (direction " + direction + ", found "
                            + context.getBlockState(pos.relative(direction)).getBlock() + ")"
            );
        }
    }
}
