package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class PiglinFoolsGoldTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    private static Piglin spawnPiglin(GameTestHelper context) {
        return (Piglin) context.spawnWithNoFreeWill(EntityType.PIGLIN, POS);
    }

    private static Player playerAtCenter(GameTestHelper context) {
        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absCenter = context.absolutePos(POS);
        player.setPos(absCenter.getX() + 0.5, absCenter.getY(), absCenter.getZ() + 0.5);
        return player;
    }

    private static void stopHoldingOffHandItem(Piglin piglin, boolean barter) {
        try {
            Method method = PiglinAi.class.getDeclaredMethod("stopHoldingOffHandItem", Piglin.class, boolean.class);
            method.setAccessible(true);
            method.invoke(null, piglin, barter);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not invoke PiglinAi.stopHoldingOffHandItem", e);
        }
    }

    private static boolean wantsToPickup(Piglin piglin, ItemStack stack) {
        try {
            Method method = PiglinAi.class.getDeclaredMethod("wantsToPickup", Piglin.class, ItemStack.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, piglin, stack);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not invoke PiglinAi.wantsToPickup", e);
        }
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void bartering_fools_gold_angry_and_remembered(GameTestHelper context) {
        Piglin piglin = spawnPiglin(context);
        Player player = playerAtCenter(context);
        piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, player);
        ItemStack pyrite = new ItemStack(ModItems.PYRITE);
        piglin.setItemInHand(InteractionHand.OFF_HAND, pyrite);

        stopHoldingOffHandItem(piglin, true);

        Brain<Piglin> brain = piglin.getBrain();
        context.assertTrue(brain.hasMemoryValue(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD),
                "the fools-gold memory must be set after a fools-gold barter");
        context.assertTrue(brain.getMemoryInternal(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD).orElse(false),
                "the fools-gold memory must be true");
        context.assertTrue(pyrite.getCount() == 0,
                "the fools gold must be consumed, not bartered out");
        Optional<UUID> angryAt = brain.getMemoryInternal(MemoryModuleType.ANGRY_AT);
        context.assertTrue(angryAt.isPresent() && angryAt.get().equals(player.getUUID()),
                "the piglin must be angry at the bartering player");
        context.succeed();
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void piglin_that_remembers_fools_gold_refuses_it_but_still_takes_gold(GameTestHelper context) {
        Piglin piglin = spawnPiglin(context);
        piglin.getBrain().setMemory(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD, true);

        context.assertTrue(!wantsToPickup(piglin, new ItemStack(ModItems.PYRITE)),
                "a piglin that remembers fools gold must refuse pyrite");
        context.assertTrue(wantsToPickup(piglin, new ItemStack(Items.GOLD_INGOT)),
                "regular gold must still be bartered after a fools-gold incident");
        context.succeed();
    }

    @GameTest(template = "budding_growth_tests.empty5x4x5", templateNamespace = "geodes-test")
    public void fresh_piglin_still_accepts_fools_gold(GameTestHelper context) {
        Piglin piglin = spawnPiglin(context);
        context.assertTrue(!piglin.getBrain().hasMemoryValue(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD),
                "a fresh piglin must not remember fools gold yet");
        context.assertTrue(wantsToPickup(piglin, new ItemStack(ModItems.PYRITE)),
                "a piglin without the fools-gold memory must still accept pyrite");
        context.succeed();
    }
}
