package com.github.thedeathlycow.moregeodes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoreGeodesTest {
    @AfterEach
    void resetChecker() {
        MoreGeodes.setModLoadedChecker(id -> false);
    }

    @Test
    void nothingIsLoadedByDefault() {
        assertFalse(MoreGeodes.isCarpetLoaded());
        assertFalse(MoreGeodes.isAE2Loaded());
        assertFalse(MoreGeodes.isSpectrumLoaded());
        assertFalse(MoreGeodes.isMovableAmethystEnabled());
    }

    @Test
    void injectedCheckerDrivesTheCompatChecks() {
        MoreGeodes.setModLoadedChecker(id -> id.equals("spectrum") || id.equals("ae2"));
        assertTrue(MoreGeodes.isSpectrumLoaded());
        assertTrue(MoreGeodes.isAE2Loaded());
        assertFalse(MoreGeodes.isCarpetLoaded());
    }

    @Test
    void movableAmethystIsDisabledWithoutCarpet() {
        MoreGeodes.setModLoadedChecker(id -> false);
        assertFalse(MoreGeodes.isMovableAmethystEnabled(),
                "without Carpet the movable-amethyst compat must be off");
    }

    @Test
    void movableAmethystDegradesToDisabledWhenCarpetClassIsMissing() {
        MoreGeodes.setModLoadedChecker(id -> id.equals("carpet"));
        assertFalse(MoreGeodes.isMovableAmethystEnabled(),
                "a missing Carpet class must degrade to movable-amethyst disabled");
    }
}
