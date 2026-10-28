package com.github.thedeathlycow.moregeodes.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrystalClusterBlockStateTest {
    @BeforeAll
    static void bootstrap() {
        com.github.thedeathlycow.moregeodes.TestBootstrap.registerModContent(
                java.nio.file.Path.of("build", "test-config"));
    }

    private static BlockState cluster() {
        return ModBlocks.EMERALD_CLUSTER.defaultBlockState();
    }

    @Test
    void defaultStateFacesUpAndIsNotWaterlogged() {
        BlockState state = cluster();
        assertEquals(Direction.UP, state.getValue(CrystalClusterBlock.FACING));
        assertFalse(state.getValue(CrystalClusterBlock.WATERLOGGED));
    }

    @Test
    void shapeDependsOnFacing() {
        BlockState up = cluster();
        BlockState down = cluster().setValue(CrystalClusterBlock.FACING, Direction.DOWN);
        assertFalse(up.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty())
                .equals(down.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty())),
                "up-facing and down-facing clusters must have different shapes");
    }

    @Test
    void rotateTurnsTheFacing() {
        assertEquals(cluster(), cluster().rotate(Rotation.CLOCKWISE_90));
        BlockState rotated = cluster().setValue(CrystalClusterBlock.FACING, Direction.NORTH)
                .rotate(Rotation.CLOCKWISE_90);
        assertEquals(Direction.EAST, rotated.getValue(CrystalClusterBlock.FACING));
        BlockState fullTurn = cluster().setValue(CrystalClusterBlock.FACING, Direction.NORTH)
                .rotate(Rotation.CLOCKWISE_90)
                .rotate(Rotation.CLOCKWISE_90)
                .rotate(Rotation.CLOCKWISE_90)
                .rotate(Rotation.CLOCKWISE_90);
        assertEquals(cluster().setValue(CrystalClusterBlock.FACING, Direction.NORTH), fullTurn);
    }

    @Test
    void mirrorTurnsTheFacing() {
        BlockState mirrored = cluster().setValue(CrystalClusterBlock.FACING, Direction.NORTH)
                .mirror(Mirror.FRONT_BACK);
        assertEquals(Direction.NORTH, mirrored.getValue(CrystalClusterBlock.FACING));
        BlockState leftRight = cluster().setValue(CrystalClusterBlock.FACING, Direction.NORTH)
                .mirror(Mirror.LEFT_RIGHT);
        assertEquals(Direction.SOUTH, leftRight.getValue(CrystalClusterBlock.FACING));
        assertEquals(cluster(), cluster().mirror(Mirror.LEFT_RIGHT));
        assertEquals(cluster(), cluster().mirror(Mirror.FRONT_BACK));
    }

    @Test
    void waterloggedStateGivesAWaterFluidState() {
        BlockState waterlogged = cluster().setValue(CrystalClusterBlock.WATERLOGGED, true);
        FluidState fluid = waterlogged.getFluidState();
        assertTrue(fluid.is(Fluids.WATER), "a waterlogged cluster must hold water");
        assertFalse(cluster().getFluidState().is(Fluids.WATER),
                "a dry cluster must not hold water");
    }
}
