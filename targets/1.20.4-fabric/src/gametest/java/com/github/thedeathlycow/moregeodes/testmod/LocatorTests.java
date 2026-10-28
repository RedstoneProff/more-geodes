package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.entity.EchoDisplay;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.item.CrystalLocatorItem;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedCrystalLocatorItem;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class LocatorTests {
    private static final BlockPos CENTER = new BlockPos(2, 2, 2);

    private static Player playerAtCenter(GameTestHelper context) {
        return playerAt(context, CENTER);
    }

    private static Player playerAt(GameTestHelper context, BlockPos relativePos) {
        Player player = context.makeMockPlayer();
        BlockPos absCenter = context.absolutePos(relativePos);
        player.setPos(absCenter.getX() + 0.5, absCenter.getY(), absCenter.getZ() + 0.5);
        return player;
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void tuned_locator_pings_a_matching_crystal(GameTestHelper context) {
        context.setBlock(CENTER, Blocks.AMETHYST_CLUSTER);

        Player player = playerAtCenter(context);
        ItemStack locator = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);

        ModItems.TUNED_CRYSTAL_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.assertEntityPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.assertTrue(player.getCooldowns().isOnCooldown(ModItems.TUNED_CRYSTAL_LOCATOR),
                "the tuned locator must go on cooldown after use");
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void untuned_locator_pings_nothing(GameTestHelper context) {
        context.setBlock(CENTER, Blocks.AMETHYST_CLUSTER);

        Player player = playerAtCenter(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CRYSTAL_LOCATOR));

        ModItems.CRYSTAL_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.assertEntityNotPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void wrongly_tuned_locator_pings_nothing(GameTestHelper context) {
        context.setBlock(CENTER, Blocks.AMETHYST_CLUSTER);

        Player player = playerAtCenter(context);
        ItemStack locator = TunedLocatorData.makeTunedLocatorStack("geodes:diamond");
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);

        ModItems.TUNED_CRYSTAL_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.assertEntityNotPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void echo_locator_detects_ore_through_stone_and_takes_damage(GameTestHelper context) {
        BlockPos sceneCenter = CENTER.south();
        context.setBlock(sceneCenter.north(), Blocks.STONE);
        context.setBlock(sceneCenter.north(2), Blocks.IRON_ORE);

        Player player = playerAt(context, sceneCenter);
        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);

        ModItems.ECHO_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.assertTrue(locator.getDamageValue() == 1,
                "echo locator must take exactly 1 damage per detected ore, got " + locator.getDamageValue());
        context.assertTrue(player.getCooldowns().isOnCooldown(ModItems.ECHO_LOCATOR),
                "echo locator must go on cooldown after use");
        context.assertEntityPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void echo_locator_ignores_blocks_outside_its_range(GameTestHelper context) {
        Player player = playerAtCenter(context);
        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);

        ModItems.ECHO_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.assertTrue(locator.getDamageValue() == 0,
                "echo locator must not take damage when nothing is detected");
        context.assertEntityNotPresent(GeodesEntityTypes.ECHO_DISPLAY);
        context.succeed();
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void echo_display_pings_and_becomes_visible_after_delay(GameTestHelper context) {
        context.setBlock(CENTER.north(), Blocks.IRON_ORE);

        Player player = playerAtCenter(context);
        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);
        ModItems.ECHO_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.succeedWhen(() -> {
            List<EchoDisplay> displays = context.getLevel()
                    .getEntitiesOfClass(EchoDisplay.class, context.getBounds(),
                            Entity::isAlive);
            context.assertTrue(!displays.isEmpty(), "echo display was not spawned");
            EchoDisplay display = displays.get(0);
            context.assertTrue(display.isCurrentlyGlowing(),
                    "echo display did not start glowing after its glow delay");
            context.assertTrue(!display.isInvisible(),
                    "echo display did not become visible when it pinged");
        });
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void echo_display_save_load_round_trip(GameTestHelper context) {
        context.setBlock(CENTER.north(), Blocks.IRON_ORE);

        Player player = playerAtCenter(context);
        ItemStack locator = new ItemStack(ModItems.ECHO_LOCATOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, locator);
        ModItems.ECHO_LOCATOR.use(context.getLevel(), player, InteractionHand.MAIN_HAND);

        context.succeedWhen(() -> {
            List<EchoDisplay> displays = context.getLevel()
                    .getEntitiesOfClass(EchoDisplay.class, context.getBounds(),
                            Entity::isAlive);
            context.assertTrue(!displays.isEmpty(), "echo display was not spawned");

            EchoDisplay original = displays.get(0);
            CompoundTag saved = new CompoundTag();
            original.saveWithoutId(saved);
            context.assertTrue(saved.contains("time_to_live"), "saved NBT is missing time_to_live");
            context.assertTrue(saved.contains("glow_time"), "saved NBT is missing glow_time");
            context.assertTrue(saved.contains("is_pinged"), "saved NBT is missing is_pinged");

            EchoDisplay loaded = GeodesEntityTypes.ECHO_DISPLAY.create(context.getLevel());
            context.assertTrue(loaded != null, "could not create a fresh echo display");
            loaded.load(saved);

            CompoundTag resaved = new CompoundTag();
            loaded.saveWithoutId(resaved);
            context.assertTrue(saved.getInt("time_to_live") == resaved.getInt("time_to_live"),
                    "time_to_live changed across save/load: " + saved.getInt("time_to_live")
                            + " -> " + resaved.getInt("time_to_live"));
            context.assertTrue(saved.getInt("glow_time") == resaved.getInt("glow_time"),
                    "glow_time changed across save/load");
            context.assertTrue(saved.getBoolean("is_pinged") == resaved.getBoolean("is_pinged"),
                    "is_pinged changed across save/load");
            context.succeed();
        });
    }

    @GameTest(template = "geodes-test:budding_growth_tests.empty5x4x5")
    public void tuned_locator_tuning_lookup_and_tooltip(GameTestHelper context) {
        ServerLevel level = context.getLevel();

        ItemStack tuned = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        Tuning amethyst = TunedCrystalLocatorItem.getTuning(level.registryAccess(), tuned);
        context.assertTrue(amethyst != null, "amethyst tuning not found in the tunings registry");
        context.assertTrue(((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR).isTuned(tuned, level.registryAccess()),
                "tuned locator with a tuning id must report isTuned=true");

        List<Component> tooltip = new ArrayList<>();
        ModItems.TUNED_CRYSTAL_LOCATOR.appendHoverText(tuned, level, tooltip, TooltipFlag.NORMAL);
        context.assertTrue(tooltip.stream().anyMatch(line -> line.getString().equals("Tuned to Amethyst Crystals")),
                "tuned locator tooltip must show the tuning description, got: " + tooltip);

        ItemStack plain = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        context.assertTrue(!((TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR).isTuned(plain, level.registryAccess()),
                "tuned locator without a tuning id must report isTuned=false");

        ItemStack bad = TunedLocatorData.makeTunedLocatorStack("geodes:does_not_exist");
        context.assertTrue(TunedCrystalLocatorItem.getTuning(level.registryAccess(), bad) == null,
                "unknown tuning id must resolve to null");

        List<Component> plainTooltip = new ArrayList<>();
        ModItems.CRYSTAL_LOCATOR.appendHoverText(plain, level, plainTooltip, TooltipFlag.NORMAL);
        context.assertTrue(plainTooltip.stream().anyMatch(line -> line.getString().equals("Untuned")),
                "plain locator tooltip must show the untuned hint, got: " + plainTooltip);
        context.assertTrue(!((CrystalLocatorItem) ModItems.CRYSTAL_LOCATOR).isTuned(plain, level.registryAccess()),
                "the plain crystal locator is never tuned");
        context.succeed();
    }
}
