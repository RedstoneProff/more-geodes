package com.github.thedeathlycow.moregeodes.blocks;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuddingBlockTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    @Test
    void emeraldBuddingChainIsSmallMediumLargeCluster() {
        List<Block> chain = ((GeodeBuddingBlock) ModBlocks.BUDDING_EMERALD).getClusters();
        assertEquals(List.of(ModBlocks.SMALL_EMERALD_BUD, ModBlocks.MEDIUM_EMERALD_BUD,
                ModBlocks.LARGE_EMERALD_BUD, ModBlocks.EMERALD_CLUSTER), chain);
    }

    @Test
    void quartzBuddingChainIsSmallMediumLargeCluster() {
        List<Block> chain = ((GeodeBuddingBlock) ModBlocks.BUDDING_QUARTZ).getClusters();
        assertEquals(List.of(ModBlocks.SMALL_QUARTZ_BUD, ModBlocks.MEDIUM_QUARTZ_BUD,
                ModBlocks.LARGE_QUARTZ_BUD, ModBlocks.QUARTZ_CLUSTER), chain);
    }

    @Test
    void echoBuddingChainIsSmallMediumLargeCluster() {
        List<Block> chain = ((GeodeBuddingBlock) ModBlocks.BUDDING_ECHO_BLOCK).getClusters();
        assertEquals(List.of(ModBlocks.SMALL_ECHO_BUD, ModBlocks.MEDIUM_ECHO_BUD,
                ModBlocks.LARGE_ECHO_BUD, ModBlocks.ECHO_CLUSTER), chain);
    }

    @Test
    void lapisBuddingChainIsSmallMediumLargeCluster() {
        List<Block> chain = ((GeodeBuddingBlock) ModBlocks.BUDDING_LAPIS).getClusters();
        assertEquals(List.of(ModBlocks.SMALL_LAPIS_BUD, ModBlocks.MEDIUM_LAPIS_BUD,
                ModBlocks.LARGE_LAPIS_BUD, ModBlocks.LAPIS_CLUSTER), chain);
    }

    @Test
    void gypsumBuddingChainIsSmallMediumLargeRose() {
        List<Block> chain = ((GeodeBuddingBlock) ModBlocks.BUDDING_GYPSUM).getClusters();
        assertEquals(List.of(ModBlocks.SMALL_GYPSUM_BUD, ModBlocks.MEDIUM_GYPSUM_BUD,
                ModBlocks.LARGE_GYPSUM_BUD, ModBlocks.GYPSUM_ROSE), chain);
    }

    @Test
    void gypsumBuddingBlockGrowsUpwardOnly() {
        assertTrue(ModBlocks.BUDDING_GYPSUM instanceof DirectedBuddingBlock,
                "budding gypsum must grow in one direction only");
        for (Block budding : List.of(ModBlocks.BUDDING_EMERALD, ModBlocks.BUDDING_QUARTZ,
                ModBlocks.BUDDING_ECHO_BLOCK, ModBlocks.BUDDING_LAPIS)) {
            assertTrue(!(budding instanceof DirectedBuddingBlock),
                    budding + " must grow in any direction");
        }
    }

    @Test
    void clusterStatesCarryTheClusterFacingProperty() {
        for (BlockState state : ((GeodeBuddingBlock) ModBlocks.BUDDING_EMERALD).getClusterStates()) {
            assertTrue(state.hasProperty(CrystalClusterBlock.FACING),
                    "every bud/cluster state must have the FACING property");
        }
    }

    @Test
    void clustersListIsUnmodifiable() {
        List<Block> chain = ((GeodeBuddingBlock) ModBlocks.BUDDING_EMERALD).getClusters();
        assertEquals(true, chain instanceof java.util.List<Block> unmod && true);
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> chain.add(ModBlocks.EMERALD_CLUSTER));
    }
}
