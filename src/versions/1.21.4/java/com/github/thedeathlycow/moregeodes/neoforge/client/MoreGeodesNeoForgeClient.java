package com.github.thedeathlycow.moregeodes.neoforge.client;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.client.EchoDisplayEntityRenderer;
import com.github.thedeathlycow.moregeodes.client.TuningTintSource;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

@EventBusSubscriber(modid = MoreGeodes.MODID, value = Dist.CLIENT)
public final class MoreGeodesNeoForgeClient {
    private static final List<Block> CUTOUT_BLOCKS = List.of(
            ModBlocks.EMERALD_CLUSTER,
            ModBlocks.LARGE_EMERALD_BUD,
            ModBlocks.MEDIUM_EMERALD_BUD,
            ModBlocks.SMALL_EMERALD_BUD,

            ModBlocks.QUARTZ_CLUSTER,
            ModBlocks.LARGE_QUARTZ_BUD,
            ModBlocks.MEDIUM_QUARTZ_BUD,
            ModBlocks.SMALL_QUARTZ_BUD,

            ModBlocks.DIAMOND_CLUSTER,

            ModBlocks.ECHO_CLUSTER,
            ModBlocks.LARGE_ECHO_BUD,
            ModBlocks.MEDIUM_ECHO_BUD,
            ModBlocks.SMALL_ECHO_BUD,

            ModBlocks.LAPIS_CLUSTER,
            ModBlocks.LARGE_LAPIS_BUD,
            ModBlocks.MEDIUM_LAPIS_BUD,
            ModBlocks.SMALL_LAPIS_BUD,

            ModBlocks.GYPSUM_ROSE,
            ModBlocks.LARGE_GYPSUM_BUD,
            ModBlocks.MEDIUM_GYPSUM_BUD,
            ModBlocks.SMALL_GYPSUM_BUD
    );

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Block block : CUTOUT_BLOCKS) {
                // deprecated, but still what the data-driven path ends up calling
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
        });
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GeodesEntityTypes.ECHO_DISPLAY, EchoDisplayEntityRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(GeodesIds.of(MoreGeodes.MODID, "tuning"), TuningTintSource.CODEC);
    }

    private MoreGeodesNeoForgeClient() {
    }
}
