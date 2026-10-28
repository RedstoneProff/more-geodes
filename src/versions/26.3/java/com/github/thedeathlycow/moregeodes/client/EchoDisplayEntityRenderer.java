package com.github.thedeathlycow.moregeodes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class EchoDisplayEntityRenderer extends DisplayRenderer.BlockDisplayRenderer {
    public EchoDisplayEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submitInner(
            BlockDisplayEntityRenderState state,
            PoseStack matrixStack,
            SubmitNodeCollector collector,
            int brightness, float lerpProgress
    ) {
        if (!state.isInvisible) {
            state.blockModel.submitOnlyOutline(
                    matrixStack,
                    collector,
                    brightness,
                    OverlayTexture.NO_OVERLAY,
                    state.outlineColor
            );
        }
    }
}
