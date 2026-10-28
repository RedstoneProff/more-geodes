package com.github.thedeathlycow.moregeodes.mixin;

import com.github.thedeathlycow.moregeodes.entity.GeodesMemoryModules;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;

@Mixin(Piglin.class)
public class PiglinMemoryMixin {

    @ModifyArg(
            method = "brainProvider",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;provider(Ljava/util/Collection;Ljava/util/Collection;)Lnet/minecraft/world/entity/ai/Brain$Provider;"
            ),
            index = 0
    )
    private Collection<? extends MemoryModuleType<?>> registerFoolsGoldMemoryToPiglin(Collection<? extends MemoryModuleType<?>> memoryModules) {
        return new ImmutableList.Builder<MemoryModuleType<?>>()
                .addAll(memoryModules)
                .add(GeodesMemoryModules.REMEMBERS_FOOLS_GOLD)
                .build();
    }

}
