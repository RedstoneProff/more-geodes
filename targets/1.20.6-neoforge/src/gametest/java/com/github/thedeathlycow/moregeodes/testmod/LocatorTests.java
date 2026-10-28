package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class LocatorTests {
    private static final BlockPos PLAYER_POS = MoreGeodesTestHelper.CENTER;
    private static final BlockPos CRYSTAL_POS = PLAYER_POS.east().east();

    private static ServerPlayer playerAt(GameTestHelper context, BlockPos pos) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        player.getAbilities().instabuild = false;
        player.getAbilities().invulnerable = false;
        Vec3 center = Vec3.atCenterOf(context.absolutePos(pos));
        player.teleportTo(center.x, center.y, center.z);
        return player;
    }

    private static InteractionResultHolder<ItemStack> useLocator(
            GameTestHelper context,
            ServerPlayer player,
            ItemStack stack
    ) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        return stack.getItem().use(context.getLevel(), player, InteractionHand.MAIN_HAND);
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_tuned_locator_pings_matching_crystal(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER);

        var tunings = context.getLevel().registryAccess().lookup(
                com.github.thedeathlycow.moregeodes.item.tuning.Tunings.REGISTRY_KEY
        );
        context.assertTrue(tunings.isPresent(), "tunings dynamic registry is not loaded");
        var amethystHolder = tunings.get().get(
                ResourceKey.create(
                        com.github.thedeathlycow.moregeodes.item.tuning.Tunings.REGISTRY_KEY,
                        new ResourceLocation("geodes", "amethyst")
                )
        );
        context.assertTrue(amethystHolder.isPresent(), "geodes:amethyst tuning is not loaded");
        context.assertTrue(
                amethystHolder.get().value().test(
                        context.getLevel(), context.absolutePos(CRYSTAL_POS)
                ),
                "the amethyst tuning must match a vanilla amethyst cluster"
        );

        ItemStack locator = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        InteractionResultHolder<ItemStack> result = useLocator(context, player, locator);

        context.assertTrue(
                result.getResult().consumesAction(),
                "using a tuned locator must succeed"
        );
        context.assertEntityPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_untuned_locator_pings_nothing(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER);

        useLocator(context, player, new ItemStack(ModItems.CRYSTAL_LOCATOR));

        context.assertEntityNotPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_wrong_tuning_pings_nothing(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, ModBlocks.EMERALD_CLUSTER);

        useLocator(context, player, TunedLocatorData.makeTunedLocatorStack("geodes:amethyst"));

        context.assertEntityNotPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_locator_on_cooldown_after_use(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER);

        ItemStack locator = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        useLocator(context, player, locator);

        context.assertTrue(
                player.getCooldowns().isOnCooldown(ModItems.TUNED_CRYSTAL_LOCATOR),
                "tuned locator must be on cooldown after use"
        );
        context.assertTrue(
                player.getCooldowns().isOnCooldown(ModItems.CRYSTAL_LOCATOR),
                "plain locator must share the cooldown with the tuned locator"
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_012_echo_locator_pings_locatable_blocks_and_damages_itself(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, ModBlocks.SMALL_EMERALD_BUD);
        context.setBlock(CRYSTAL_POS.west(), ModBlocks.SMALL_EMERALD_BUD);

        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        useLocator(context, player, locator);

        context.assertEntityPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.assertTrue(
                locator.getDamageValue() == 1,
                "echo locator must take 1 damage per use that pinged at least one block, got "
                        + locator.getDamageValue()
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_012_echo_locator_ignores_non_locatable_blocks(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, net.minecraft.world.level.block.Blocks.STONE);
        context.setBlock(CRYSTAL_POS.west(), net.minecraft.world.level.block.Blocks.STONE);

        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        useLocator(context, player, locator);

        context.assertEntityNotPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.assertTrue(
                locator.getDamageValue() == 0,
                "echo locator must not take damage when nothing was pinged, got "
                        + locator.getDamageValue()
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_echo_display_becomes_visible_after_delay(GameTestHelper context) {
        ServerPlayer player = playerAt(context, PLAYER_POS);
        context.setBlock(CRYSTAL_POS, net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER);

        useLocator(context, player, TunedLocatorData.makeTunedLocatorStack("geodes:amethyst"));

        // block displays have a zero-size bounding box, so search the whole structure
        context.succeedWhen(() -> {
            var origin = context.absolutePos(new BlockPos(0, 0, 0));
            var structure = new AABB(
                    origin.getX(), origin.getY(), origin.getZ(),
                    origin.getX() + 15, origin.getY() + 8, origin.getZ() + 15);
            var displays = context.getLevel().getEntities(
                    GeodesEntityTypes.ECHO_DISPLAY, structure, e -> true);
            context.assertTrue(!displays.isEmpty(), "expected an echo display in the structure");
            context.assertTrue(
                    !displays.get(0).isInvisible(),
                    "the echo display must become visible after the ping delay"
            );
        });
    }
}
