package com.github.thedeathlycow.moregeodes.blocks;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BlockLevelInteractionTest {
    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
    }

    private static Block block(String name) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(MoreGeodes.MODID, name));
    }

    private static ServerLevel serverLevel() {
        return mock(ServerLevel.class);
    }

    @Test
    void buddingEmeraldGrowsASmallBudIntoAirViaSetBlock() {
        ServerLevel level = serverLevel();
        BlockPos pos = BlockPos.ZERO;
        when(level.getBlockState(any())).thenReturn(Blocks.AIR.defaultBlockState());

        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        budding.growCrystalOnce(level, pos, Direction.UP);

        BlockState expected = block("small_emerald_bud").defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(CrystalClusterBlock.WATERLOGGED, false);
        verify(level).setBlock(eq(pos.above()), eq(expected), anyInt());
    }

    @Test
    void buddingEmeraldDoesNotGrowIntoOccupiedSpace() {
        ServerLevel level = serverLevel();
        BlockPos pos = BlockPos.ZERO;
        when(level.getBlockState(any())).thenReturn(Blocks.STONE.defaultBlockState());

        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        budding.growCrystalOnce(level, pos, Direction.UP);

        verify(level, never()).setBlock(any(), any(), anyInt());
    }

    @Test
    void buddingEmeraldGrowsWaterloggedBudIntoWater() {
        ServerLevel level = serverLevel();
        BlockPos pos = BlockPos.ZERO;
        when(level.getBlockState(any())).thenReturn(Blocks.WATER.defaultBlockState());

        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        budding.growCrystalOnce(level, pos, Direction.UP);

        BlockState expected = block("small_emerald_bud").defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(CrystalClusterBlock.WATERLOGGED, true);
        verify(level).setBlock(eq(pos.above()), eq(expected), anyInt());
    }

    @Test
    void buddingGypsumGrowsARoseWithBothHalves() {
        ServerLevel level = serverLevel();
        BlockPos pos = BlockPos.ZERO;
        BlockState largeBud = block("large_gypsum_bud").defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        when(level.getBlockState(eq(pos.above()))).thenReturn(largeBud);
        when(level.getBlockState(eq(pos.above().above()))).thenReturn(Blocks.AIR.defaultBlockState());
        when(level.getBlockState(eq(pos))).thenReturn(Blocks.STONE.defaultBlockState());

        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_gypsum");
        budding.growCrystalOnce(level, pos, Direction.UP);

        BlockState lower = block("gypsum_rose").defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(CrystalClusterBlock.WATERLOGGED, false)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState upper = block("gypsum_rose").defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(CrystalClusterBlock.WATERLOGGED, false)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.UPPER);
        verify(level).setBlock(eq(pos.above()), eq(lower), anyInt());
        verify(level).setBlock(eq(pos.above().above()), eq(upper), anyInt());
    }

    @Test
    void buddingGypsumDoesNotGrowARoseWhenHeadSpaceIsOccupied() {
        ServerLevel level = serverLevel();
        BlockPos pos = BlockPos.ZERO;
        BlockPos headPos = pos.above();
        BlockState largeBud = block("large_gypsum_bud").defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP);
        when(level.getBlockState(eq(pos.above()))).thenReturn(largeBud);
        when(level.getBlockState(eq(pos.above().above()))).thenReturn(Blocks.STONE.defaultBlockState());

        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_gypsum");
        budding.growCrystalOnce(level, pos, Direction.UP);

        verify(level, never()).setBlock(any(), any(), anyInt());
    }

    @Test
    void largeCrystalUpperHalfSurvivesOnlyOnItsLowerAnchor() {
        Block rose = block("gypsum_rose");
        LevelReader world = mock(LevelReader.class);
        BlockPos upperPos = BlockPos.ZERO;
        BlockState upper = rose.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.UPPER);

        when(world.getBlockState(upperPos.below())).thenReturn(rose.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.LOWER));
        assertTrue(rose.canSurvive(upper, world, upperPos));

        when(world.getBlockState(upperPos.below())).thenReturn(Blocks.STONE.defaultBlockState());
        assertFalse(rose.canSurvive(upper, world, upperPos));
    }

    @Test
    void largeCrystalLowerHalfBreaksWhenItsHeadIsGone() {
        Block rose = block("gypsum_rose");
        LevelAccessor world = mock(LevelAccessor.class);
        BlockPos lowerPos = BlockPos.ZERO;
        BlockState lower = rose.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.LOWER);

        BlockState updated = rose.updateShape(
                lower, Direction.UP, Blocks.STONE.defaultBlockState(), world, lowerPos, lowerPos.above());
        assertEquals(Blocks.AIR.defaultBlockState(), updated, "a lower half without its head must break");
    }

    @Test
    void largeCrystalLowerHalfStaysWhenItsHeadIsPresent() {
        Block rose = block("gypsum_rose");
        LevelAccessor world = mock(LevelAccessor.class);
        BlockPos lowerPos = BlockPos.ZERO;
        BlockState lower = rose.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState upper = rose.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(LargeCrystalClusterBlock.HALF, DoubleBlockHalf.UPPER);

        BlockState updated = rose.updateShape(lower, Direction.UP, upper, world, lowerPos, lowerPos.above());
        assertEquals(lower, updated, "a lower half with its head must stay");
    }

    @Test
    void placeAtSetsBothHalves() {
        LevelAccessor world = mock(LevelAccessor.class);
        when(world.getBlockState(any())).thenReturn(Blocks.AIR.defaultBlockState());
        BlockPos pos = BlockPos.ZERO;

        LargeCrystalClusterBlock.placeAt(world,
                block("gypsum_rose").defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP),
                pos, Block.UPDATE_ALL);

        verify(world).setBlock(eq(pos), any(BlockState.class), anyInt());
        verify(world).setBlock(eq(pos.above()), any(BlockState.class), anyInt());
    }

    @Test
    void projectileHitPlaysHitAndChimeSounds() {
        Level level = mock(Level.class);
        when(level.isClientSide()).thenReturn(false);
        when(level.getRandom()).thenReturn(net.minecraft.util.RandomSource.create());

        Block cluster = block("emerald_cluster");
        var group = ((CrystalBlock) cluster).getHitSoundGroup();
        var hitResult = new net.minecraft.world.phys.BlockHitResult(
                net.minecraft.world.phys.Vec3.ZERO, Direction.UP, BlockPos.ZERO, false);
        cluster.onProjectileHit(level, cluster.defaultBlockState(), hitResult,
                mock(net.minecraft.world.entity.projectile.Projectile.class));

        verify(level).playSound(any(), eq(BlockPos.ZERO), eq(group.hitSound()),
                eq(net.minecraft.sounds.SoundSource.BLOCKS), eq(1.0f), anyFloat());
        verify(level).playSound(any(), eq(BlockPos.ZERO), eq(group.chimeSound()),
                eq(net.minecraft.sounds.SoundSource.BLOCKS), eq(1.0f), anyFloat());
    }

    @Test
    void projectileHitIsSilentOnTheClient() {
        Level level = mock(Level.class);
        when(level.isClientSide()).thenReturn(true);

        Block cluster = block("emerald_cluster");
        var hitResult = new net.minecraft.world.phys.BlockHitResult(
                net.minecraft.world.phys.Vec3.ZERO, Direction.UP, BlockPos.ZERO, false);
        cluster.onProjectileHit(level, cluster.defaultBlockState(), hitResult,
                mock(net.minecraft.world.entity.projectile.Projectile.class));

        verify(level, never()).playSound(any(), any(BlockPos.class), any(), any(), anyFloat(), anyFloat());
    }

    @Test
    void poweredQuartzClusterFlipsAndReschedulesWhileSignalPresent() {
        ServerLevel level = serverLevel();
        when(level.hasNeighborSignal(BlockPos.ZERO)).thenReturn(true);

        Block cluster = block("quartz_cluster");
        BlockState powered = cluster.defaultBlockState().setValue(QuartzClusterBlock.POWERED, true);
        cluster.tick(powered, level, BlockPos.ZERO, net.minecraft.util.RandomSource.create());

        verify(level).setBlock(eq(BlockPos.ZERO),
                eq(cluster.defaultBlockState().setValue(QuartzClusterBlock.POWERED, false)), anyInt());
        verify(level).scheduleTick(eq(BlockPos.ZERO), eq(cluster), anyInt());
    }

    @Test
    void quartzClusterResetsToUnpoweredWhenSignalIsRemoved() {
        ServerLevel level = serverLevel();
        when(level.hasNeighborSignal(BlockPos.ZERO)).thenReturn(false);

        Block cluster = block("quartz_cluster");
        BlockState powered = cluster.defaultBlockState().setValue(QuartzClusterBlock.POWERED, true);
        cluster.tick(powered, level, BlockPos.ZERO, net.minecraft.util.RandomSource.create());

        verify(level).setBlock(eq(BlockPos.ZERO),
                eq(cluster.defaultBlockState().setValue(QuartzClusterBlock.POWERED, false)), anyInt());
        verify(level, never()).scheduleTick(any(BlockPos.class), any(Block.class), anyInt());
    }

    @Test
    void quartzClusterSchedulesOnNeighbourChange() {
        Level level = mock(Level.class);
        Block cluster = block("quartz_cluster");
        cluster.neighborChanged(cluster.defaultBlockState(), level, BlockPos.ZERO, cluster, BlockPos.ZERO, false);
        verify(level).scheduleTick(eq(BlockPos.ZERO), eq(cluster), anyInt());
    }
}
