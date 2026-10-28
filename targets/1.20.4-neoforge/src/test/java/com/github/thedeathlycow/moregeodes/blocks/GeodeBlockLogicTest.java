package com.github.thedeathlycow.moregeodes.blocks;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeodeBlockLogicTest {
    @TempDir
    static Path configDir;

    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
    }

    private static Block block(String name) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(MoreGeodes.MODID, name));
    }

    @Test
    void buddingEmeraldGrowsSmallBudIntoAir() {
        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        Optional<Block> next = budding.getNextBlockForGrowth(
                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Direction.UP);
        assertTrue(next.isPresent());
        assertEquals(block("small_emerald_bud"), next.get());
    }

    @Test
    void buddingEmeraldAdvancesBudChainToCluster() {
        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        Block small = block("small_emerald_bud");
        Block medium = block("medium_emerald_bud");
        Block large = block("large_emerald_bud");
        Block cluster = block("emerald_cluster");

        assertEquals(Optional.of(medium), budding.getNextBlockForGrowth(
                small.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.UP), Direction.UP));
        assertEquals(Optional.of(large), budding.getNextBlockForGrowth(
                medium.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.UP), Direction.UP));
        assertEquals(Optional.of(cluster), budding.getNextBlockForGrowth(
                large.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.UP), Direction.UP));
    }

    @Test
    void growthIsTerminalAtCluster() {
        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        Optional<Block> next = budding.getNextBlockForGrowth(
                block("emerald_cluster").defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP),
                Direction.UP);
        assertTrue(next.isEmpty(), "a full cluster must not grow further");
    }

    @Test
    void growthChainCoversAllSixDirections() {
        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_emerald");
        for (Direction direction : Direction.values()) {
            Optional<Block> next = budding.getNextBlockForGrowth(
                    net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), direction);
            assertTrue(next.isPresent(), "no growth into " + direction);
            assertEquals(block("small_emerald_bud"), next.get());
        }
    }

    @Test
    void buddingGypsumIsDirectedUpward() throws Exception {
        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_gypsum");
        assertTrue(budding instanceof DirectedBuddingBlock,
                "budding gypsum must be a directed budding block");
        java.lang.reflect.Field directionField = DirectedBuddingBlock.class.getDeclaredField("direction");
        directionField.setAccessible(true);
        assertEquals(Direction.UP, directionField.get(budding),
                "budding gypsum must be configured to grow upward only");
    }

    @Test
    void buddingGypsumAdvancesItsOwnChainUpward() {
        GeodeBuddingBlock budding = (GeodeBuddingBlock) block("budding_gypsum");
        assertEquals(Optional.of(block("medium_gypsum_bud")), budding.getNextBlockForGrowth(
                block("small_gypsum_bud").defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP), Direction.UP));
        assertEquals(Optional.of(block("large_gypsum_bud")), budding.getNextBlockForGrowth(
                block("medium_gypsum_bud").defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP), Direction.UP));
        assertEquals(Optional.of(block("gypsum_rose")), budding.getNextBlockForGrowth(
                block("large_gypsum_bud").defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP), Direction.UP));
        assertTrue(budding.getNextBlockForGrowth(
                block("gypsum_rose").defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP)
                        .setValue(LargeCrystalClusterBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER),
                Direction.UP).isEmpty());
    }

    @Test
    void buddingBlockClusterListsAreImmutableAndComplete() {
        GeodeBuddingBlock emerald = (GeodeBuddingBlock) block("budding_emerald");
        var clusters = emerald.getClusters();
        assertEquals(4, clusters.size());
        assertEquals(block("small_emerald_bud"), clusters.get(0));
        assertEquals(block("emerald_cluster"), clusters.get(3));
    }

    @Test
    void poweredQuartzClustersEmitSignal15() {
        for (String name : new String[]{
                "quartz_cluster", "large_quartz_bud", "medium_quartz_bud", "small_quartz_bud"}) {
            Block quartz = block(name);
            BlockState powered = quartz.defaultBlockState().setValue(QuartzClusterBlock.POWERED, true);
            assertEquals(15, quartz.getSignal(powered, null, BlockPos.ZERO, Direction.UP),
                    name + " must emit signal 15 while powered");
            assertTrue(quartz.isSignalSource(quartz.defaultBlockState()),
                    name + " must be a signal source");
        }
    }

    @Test
    void unpoweredQuartzClustersEmitNoSignal() {
        for (String name : new String[]{
                "quartz_cluster", "large_quartz_bud", "medium_quartz_bud", "small_quartz_bud"}) {
            Block quartz = block(name);
            assertEquals(0, quartz.getSignal(quartz.defaultBlockState(), null, BlockPos.ZERO, Direction.UP),
                    name + " must be silent while unpowered");
        }
    }

    @Test
    void quartzClusterDefaultsToUnpowered() {
        assertFalse(block("quartz_cluster").defaultBlockState().getValue(QuartzClusterBlock.POWERED));
    }

    @Test
    void clusterShapesDifferPerFacing() {
        Block cluster = block("emerald_cluster");
        VoxelShape up = cluster.getShape(
                cluster.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.UP),
                null, BlockPos.ZERO, CollisionContext.empty());
        VoxelShape down = cluster.getShape(
                cluster.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.DOWN),
                null, BlockPos.ZERO, CollisionContext.empty());
        VoxelShape north = cluster.getShape(
                cluster.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.NORTH),
                null, BlockPos.ZERO, CollisionContext.empty());
        assertNotEquals(up, down, "up and down shapes must differ");
        assertNotEquals(up, north, "up and north shapes must differ");
        assertFalse(up.isEmpty());
    }

    @Test
    void largeGypsumRoseHasSeparateLowerHalfShape() {
        Block rose = block("gypsum_rose");
        VoxelShape lower = rose.getShape(
                rose.defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP)
                        .setValue(LargeCrystalClusterBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER),
                null, BlockPos.ZERO, CollisionContext.empty());
        VoxelShape upper = rose.getShape(
                rose.defaultBlockState()
                        .setValue(CrystalClusterBlock.FACING, Direction.UP)
                        .setValue(LargeCrystalClusterBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER),
                null, BlockPos.ZERO, CollisionContext.empty());
        assertFalse(lower.isEmpty());
        assertFalse(upper.isEmpty());
        assertNotEquals(lower, upper, "the two halves of a gypsum rose must have different shapes");
    }
}
