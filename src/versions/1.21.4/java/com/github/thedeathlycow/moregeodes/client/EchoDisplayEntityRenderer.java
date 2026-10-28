package com.github.thedeathlycow.moregeodes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class EchoDisplayEntityRenderer extends DisplayRenderer.BlockDisplayRenderer {
    public EchoDisplayEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderInner(
            BlockDisplayEntityRenderState state,
            PoseStack matrixStack,
            MultiBufferSource vertexConsumerProvider,
            int brightness, float lerpProgress
    ) {
        if (!state.isInvisible) {
            super.renderInner(
                    state,
                    matrixStack,
                    // change the provider to only render outlines, and not render the normal state texture
                    layer -> {
                        return vertexConsumerProvider.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS));
                    },
                    brightness,
                    lerpProgress
            );
        }
    }
}
