package com.github.thedeathlycow.moregeodes.testmod;

import java.nio.file.Paths;

import net.minecraft.gametest.framework.StructureUtils;
import net.neoforged.fml.common.Mod;

@Mod("geodes_gametest")
public class MoreGeodesGameTestMod {
	public MoreGeodesGameTestMod() {
		String structures = System.getProperty("geodes.gametest.structures");
		if (structures != null && !structures.isEmpty()) {
			StructureUtils.testStructuresSourceDir = Paths.get(structures);
		}
	}
}
