package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.entity.EchoDisplay;
import com.github.thedeathlycow.moregeodes.features.ModConfiguredFeatures;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.util.RandomSource;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class EdgeCaseTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void emerald_cluster_drops_emeralds_even_with_an_empty_hand(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

        Player player = context.makeMockPlayer();
        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(POS),
                context.getBlockState(POS), null, ItemStack.EMPTY
        );
        context.setBlock(POS, Blocks.AIR);

        context.assertItemEntityCountIs(Items.EMERALD, POS, 2.0, 4);
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void emerald_cluster_drops_emeralds_with_a_wrong_tool(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

        Player player = context.makeMockPlayer();
        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(POS),
                context.getBlockState(POS), null, new ItemStack(Items.IRON_AXE)
        );
        context.setBlock(POS, Blocks.AIR);

        context.assertItemEntityCountIs(Items.EMERALD, POS, 2.0, 4);
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void locator_cooldown_is_per_player(GameTestHelper context) {
        context.setBlock(POS, Blocks.AMETHYST_CLUSTER);

        Player playerA = playerAt(context, POS);
        Player playerB = playerAt(context, POS.south());

        ItemStack locatorA = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        playerA.setItemInHand(InteractionHand.MAIN_HAND, locatorA);
        ModItems.TUNED_CRYSTAL_LOCATOR.use(context.getLevel(), playerA, InteractionHand.MAIN_HAND);

        context.assertTrue(playerA.getCooldowns().isOnCooldown(ModItems.TUNED_CRYSTAL_LOCATOR),
                "player A's locator must be on cooldown after use");
        context.assertTrue(!playerB.getCooldowns().isOnCooldown(ModItems.TUNED_CRYSTAL_LOCATOR),
                "player B must not inherit player A's cooldown");

        ItemStack locatorB = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        playerB.setItemInHand(InteractionHand.MAIN_HAND, locatorB);
        ModItems.TUNED_CRYSTAL_LOCATOR.use(context.getLevel(), playerB, InteractionHand.MAIN_HAND);

        context.assertTrue(playerB.getCooldowns().isOnCooldown(ModItems.TUNED_CRYSTAL_LOCATOR),
                "player B's own use must put their locator on cooldown");
        long displays = context.getLevel()
                .getEntitiesOfClass(EchoDisplay.class, context.getBounds(), Entity::isAlive)
                .size();
        context.assertTrue(displays >= 2, "both players must ping, got " + displays + " displays");
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void emerald_geode_feature_places_its_shell_layers(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos origin = context.absolutePos(POS).above(24);
        fillWithStone(level, origin, 20);

        boolean placed = placeConfiguredFeature(level, ModConfiguredFeatures.EMERALD_GEODE, origin);
        context.assertTrue(placed, "the emerald geode configured feature failed to place");

        context.assertTrue(containsBlockNear(level, origin, 8, Blocks.SMOOTH_BASALT),
                "emerald geode shell has no smooth basalt outer layer");
        context.assertTrue(containsBlockNear(level, origin, 8, Blocks.CALCITE),
                "emerald geode shell has no calcite middle layer");
        context.assertTrue(containsBlockNear(level, origin, 8, ModBlocks.EMERALD_CRYSTAL_BLOCK),
                "emerald geode has no emerald crystal block inner layer");
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void quartz_geode_feature_places_its_tuff_middle_layer(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        BlockPos origin = context.absolutePos(POS).above(24);
        fillWithStone(level, origin, 20);

        boolean placed = placeConfiguredFeature(level, ModConfiguredFeatures.QUARTZ_GEODE, origin);
        context.assertTrue(placed, "the quartz geode configured feature failed to place");

        context.assertTrue(containsBlockNear(level, origin, 8, Blocks.TUFF),
                "quartz geode shell has no tuff middle layer");
        context.assertTrue(containsBlockNear(level, origin, 8, Blocks.SMOOTH_BASALT),
                "quartz geode shell has no smooth basalt outer layer");
        context.assertTrue(containsBlockNear(level, origin, 8, ModBlocks.QUARTZ_CRYSTAL_BLOCK),
                "quartz geode has no quartz crystal block inner layer");
        context.succeed();
    }

    private static Player playerAt(GameTestHelper context, BlockPos relativePos) {
        Player player = context.makeMockPlayer();
        BlockPos abs = context.absolutePos(relativePos);
        player.setPos(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5);
        return player;
    }

    private static boolean placeConfiguredFeature(ServerLevel level,
                                                  ResourceKey<ConfiguredFeature<?, ?>> key,
                                                  BlockPos pos) {
        ConfiguredFeature<?, ?> feature = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(key);
        return placeRaw(feature, level, pos);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean placeRaw(ConfiguredFeature<?, ?> feature, ServerLevel level, BlockPos pos) {
        ConfiguredFeature raw = feature;
        return raw.feature().place(raw.config(), level, level.getChunkSource().getGenerator(),
                RandomSource.create(), pos);
    }

    private static void fillWithStone(ServerLevel level, BlockPos center, int radius) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private static boolean containsBlockNear(ServerLevel level, BlockPos origin, int radius, Block block) {
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            if (level.getBlockState(pos).is(block)) {
                return true;
            }
        }
        return false;
    }
}
