package com.github.thedeathlycow.moregeodes.blocks;

import net.minecraft.world.level.material.PushReaction;

public final class GeodesPushReactions {
    public static PushReaction destroy() {
        return PushReaction.DESTROY;
    }

    public static PushReaction normal() {
        return PushReaction.NORMAL;
    }

    private GeodesPushReactions() {
    }
}
