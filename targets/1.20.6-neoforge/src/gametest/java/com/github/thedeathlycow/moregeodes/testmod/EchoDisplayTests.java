package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.entity.EchoDisplay;
import com.github.thedeathlycow.moregeodes.entity.GeodesEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("geodes")
@PrefixGameTestTemplate(false)
public class EchoDisplayTests {
    private static final BlockPos POS = MoreGeodesTestHelper.CENTER;

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_echo_display_nbt_round_trip(GameTestHelper context) {
        EchoDisplay display = GeodesEntityTypes.ECHO_DISPLAY.create(context.getLevel());
        context.assertTrue(display != null, "echo display entity could not be created");
        display.setPos(POS.getX(), POS.getY(), POS.getZ());
        display.setGlowDelay(5);

        CompoundTag saved = display.saveWithoutId(new CompoundTag());
        context.assertTrue(
                saved.contains("time_to_live") && saved.contains("glow_time") && saved.contains("is_pinged"),
                "echo display must serialize time_to_live, glow_time and is_pinged"
        );
        int ttl = saved.getInt("time_to_live");
        int glowTime = saved.getInt("glow_time");
        context.assertTrue(ttl == 35, "time to live must be base 30 + delay 5, got " + ttl);
        context.assertTrue(glowTime == 30, "glow time must be the base 30, got " + glowTime);

        EchoDisplay loaded = GeodesEntityTypes.ECHO_DISPLAY.create(context.getLevel());
        context.assertTrue(loaded != null, "echo display entity could not be created");
        loaded.load(saved);

        CompoundTag savedAgain = loaded.saveWithoutId(new CompoundTag());
        context.assertTrue(
                savedAgain.getInt("time_to_live") == ttl,
                "time to live must survive a save/load round trip"
        );
        context.assertTrue(
                savedAgain.getInt("glow_time") == glowTime,
                "glow time must survive a save/load round trip"
        );
        context.assertTrue(
                savedAgain.getBoolean("is_pinged") == (glowTime != 0),
                "on load the pinged state must be read from the glow_time key (upstream parity)"
        );
        context.succeed();
    }

    @GameTest(template = "empty15", templateNamespace = "geodes-test")
    public void F_010_echo_display_discards_after_time_to_live(GameTestHelper context) {
        EchoDisplay display = GeodesEntityTypes.ECHO_DISPLAY.create(context.getLevel());
        context.assertTrue(display != null, "echo display entity could not be created");
        display.setPos(POS.getX(), POS.getY(), POS.getZ());
        display.setGlowDelay(5);
        context.getLevel().addFreshEntity(display);

        context.startSequence()
                .thenIdle(34)
                .thenExecute(() -> context.assertTrue(display.isAlive(), "echo display must live for its time to live"))
                .thenIdle(2)
                .thenExecute(() -> context.assertFalse(display.isAlive(), "echo display must discard itself after its time to live"))
                .thenSucceed();
    }
}
