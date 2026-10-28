package com.github.thedeathlycow.moregeodes.mixin.client;

import com.github.thedeathlycow.moregeodes.client.TuningTintSource;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemTintSources.class)
public abstract class ItemTintSourcesMixin {

    @Shadow
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ItemTintSource>> ID_MAPPER;

    @Inject(
            method = "bootstrap",
            at = @At("TAIL")
    )
    private static void registerTuningTint(CallbackInfo ci) {
        ID_MAPPER.put(GeodesIds.of("geodes", "tuning"), TuningTintSource.CODEC);
    }

}
