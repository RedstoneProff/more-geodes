package com.github.thedeathlycow.moregeodes.neoforge.client;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.client.EchoDisplayEntityRenderer;
import com.github.thedeathlycow.moregeodes.client.TuningTintSource;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.util.GeodesIds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = MoreGeodes.MODID, value = Dist.CLIENT)
public final class MoreGeodesNeoForgeClient {
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
