package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.entity.EchoDisplay;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import com.github.thedeathlycow.moregeodes.sounds.GeodesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrystalLocatorItem extends Item {
    public static final int CRYSTAL_LOCATOR_RANGE = 48;
    public static final int CRYSTAL_LOCATOR_COOL_DOWN = 20;
    private final int range;
    private final int coolDown;

    public CrystalLocatorItem(Properties settings, int range, int coolDown) {
        super(settings);
        this.range = range;
        this.coolDown = coolDown;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!world.isClientSide) {
            int blocksPinged = this.activate((ServerLevel) world, user.blockPosition(), itemStack);

            if (blocksPinged > 0) {
                itemStack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(hand));
            }

            ItemCooldowns cooldownManager = user.getCooldowns();
            this.setCooldown(cooldownManager, this.coolDown);

            user.playSound(GeodesSoundEvents.BLOCK_ECHO_LOCATOR_USE, 1.0f, 1.0f);
        }

        return world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (!this.isTuned(stack, context.registries())) {
            tooltip.add(Tuning.UNTUNED.getDescription().copy().withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, type);
    }

    public boolean isTuned(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        return false;
    }

    protected boolean isPingableCrystal(ItemStack locatorStack, ServerLevel world, BlockPos pos) {
        return false;
    }

    protected void setCooldown(ItemCooldowns cooldownManager, int ticks) {
        // cooldowns are keyed by the item id, so both locators have to be set to share one cooldown
        cooldownManager.addCooldown(BuiltInRegistries.ITEM.getKey(ModItems.CRYSTAL_LOCATOR), ticks);
        cooldownManager.addCooldown(BuiltInRegistries.ITEM.getKey(ModItems.TUNED_CRYSTAL_LOCATOR), ticks);
    }

    private int activate(ServerLevel world, BlockPos origin, ItemStack itemStack) {
        var scanVector = new Vec3i(range, range, range);
        BlockPos from = origin.subtract(scanVector);
        BlockPos to = origin.offset(scanVector);
        final int rangeSquared = this.range * this.range;
        int blocksPinged = 0;

        for (BlockPos pos : BlockPos.betweenClosed(from, to)) {
            double distanceToPos = origin.distSqr(pos) * 3;
            if (distanceToPos <= rangeSquared) {
                blocksPinged += this.tryPing(world, pos, itemStack, distanceToPos);
            }
        }

        return blocksPinged;
    }

    private int tryPing(ServerLevel world, BlockPos pos, ItemStack itemStack, double distanceToPosSquared) {
        if (this.isPingableCrystal(itemStack, world, pos)) {
            int delay = Mth.ceil(Math.sqrt(distanceToPosSquared));
            highlightCrystal(
                    world,
                    pos,
                    world.getBlockState(pos),
                    delay
            );
            return 1;
        }
        return 0;
    }

    private static void highlightCrystal(
            Level world,
            BlockPos pos,
            BlockState state,
            int delay
    ) {
        EchoDisplay blockDisplay = GeodesEntityTypes.ECHO_DISPLAY.create(world, EntitySpawnReason.COMMAND);

        if (blockDisplay == null) {
            return;
        }

        blockDisplay.setBlockState(state);
        blockDisplay.setPos(pos.getX(), pos.getY(), pos.getZ());
        blockDisplay.setGlowDelay(delay);

        world.addFreshEntity(blockDisplay);
    }
}
