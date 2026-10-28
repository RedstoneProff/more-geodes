package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class CrystalLootTests {
    private static final BlockPos POS = MoreGeodesTestHelper.CENTER;

    private static void breakBlockWith(GameTestHelper context, BlockPos pos, ItemStack tool) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        BlockPos abs = context.absolutePos(pos);
        BlockState state = context.getBlockState(pos);
        BlockEntity blockEntity = context.getBlockEntity(pos);
        state.getBlock().playerDestroy(
                context.getLevel(), player, abs, state, blockEntity, tool
        );
        context.getLevel().removeBlock(abs, false);
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_001_emerald_cluster_drops_four_emeralds_with_pickaxe(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER);

        breakBlockWith(context, POS, new ItemStack(Items.IRON_PICKAXE));

        context.assertItemEntityCountIs(Items.EMERALD, POS, 2.0, 4);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_001_emerald_cluster_drops_four_emeralds_with_empty_hand(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER);

        breakBlockWith(context, POS, ItemStack.EMPTY);

        context.assertItemEntityCountIs(Items.EMERALD, POS, 2.0, 4);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_001_emerald_cluster_drops_itself_with_silk_touch(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER);

        ItemStack silkPickaxe = new ItemStack(Items.IRON_PICKAXE);
        silkPickaxe.enchant(Enchantments.SILK_TOUCH, 1);
        breakBlockWith(context, POS, silkPickaxe);

        context.assertItemEntityCountIs(ModBlocks.EMERALD_CLUSTER.asItem(), POS, 2.0, 1);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_002_budding_emerald_drops_nothing(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.BUDDING_EMERALD);

        breakBlockWith(context, POS, new ItemStack(Items.IRON_PICKAXE));

        context.assertEntityNotPresent(EntityType.ITEM);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_002_budding_quartz_drops_nothing(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.BUDDING_QUARTZ);

        breakBlockWith(context, POS, new ItemStack(Items.IRON_PICKAXE));

        context.assertEntityNotPresent(EntityType.ITEM);
        context.succeed();
    }
}
