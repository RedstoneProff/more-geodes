package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
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

    private static void invokeStopHoldingOffHandItem(Piglin piglin, boolean barter) {
        try {
            Method method = PiglinAi.class.getDeclaredMethod(
                    "stopHoldingOffHandItem", Piglin.class, boolean.class
            );
            method.setAccessible(true);
            method.invoke(null, piglin, barter);
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
    public void F_013_piglin_bartering_fools_gold_angers_and_remembers(GameTestHelper context) {
        Piglin piglin = context.spawnWithNoFreeWill(EntityType.PIGLIN, PIGLIN_POS);
        var player = context.makeMockServerPlayerInLevel();
        player.getAbilities().invulnerable = false;
        var center = Vec3.atCenterOf(context.absolutePos(PIGLIN_POS.east()));
        player.teleportTo(center.x, center.y, center.z);
        piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, player);
        piglin.setItemInHand(
                InteractionHand.OFF_HAND,
                new ItemStack(ModItems.PYRITE_CHUNK)
        );

        invokeStopHoldingOffHandItem(piglin, true);

        context.assertTrue(
                piglin.getBrain().hasMemoryValue(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD),
                "piglin must remember the fools gold after bartering with it"
        );
        context.assertTrue(
                piglin.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT),
                "piglin must get angry at the player after bartering with fools gold"
        );
        context.assertTrue(
                piglin.getOffhandItem().isEmpty(),
                "piglin must drop the fools gold after bartering with it"
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_013_piglin_remembering_fools_gold_refuses_to_pick_it_up(GameTestHelper context) {
        Piglin piglin = context.spawnWithNoFreeWill(EntityType.PIGLIN, PIGLIN_POS);

        context.assertTrue(
                invokeWantsToPickup(piglin, new ItemStack(Items.GOLD_INGOT)),
                "a fresh piglin must want to pick up a gold ingot"
        );

        piglin.getBrain().setMemory(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD, true);

        context.assertFalse(
                invokeWantsToPickup(piglin, new ItemStack(ModItems.PYRITE_CHUNK)),
                "a piglin that remembers fools gold must refuse to pick up fools gold"
        );
        context.assertTrue(
                invokeWantsToPickup(piglin, new ItemStack(Items.GOLD_INGOT)),
                "a piglin that remembers fools gold must still want regular gold"
        );
        context.succeed();
    }
}
