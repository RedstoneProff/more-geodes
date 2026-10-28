package com.github.thedeathlycow.moregeodes.client;

import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class GeodesCutouts {

    public static void registerCutouts() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EMERALD_CLUSTER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_EMERALD_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MEDIUM_EMERALD_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMALL_EMERALD_BUD, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.QUARTZ_CLUSTER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_QUARTZ_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MEDIUM_QUARTZ_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMALL_QUARTZ_BUD, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DIAMOND_CLUSTER, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ECHO_CLUSTER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_ECHO_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MEDIUM_ECHO_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMALL_ECHO_BUD, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LAPIS_CLUSTER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_LAPIS_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MEDIUM_LAPIS_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMALL_LAPIS_BUD, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GYPSUM_ROSE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_GYPSUM_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MEDIUM_GYPSUM_BUD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMALL_GYPSUM_BUD, RenderType.cutout());
    }

}
