package com.github.thedeathlycow.moregeodes.neoforge.client;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.client.EchoDisplayEntityRenderer;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedCrystalLocatorItem;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.item.tuning.Tunings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = MoreGeodes.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
        });
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GeodesEntityTypes.ECHO_DISPLAY, EchoDisplayEntityRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(MoreGeodesNeoForgeClient::getLocatorColor, ModItems.TUNED_CRYSTAL_LOCATOR);
    }

    private static int getLocatorColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return Tuning.UNTUNED.getColor();
        }
        Registry<Tuning> tunings = minecraft.level.registryAccess().registryOrThrow(Tunings.REGISTRY_KEY);
        Tuning tuning = TunedCrystalLocatorItem.getTuning(tunings, stack);
        return tuning != null ? tuning.getColor() : Tuning.UNTUNED.getColor();
    }

    private MoreGeodesNeoForgeClient() {
    }
}
