package com.github.thedeathlycow.moregeodes.blocks;

import net.minecraft.world.level.material.PushReaction;

public final class GeodesPushReactions {
    public static PushReaction destroy() {
        return PushReaction.POPPED;
    }

    public static PushReaction normal() {
        return PushReaction.PUSH_PULL;
    }

    private GeodesPushReactions() {
    }
}
