package com.github.thedeathlycow.moregeodes.mixin;

import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.LargeCrystalClusterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SimpleBlockFeature.class)
public class SimpleBlockFeatureMixin {

    @Inject(
            method = "place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void placeTallClusterBlockUpperHalf(
            WorldGenLevel structureWorldAccess,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BlockPos blockPos,
            CallbackInfoReturnable<Boolean> cir,
            BlockState blockState
    ) {
        Block block = blockState.getBlock();
        if (block instanceof LargeCrystalClusterBlock) {
            Direction facing = blockState.getValue(CrystalClusterBlock.FACING);
            if (!structureWorldAccess.isEmptyBlock(blockPos.relative(facing))) {
                cir.setReturnValue(false);
                return;
            }

            LargeCrystalClusterBlock.placeAt(structureWorldAccess, blockState, blockPos, Block.UPDATE_CLIENTS);

            cir.setReturnValue(true);
        }


    }

}
