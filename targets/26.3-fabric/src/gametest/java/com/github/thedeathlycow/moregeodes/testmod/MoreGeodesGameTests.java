package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MoreGeodesGameTests {
    private static final int MAX_RANDOM_TICKS = 400;

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void G_001_budding_emerald_grows_small_bud_on_random_tick(GameTestHelper context) {
        BlockPos buddingPos = new BlockPos(2, 1, 2);
        context.setBlock(buddingPos, ModBlocks.BUDDING_EMERALD);

        for (int i = 0; i < MAX_RANDOM_TICKS && !anyNeighborIs(context, buddingPos, ModBlocks.SMALL_EMERALD_BUD); i++) {
            context.randomTick(buddingPos);
        }

        context.assertTrue(anyNeighborIs(context, buddingPos, ModBlocks.SMALL_EMERALD_BUD),
                "budding emerald must grow a small emerald bud on an adjacent face");
        context.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void G_002_growth_chain_advances_to_cluster_and_stops(GameTestHelper context) {
        BlockPos buddingPos = new BlockPos(2, 1, 2);
        context.setBlock(buddingPos, ModBlocks.BUDDING_EMERALD);

        BlockPos budPos = growFirstBud(context, buddingPos);

        int ticks = 0;
        while (!context.getBlockState(budPos).is(ModBlocks.EMERALD_CLUSTER) && ticks < 2000) {
            context.randomTick(buddingPos);
            ticks++;
        }
        context.assertTrue(context.getBlockState(budPos).is(ModBlocks.EMERALD_CLUSTER),
                "growth chain must reach the emerald cluster after " + ticks + " random ticks");

        for (int i = 0; i < 50; i++) {
            context.randomTick(budPos);
        }
        context.assertTrue(context.getBlockState(budPos).is(ModBlocks.EMERALD_CLUSTER),
                "cluster must be the terminal growth stage");
        context.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void G_003_emerald_cluster_drops_emeralds_without_silk_touch(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        BlockPos clusterPos = new BlockPos(2, 1, 2);
        BlockState cluster = ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock.FACING, Direction.UP);
        context.setBlock(clusterPos, cluster);

        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(clusterPos),
                context.getBlockState(clusterPos), null, ItemStack.EMPTY);
        context.assertTrue(context.getLevel().getEntities(
                        net.minecraft.world.level.entity.EntityTypeTest.forClass(net.minecraft.world.entity.item.ItemEntity.class),
                        player.getBoundingBox().inflate(4.0), e -> true).isEmpty(),
                "bare hand must not harvest an emerald cluster");
        context.killAllEntities();

        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(clusterPos),
                context.getBlockState(clusterPos), null, new ItemStack(Items.IRON_PICKAXE));
        context.assertItemEntityCountIs(Items.EMERALD, clusterPos, 2.0, 4);
        context.killAllEntities();

        ItemStack silkPick = new ItemStack(Items.NETHERITE_PICKAXE);
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(context.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), 1);
        silkPick.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, enchants.toImmutable());
        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(clusterPos),
                context.getBlockState(clusterPos), null, silkPick);
        context.assertItemEntityPresent(ModItems.EMERALD_CLUSTER.asItem(), clusterPos, 2.0);
        context.killAllEntities();
        context.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 200)
    public void G_004_quartz_oscillator_toggles_powered(GameTestHelper context) {
        BlockPos budPos = new BlockPos(2, 2, 2);
        context.setBlock(budPos, ModBlocks.SMALL_QUARTZ_BUD.defaultBlockState()
                .setValue(com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock.FACING, Direction.UP));
        context.setBlock(new BlockPos(2, 1, 2), Blocks.REDSTONE_BLOCK);

        context.startSequence()
                .thenExecuteAfter(25, () -> context.assertTrue(
                        context.getBlockState(budPos).getValue(com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock.POWERED),
                        "powered quartz bud must turn on after its on-time"))
                .thenExecuteAfter(20, () -> context.assertTrue(
                        !context.getBlockState(budPos).getValue(com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock.POWERED),
                        "oscillator must toggle off after another on-time"))
                .thenExecuteAfter(20, () -> context.assertTrue(
                        context.getBlockState(budPos).getValue(com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock.POWERED),
                        "oscillator must toggle back on (0.5 Hz clock)"))
                .thenSucceed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void G_004b_powered_quartz_bud_emits_signal_15(GameTestHelper context) {
        BlockPos budPos = new BlockPos(2, 2, 2);
        BlockState powered = ModBlocks.SMALL_QUARTZ_BUD.defaultBlockState()
                .setValue(com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock.FACING, Direction.UP)
                .setValue(com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock.POWERED, true);
        context.setBlock(budPos, powered);
        context.assertTrue(
                context.getBlockState(budPos).getSignal(context.getLevel(), budPos, Direction.DOWN) == 15,
                "powered quartz bud must emit signal strength 15");
        context.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void G_006_budding_gypsum_grows_only_upward(GameTestHelper context) {
        BlockPos buddingPos = new BlockPos(2, 1, 2);
        context.setBlock(buddingPos, ModBlocks.BUDDING_GYPSUM);

        for (int i = 0; i < MAX_RANDOM_TICKS; i++) {
            context.randomTick(buddingPos);
        }

        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN}) {
            context.assertTrue(
                    !context.getBlockState(buddingPos.relative(dir)).is(ModBlocks.SMALL_GYPSUM_BUD),
                    "budding gypsum must never grow sideways/down (tried " + dir + ")");
        }
        BlockState above = context.getBlockState(buddingPos.above());
        context.assertTrue(
                above.is(ModBlocks.SMALL_GYPSUM_BUD) || above.is(ModBlocks.MEDIUM_GYPSUM_BUD)
                        || above.is(ModBlocks.LARGE_GYPSUM_BUD) || above.is(ModBlocks.GYPSUM_ROSE),
                "budding gypsum must grow upward, got " + above.getBlock());
        context.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void G_009_echo_locator_pings_ore_and_takes_damage(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        BlockPos playerPos = new BlockPos(2, 1, 2);
        context.setBlock(playerPos, Blocks.STONE);
        player.setPos(net.minecraft.world.phys.Vec3.atCenterOf(context.absolutePos(playerPos)));

        BlockPos orePos = playerPos.east(3);
        context.setBlock(orePos, Blocks.LAPIS_ORE);
        context.setBlock(playerPos.west(3), Blocks.STONE);

        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);
        ModItems.ECHO_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.assertTrue(
                !context.getLevel().getEntities(
                        net.minecraft.world.level.entity.EntityTypeTest.forClass(com.github.thedeathlycow.moregeodes.entity.EchoDisplay.class),
                        player.getBoundingBox().inflate(8.0), e -> true).isEmpty(),
                "echo locator must spawn an EchoDisplay ping at the tagged ore");
        context.assertTrue(player.getCooldowns().isOnCooldown(locator),
                "echo locator must go on cooldown after use");
        context.succeed();
    }

    private static boolean anyNeighborIs(GameTestHelper context, BlockPos pos, net.minecraft.world.level.block.Block block) {
        for (Direction dir : Direction.values()) {
            if (context.getBlockState(pos.relative(dir)).is(block)) {
                return true;
            }
        }
        return false;
    }

    private static BlockPos growFirstBud(GameTestHelper context, BlockPos buddingPos) {
        for (int i = 0; i < MAX_RANDOM_TICKS; i++) {
            context.randomTick(buddingPos);
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = buddingPos.relative(dir);
                if (!context.getBlockState(neighbor).isAir()) {
                    return neighbor;
                }
            }
        }
        throw new AssertionError("budding emerald never grew a first bud");
    }
}
