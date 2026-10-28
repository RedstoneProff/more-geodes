package com.github.thedeathlycow.moregeodes.mixin.compat.carpet;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.blocks.GeodesPushReactions;
import net.minecraft.world.level.material.PushReaction;
import com.github.thedeathlycow.moregeodes.blocks.GeodeBuddingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MovableAmethystMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getPistonPushReaction", at = @At("HEAD"), cancellable = true)
    private void onGetPistonPushReaction(CallbackInfoReturnable<PushReaction> cir) {
        if (MoreGeodes.isMovableAmethystEnabled() && this.getBlock() instanceof GeodeBuddingBlock) {
            cir.setReturnValue(GeodesPushReactions.normal());
        }
    }
}
