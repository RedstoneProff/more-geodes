package com.github.thedeathlycow.moregeodes.mixin;

import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Display.BlockDisplay#setBlockState is private before 1.21. */
@Mixin(Display.BlockDisplay.class)
public interface BlockDisplayInvoker {
    @Invoker("setBlockState")
    void geodes$setBlockState(BlockState state);
}
