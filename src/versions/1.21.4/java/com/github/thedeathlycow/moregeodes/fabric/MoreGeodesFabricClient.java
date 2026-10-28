package com.github.thedeathlycow.moregeodes.fabric;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.client.GeodesCutouts;
import com.github.thedeathlycow.moregeodes.client.GeodesEntityRenderers;
import net.fabricmc.api.ClientModInitializer;

public class MoreGeodesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoreGeodes.LOGGER.info("Beginning More Geodes client initialization");
        GeodesCutouts.registerCutouts();
        GeodesEntityRenderers.registerEntityRenderers();

        MoreGeodes.LOGGER.info("More Geodes client initialized!");
    }
}
