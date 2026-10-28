package com.github.thedeathlycow.moregeodes.item.tuning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;

import java.util.Optional;

public class Tuning {
    private static final LocationPredicate ANY = new LocationPredicate(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
    );

    public static final Tuning UNTUNED = new Tuning(
            FastColor.ARGB32.color(0xff, 0x44, 0x44, 0x44),
            Component.translatable("geodes.tunings.untuned")
                    .withStyle(ChatFormatting.GRAY),
            ANY
    );

    private static final String COLOR_KEY = "color";
    private static final String DESCRIPTION_KEY = "description";
    private static final String PREDICATE_KEY = "tuned_to";

    public static final Codec<Tuning> DATAPACK_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf(COLOR_KEY).forGetter(Tuning::getColor),
                    ComponentSerialization.CODEC.fieldOf(DESCRIPTION_KEY).forGetter(Tuning::getDescription),
                    LocationPredicate.CODEC.fieldOf(PREDICATE_KEY).forGetter(Tuning::getIsTunedToBlock)
            ).apply(instance, Tuning::new)
    );

    public static final Codec<Tuning> NETWORK_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf(COLOR_KEY).forGetter(Tuning::getColor),
                    ComponentSerialization.CODEC.fieldOf(DESCRIPTION_KEY).forGetter(Tuning::getDescription)
            ).apply(instance, Tuning::new)
    );

    private final int color;
    private final Component description;
    private final LocationPredicate isTunedToBlock;

    public Tuning(int color, Component description, LocationPredicate isTunedToBlock) {
        this.color = color;
        this.description = description;
        this.isTunedToBlock = isTunedToBlock;
    }

    private Tuning(int color, Component description) {
        this(color, description, ANY);
    }

    public boolean test(ServerLevel world, BlockPos pos) {
        if (this.isTunedToBlock == null || this.isTunedToBlock.equals(ANY)) {
            return false;
        }

        return this.isTunedToBlock.matches(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public Component getDescription() {
        return description;
    }

    public int getColor() {
        return this.color;
    }

    public LocationPredicate getIsTunedToBlock() {
        return isTunedToBlock;
    }
}
