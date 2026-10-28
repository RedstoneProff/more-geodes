package com.github.thedeathlycow.moregeodes.entity;

import com.github.thedeathlycow.moregeodes.sounds.GeodesSoundEvents;
import com.github.thedeathlycow.moregeodes.world.GeodesGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.Level;

public class EchoDisplay extends Display.BlockDisplay {

    private static final String TTL_NBT_KEY = "time_to_live";
    private static final String GLOW_TIME_NBT_KEY = "glow_time";
    private static final String IS_PINGED_NBT_KEY = "is_pinged";

    private static final int BASE_TTL = 30;
    private static final int BASE_GLOW_TIME = Integer.MIN_VALUE;
    private static final EntityDataAccessor<Integer> TIME_TO_LIVE = SynchedEntityData.defineId(
            EchoDisplay.class,
            EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Integer> GLOW_TIME = SynchedEntityData.defineId(
            EchoDisplay.class,
            EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Boolean> IS_PINGED = SynchedEntityData.defineId(
            EchoDisplay.class,
            EntityDataSerializers.BOOLEAN
    );

    public EchoDisplay(EntityType<? extends Display.BlockDisplay> entityType, Level world) {
        super(entityType, world);
        this.setInvisible(true);
        this.setInvulnerable(true);
    }

    public void setGlowDelay(int delay) {
        int ttl = this.getTimeToLive();
        this.setGlowTime(ttl);
        this.setTimeToLive(ttl + delay);
    }

    @Override
    public void tick() {
        super.tick();

        int timeToLive = this.getTimeToLive();
        timeToLive--;
        this.setTimeToLive(timeToLive);

        if (timeToLive <= this.getGlowTime() && !this.isPinged()) {
            this.ping();
        }

        if (timeToLive <= 0) {
            this.discard();
        }

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setTimeToLive(nbt.getInt(TTL_NBT_KEY));
        this.setGlowTime(nbt.getInt(GLOW_TIME_NBT_KEY));
        this.setPinged(nbt.getBoolean(GLOW_TIME_NBT_KEY));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt(TTL_NBT_KEY, this.getTimeToLive());
        nbt.putInt(GLOW_TIME_NBT_KEY, this.getGlowTime());
        nbt.putBoolean(IS_PINGED_NBT_KEY, this.isPinged());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TIME_TO_LIVE, BASE_TTL);
        builder.define(GLOW_TIME, BASE_GLOW_TIME);
        builder.define(IS_PINGED, false);
    }

    private void ping() {
        this.setInvisible(false);
        this.setPinged(true);
        this.setGlowingTag(true);
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        world.gameEvent(this, GeodesGameEvents.CRYSTAL_RESONATE, pos);
        world.playSound(
                null,
                pos,
                GeodesSoundEvents.BLOCK_ECHO_LOCATOR_RESONATE, SoundSource.BLOCKS,
                3.0f, 1.0f
        );
    }

    private int getGlowTime() {
        return this.entityData.get(GLOW_TIME);
    }

    private void setGlowTime(int value) {
        this.entityData.set(GLOW_TIME, value);
    }

    private int getTimeToLive() {
        return this.entityData.get(TIME_TO_LIVE);
    }

    private void setTimeToLive(int value) {
        this.entityData.set(TIME_TO_LIVE, value);
    }

    private boolean isPinged() {
        return this.entityData.get(IS_PINGED);
    }

    private void setPinged(boolean value) {
        this.entityData.set(IS_PINGED, value);
    }
}
