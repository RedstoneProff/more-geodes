package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class PiglinFoolsGoldTests {
    private static final BlockPos PIGLIN_POS = MoreGeodesTestHelper.CENTER;

    private static void invokeStopHoldingOffHandItem(ServerLevel level, Piglin piglin, boolean barter) {
        try {
            Method method = PiglinAi.class.getDeclaredMethod(
                    "stopHoldingOffHandItem", ServerLevel.class, Piglin.class, boolean.class
            );
            method.setAccessible(true);
            method.invoke(null, level, piglin, barter);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("could not invoke PiglinAi.stopHoldingOffHandItem", e);
        }
    }

    private static boolean invokeWantsToPickup(Piglin piglin, ItemStack stack) {
        try {
            Method method = PiglinAi.class.getDeclaredMethod(
                    "wantsToPickup", Piglin.class, ItemStack.class
            );
            method.setAccessible(true);
            return (boolean) method.invoke(null, piglin, stack);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("could not invoke PiglinAi.wantsToPickup", e);
        }
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void bartering_fools_gold_angry_and_remembered(GameTestHelper context) {
        Piglin piglin = context.spawnWithNoFreeWill(EntityType.PIGLIN, PIGLIN_POS);
        var player = context.makeMockServerPlayerInLevel();
        player.getAbilities().invulnerable = false;
        var center = Vec3.atCenterOf(context.absolutePos(PIGLIN_POS.east()));
        player.teleportTo(center.x, center.y, center.z);
        piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, player);
        ItemStack pyrite = new ItemStack(ModItems.PYRITE_CHUNK);
        piglin.setItemInHand(InteractionHand.OFF_HAND, pyrite);

        invokeStopHoldingOffHandItem(context.getLevel(), piglin, true);

        context.assertTrue(
                piglin.getBrain().hasMemoryValue(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD),
                "the fools-gold memory must be set after a fools-gold barter");
        context.assertTrue(
                piglin.getBrain().getMemoryInternal(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD).orElse(false),
                "the fools-gold memory must be true");
        context.assertTrue(pyrite.getCount() == 0,
                "the fools gold must be consumed, not bartered out");
        context.assertTrue(
                piglin.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT),
                "the piglin must be angry at the bartering player");
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void piglin_that_remembers_fools_gold_refuses_it_but_still_takes_gold(GameTestHelper context) {
        Piglin piglin = context.spawnWithNoFreeWill(EntityType.PIGLIN, PIGLIN_POS);

        context.assertTrue(
                invokeWantsToPickup(piglin, new ItemStack(Items.GOLD_INGOT)),
                "a fresh piglin must want to pick up a gold ingot"
        );

        piglin.getBrain().setMemory(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD, true);

        context.assertFalse(
                invokeWantsToPickup(piglin, new ItemStack(ModItems.PYRITE_CHUNK)),
                "a piglin that remembers fools gold must refuse pyrite");
        context.assertTrue(
                invokeWantsToPickup(piglin, new ItemStack(Items.GOLD_INGOT)),
                "regular gold must still be bartered after a fools-gold incident");
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void fresh_piglin_still_accepts_fools_gold(GameTestHelper context) {
        Piglin piglin = context.spawnWithNoFreeWill(EntityType.PIGLIN, PIGLIN_POS);
        context.assertTrue(!piglin.getBrain().hasMemoryValue(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD),
                "a fresh piglin must not remember fools gold yet");
        context.assertTrue(invokeWantsToPickup(piglin, new ItemStack(ModItems.PYRITE_CHUNK)),
                "a piglin without the fools-gold memory must still accept pyrite");
        context.succeed();
    }
}
