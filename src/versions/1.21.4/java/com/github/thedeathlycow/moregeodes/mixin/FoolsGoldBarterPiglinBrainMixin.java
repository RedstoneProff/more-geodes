package com.github.thedeathlycow.moregeodes.mixin;

import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.github.thedeathlycow.moregeodes.tag.ModItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinAi.class)
public abstract class FoolsGoldBarterPiglinBrainMixin {
    @Shadow
    protected static void setAngerTarget(ServerLevel world, AbstractPiglin piglin, LivingEntity target) {
    }

    @Shadow
    protected static void broadcastAngerTarget(ServerLevel world, AbstractPiglin piglin, LivingEntity target) {
    }

    @Inject(
            method = "wantsToPickup",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void piglinsRememberFoolsGold(
            Piglin piglin, ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Brain<Piglin> brain = piglin.getBrain();
        Optional<Boolean> memory = brain.getMemoryInternal(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD);

        boolean remembersFoolsGold = brain.hasMemoryValue(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD)
                && memory != null // NOSONAR - why tf is there a nullable optional
                && memory.isPresent()
                && memory.get();

        if (remembersFoolsGold && stack.is(ModItemTags.FOOLS_GOLD)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "stopHoldingOffHandItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void angerPiglinWhenBarterWithFoolsGold(ServerLevel world, Piglin piglin, boolean barter, CallbackInfo ci) {
        if (!barter) {
            return;
        }

        ItemStack stack = piglin.getOffhandItem();
        if (stack.is(ModItemTags.FOOLS_GOLD)) {
            angerAtNearbyPlayers(world, piglin);
            piglin.getBrain().setMemory(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD, true);
            stack.setCount(0);
            piglin.swing(InteractionHand.OFF_HAND);
            ci.cancel();
        }
    }

    private static void angerAtNearbyPlayers(ServerLevel world, Piglin piglin) {
        Brain<Piglin> brain = piglin.getBrain();
        Optional<Player> rememberedPlayer = brain
                .getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);

        rememberedPlayer.ifPresent(
                player -> {
                    setAngerTarget(world, piglin, player);
                    broadcastAngerTarget(world, piglin, player);
                }
        );
    }
}
