package com.github.thedeathlycow.moregeodes.item;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.TestBootstrap;
import com.github.thedeathlycow.moregeodes.entity.EchoDisplay;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import com.github.thedeathlycow.moregeodes.item.tuning.Tuning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocatorItemTest {
    @BeforeAll
    static void setUp() throws Exception {
        TestBootstrap.ensureBootstrapped();
    }

    private static CrystalLocatorItem crystalLocator() {
        return (CrystalLocatorItem) ModItems.CRYSTAL_LOCATOR;
    }

    private static TunedCrystalLocatorItem tunedLocator() {
        return (TunedCrystalLocatorItem) ModItems.TUNED_CRYSTAL_LOCATOR;
    }

    private static EchoLocatorItem echoLocator() {
        return (EchoLocatorItem) ModItems.ECHO_LOCATOR;
    }

    private static ServerLevel serverLevel() {
        ServerLevel level = mock(ServerLevel.class);
        when(level.isClientSide()).thenReturn(false);
        when(level.holderLookup(Registries.BLOCK)).thenReturn(mock(HolderLookup.RegistryLookup.class));
        return level;
    }

    private static Player player() {
        Player player = mock(Player.class);
        when(player.getCooldowns()).thenReturn(mock(ItemCooldowns.class));
        return player;
    }

    @Test
    void locatorConstantsMatchThePlan() {
        assertEquals(48, CrystalLocatorItem.CRYSTAL_LOCATOR_RANGE);
        assertEquals(20, CrystalLocatorItem.CRYSTAL_LOCATOR_COOL_DOWN);
        assertEquals(6, EchoLocatorItem.BLOCK_RANGE);
        assertEquals(80, EchoLocatorItem.ECHO_LOCATOR_COOL_DOWN);
        assertEquals(16, ModItems.CRYSTAL_LOCATOR.getMaxDamage());
        assertEquals(32, ModItems.ECHO_LOCATOR.getMaxDamage());
    }

    @Test
    void untunedLocatorUsePingsNothingButStartsCooldownAndPlaysSound() {
        ServerLevel level = serverLevel();
        Player user = player();
        ItemStack stack = new ItemStack(ModItems.CRYSTAL_LOCATOR);
        when(user.getItemInHand(any())).thenReturn(stack);
        when(user.blockPosition()).thenReturn(BlockPos.ZERO);

        InteractionResultHolder<ItemStack> result = crystalLocator().use(level, user, InteractionHand.MAIN_HAND);

        assertTrue(result.getResult().consumesAction(), "a server-side use must succeed");
        verify(level, never()).addFreshEntity(any());
        verify(user).playSound(any(net.minecraft.sounds.SoundEvent.class), anyFloat(), anyFloat());
        assertEquals(0, stack.getDamageValue());
    }

    @Test
    void tunedLocatorWithoutTuningNbtIsNotTuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        RegistryAccess access = mock(RegistryAccess.class);
        assertFalse(tunedLocator().isTuned(stack, access));
        assertFalse(tunedLocator().isTuned(stack, null));
    }

    @Test
    void tunedLocatorGetTuningReturnsNullForUnknownTuningId() {
        ItemStack stack = TunedLocatorData.makeTunedLocatorStack("geodes:not_a_tuning");
        RegistryAccess access = mock(RegistryAccess.class);
        when(access.registryOrThrow(com.github.thedeathlycow.moregeodes.item.tuning.Tunings.REGISTRY_KEY))
                .thenReturn(mock(net.minecraft.core.Registry.class));
        assertNull(TunedCrystalLocatorItem.getTuning(access, stack));
    }

    @Test
    void tunedLocatorGetTuningReturnsNullWithoutNbt() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        RegistryAccess access = mock(RegistryAccess.class);
        assertNull(TunedCrystalLocatorItem.getTuning(access, stack));
    }

    @Test
    void echoLocatorIsAlwaysTunedAndDetectsOnlyTaggedBlocks() {
        ServerLevel level = serverLevel();
        ItemStack stack = new ItemStack(ModItems.ECHO_LOCATOR);
        assertTrue(echoLocator().isTuned(stack, null));

        BlockPos orePos = BlockPos.ZERO;
        when(level.getBlockState(orePos)).thenReturn(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        assertFalse(echoLocator().isPingableCrystal(stack, level, orePos));
    }

    @Test
    void echoLocatorUseIgnoresUntaggedSurroundings() {
        ServerLevel level = serverLevel();
        Player user = player();
        ItemStack stack = new ItemStack(ModItems.ECHO_LOCATOR);
        when(user.getItemInHand(any())).thenReturn(stack);
        when(user.blockPosition()).thenReturn(BlockPos.ZERO);
        when(level.getBlockState(any())).thenReturn(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        InteractionResultHolder<ItemStack> result = echoLocator().use(level, user, InteractionHand.MAIN_HAND);

        assertTrue(result.getResult().consumesAction());
        verify(level, never()).addFreshEntity(any());
        assertEquals(0, stack.getDamageValue());
    }

    @Test
    void untunedLocatorTooltipShowsTheUntunedDescription() {
        List<net.minecraft.network.chat.Component> tooltip = new ArrayList<>();
        crystalLocator().appendHoverText(new ItemStack(ModItems.CRYSTAL_LOCATOR), null, tooltip,
                net.minecraft.world.item.TooltipFlag.Default.NORMAL);
        assertFalse(tooltip.isEmpty());
        assertEquals(Tuning.UNTUNED.getDescription().getString(), tooltip.get(0).getString());
    }

    @Test
    void tunedLocatorTooltipWithoutLevelShowsNothingExtra() {
        List<net.minecraft.network.chat.Component> tooltip = new ArrayList<>();
        tunedLocator().appendHoverText(TunedLocatorData.makeTunedLocatorStack("geodes:amethyst"), null, tooltip,
                net.minecraft.world.item.TooltipFlag.Default.NORMAL);
        assertEquals(1, tooltip.size());
        assertEquals(Tuning.UNTUNED.getDescription().getString(), tooltip.get(0).getString());
    }

    @Test
    void echoDisplayTypeIsRegisteredForThePing() {
        assertNotNull(GeodesEntityTypes.ECHO_DISPLAY);
        assertEquals(new ResourceLocation(MoreGeodes.MODID, "echo_display"),
                BuiltInRegistries.ENTITY_TYPE.getKey(GeodesEntityTypes.ECHO_DISPLAY));
    }
}
