package com.github.thedeathlycow.moregeodes.sounds;

import com.github.thedeathlycow.moregeodes.TestBootstrap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoundGroupsTest {
    @BeforeAll
    static void boot() {
        TestBootstrap.bootstrap();
    }

    private static List<Object> staticFieldsOf(Class<?> type) throws IllegalAccessException {
        List<Object> values = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                values.add(field.get(null));
            }
        }
        return values;
    }

    @Test
    void theModsSoundEventsAreRegistered() {
        long registered = java.util.stream.StreamSupport
                .stream(BuiltInRegistries.SOUND_EVENT.spliterator(), false)
                .filter(event -> BuiltInRegistries.SOUND_EVENT.getKey(event).getNamespace().equals("geodes"))
                .count();
        assertTrue(registered >= 20, "expected the mod's crystal sound events in the registry, found " + registered);
    }

    @Test
    void blockSoundTypesWireAllFiveSounds() {
        for (SoundType type : List.of(
                ModBlockSoundGroups.EMERALD_GEODE, ModBlockSoundGroups.EMERALD_CLUSTER,
                ModBlockSoundGroups.LARGE_EMERALD_BUD, ModBlockSoundGroups.MEDIUM_EMERALD_BUD,
                ModBlockSoundGroups.SMALL_EMERALD_BUD,
                ModBlockSoundGroups.QUARTZ_GEODE, ModBlockSoundGroups.QUARTZ_CLUSTER,
                ModBlockSoundGroups.LARGE_QUARTZ_BUD, ModBlockSoundGroups.MEDIUM_QUARTZ_BUD,
                ModBlockSoundGroups.SMALL_QUARTZ_BUD,
                ModBlockSoundGroups.DIAMOND_GEODE, ModBlockSoundGroups.DIAMOND_CLUSTER,
                ModBlockSoundGroups.LAPIS_CRYSTAL_BLOCK, ModBlockSoundGroups.LAPIS_CLUSTER,
                ModBlockSoundGroups.SMALL_LAPIS_BUD, ModBlockSoundGroups.MEDIUM_LAPIS_BUD,
                ModBlockSoundGroups.LARGE_LAPIS_BUD,
                ModBlockSoundGroups.GYPSUM_CRYSTAL_BLOCK,
                ModBlockSoundGroups.ECHO_BLOCK, ModBlockSoundGroups.ECHO_CLUSTER,
                ModBlockSoundGroups.SMALL_ECHO_BUD, ModBlockSoundGroups.MEDIUM_ECHO_BUD,
                ModBlockSoundGroups.LARGE_ECHO_BUD, ModBlockSoundGroups.ECHO_LOCATOR
        )) {
            assertNotNull(type.getBreakSound(), type + " break sound");
            assertNotNull(type.getStepSound(), type + " step sound");
            assertNotNull(type.getPlaceSound(), type + " place sound");
            assertNotNull(type.getHitSound(), type + " hit sound");
            assertNotNull(type.getFallSound(), type + " fall sound");
        }
    }

    @Test
    void crystalBlockSoundGroupsAreDistinctRecords() {
        CrystalBlockSoundGroup emerald = CrystalBlockSoundGroup.EMERALD_GEODE;
        CrystalBlockSoundGroup quartz = CrystalBlockSoundGroup.QUARTZ_GEODE;
        assertFalse(emerald.equals(quartz), "emerald and quartz groups must be distinct");
        assertEquals(emerald.hitSound(), quartz.hitSound());
        assertEquals(false, emerald.chimeSound().equals(quartz.chimeSound()));
    }
}
