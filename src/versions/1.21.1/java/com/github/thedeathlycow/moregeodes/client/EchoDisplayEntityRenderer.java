package com.github.thedeathlycow.moregeodes.client;

import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import com.mojang.blaze3d.vertex.PoseStack;

public class EchoDisplayEntityRenderer extends DisplayRenderer.BlockDisplayRenderer {
    public EchoDisplayEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderInner(
            Display.BlockDisplay blockDisplayEntity,
            Display.BlockDisplay.BlockRenderState data,
            PoseStack matrixStack,
            MultiBufferSource vertexConsumerProvider,
            int brightness, float lerpProgress
    ) {
        if (!blockDisplayEntity.isInvisible()) {
            ResourceLocation texture = this.getTextureLocation(blockDisplayEntity);

            super.renderInner(
                    blockDisplayEntity,
                    data,
                    matrixStack,
                    // change the provider to only render outlines, and not render the normal state texture
                    layer -> {
                        return vertexConsumerProvider.getBuffer(RenderType.outline(texture));
                    },
                    brightness,
                    lerpProgress
            );
        }
    }
}
