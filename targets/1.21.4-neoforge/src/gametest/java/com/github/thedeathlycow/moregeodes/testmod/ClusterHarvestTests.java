package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.LargeCrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class ClusterHarvestTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void emerald_cluster_drops_emeralds_without_silk_touch(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(POS),
                context.getBlockState(POS), null, pickaxe
        );
        context.setBlock(POS, Blocks.AIR);

        context.assertItemEntityCountIs(Items.EMERALD, POS, 2.0, 4);
        context.succeed();
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void emerald_cluster_drops_itself_with_silk_touch(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        Holder<Enchantment> silkTouch = context.getLevel().registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);
        pickaxe.enchant(silkTouch, 1);
        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(POS),
                context.getBlockState(POS), null, pickaxe
        );
        context.setBlock(POS, Blocks.AIR);

        context.assertItemEntityCountIs(ModBlocks.EMERALD_CLUSTER.asItem(), POS, 2.0, 1);
        context.assertItemEntityNotPresent(Items.EMERALD, POS, 2.0);
        context.succeed();
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void breaking_rose_upper_half_removes_lower_anchor(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absPos = context.absolutePos(POS);
        LargeCrystalClusterBlock.placeAt(level, ModBlocks.GYPSUM_ROSE.defaultBlockState().setValue(CrystalClusterBlock.FACING, Direction.UP), absPos, Block.UPDATE_ALL);

        BlockState lower = level.getBlockState(absPos);
        context.assertTrue(lower.getBlock() == ModBlocks.GYPSUM_ROSE
                        && lower.getValue(LargeCrystalClusterBlock.HALF) == DoubleBlockHalf.LOWER,
                "rose lower half was not placed at the anchor position");
        BlockState upper = level.getBlockState(absPos.above());
        context.assertTrue(upper.getBlock() == ModBlocks.GYPSUM_ROSE
                        && upper.getValue(LargeCrystalClusterBlock.HALF) == DoubleBlockHalf.UPPER,
                "rose upper half was not placed above the anchor");

        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        level.getBlockState(absPos.above()).getBlock().playerWillDestroy(
                level, absPos.above(), upper, player);

        context.assertTrue(level.getBlockState(absPos).isAir(),
                "breaking the rose upper half must also remove the lower anchor half");
        context.succeed();
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void rose_placement_state_faces_the_clicked_side(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos absPos = context.absolutePos(POS);
        Player player = context.makeMockPlayer(GameType.SURVIVAL);

        BlockPlaceContext placeCtx = new BlockPlaceContext(
                player, InteractionHand.MAIN_HAND,
                new ItemStack(ModBlocks.GYPSUM_ROSE),
                new BlockHitResult(Vec3.atBottomCenterOf(absPos), Direction.NORTH, absPos, false));
        BlockState state = ModBlocks.GYPSUM_ROSE.getStateForPlacement(placeCtx);
        context.assertTrue(state != null, "getStateForPlacement returned null for a valid placement");
        context.assertTrue(state.getValue(CrystalClusterBlock.FACING) == Direction.NORTH,
                "cluster placed on a north face must face north, got " + state.getValue(CrystalClusterBlock.FACING));

        level.setBlock(absPos, state, Block.UPDATE_ALL);
        ModBlocks.GYPSUM_ROSE.setPlacedBy(level, absPos, state, player, new ItemStack(ModBlocks.GYPSUM_ROSE));
        BlockState head = level.getBlockState(absPos.north());
        context.assertTrue(head.getBlock() == ModBlocks.GYPSUM_ROSE
                        && head.getValue(LargeCrystalClusterBlock.HALF) == DoubleBlockHalf.UPPER,
                "setPlacedBy must place the upper half in the facing direction");

        context.succeed();
    }
}
