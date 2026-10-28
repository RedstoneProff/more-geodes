package com.github.thedeathlycow.moregeodes.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TunedLocatorDataTest {
    @BeforeAll
    static void bootstrap() {
        com.github.thedeathlycow.moregeodes.TestBootstrap.registerModContent(
                java.nio.file.Path.of("build", "test-config-locator-data"));
    }

    @Test
    void stackWithoutTagIsUntuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void stackWithEmptyTagIsUntuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        stack.setTag(new CompoundTag());
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void tuningKeyOfWrongTypeIsUntuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TunedLocatorData.TUNING_NBT_KEY, "garbage");
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void tuningCompoundWithoutIdIsUntuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        stack.getOrCreateTag().put(TunedLocatorData.TUNING_NBT_KEY, new CompoundTag());
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void idOfWrongTypeIsUntuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        CompoundTag tuning = new CompoundTag();
        tuning.putInt(TunedLocatorData.TUNING_ID_NBT_KEY, 5);
        stack.getOrCreateTag().put(TunedLocatorData.TUNING_NBT_KEY, tuning);
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void emptyIdIsUntuned() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        CompoundTag tuning = new CompoundTag();
        tuning.putString(TunedLocatorData.TUNING_ID_NBT_KEY, "");
        stack.getOrCreateTag().put(TunedLocatorData.TUNING_NBT_KEY, tuning);
        assertNull(TunedLocatorData.getTuningId(stack));
    }

    @Test
    void validIdRoundTrips() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        CompoundTag tuning = new CompoundTag();
        tuning.putString(TunedLocatorData.TUNING_ID_NBT_KEY, "geodes:spectrum/topaz");
        stack.getOrCreateTag().put(TunedLocatorData.TUNING_NBT_KEY, tuning);

        assertEquals(new ResourceLocation("geodes", "spectrum/topaz"), TunedLocatorData.getTuningId(stack));
    }

    @Test
    void makeTunedLocatorStackWritesTheUpstreamNbtLayout() {
        ItemStack stack = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        assertTrue(stack.getTag().contains(TunedLocatorData.TUNING_NBT_KEY, CompoundTag.TAG_COMPOUND),
                "the tuning key must be a compound (upstream 1.20.1 layout)");
        assertEquals("geodes:amethyst",
                stack.getTag().getCompound(TunedLocatorData.TUNING_NBT_KEY).getString(TunedLocatorData.TUNING_ID_NBT_KEY));
    }

    @Test
    void syntacticallyInvalidIdThrowsLikeUpstream() {
        ItemStack stack = new ItemStack(ModItems.TUNED_CRYSTAL_LOCATOR);
        CompoundTag tuning = new CompoundTag();
        tuning.putString(TunedLocatorData.TUNING_ID_NBT_KEY, "not a resource location");
        stack.getOrCreateTag().put(TunedLocatorData.TUNING_NBT_KEY, tuning);

        assertThrows(Exception.class, () -> TunedLocatorData.getTuningId(stack));
    }
}
