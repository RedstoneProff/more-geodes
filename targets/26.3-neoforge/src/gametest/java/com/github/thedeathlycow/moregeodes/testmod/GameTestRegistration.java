package com.github.thedeathlycow.moregeodes.testmod;

import java.util.List;

import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
@EventBusSubscriber(modid = "geodes_gametest")
public class GameTestRegistration {
	private static final String NAMESPACE = "geodes_gametest";

	@SubscribeEvent
	public static void registerTestFunctions(RegisterEvent event) {
		event.register(net.minecraft.core.registries.Registries.TEST_FUNCTION, helper -> {
			helper.register(
					Identifier.fromNamespaceAndPath(NAMESPACE, "large_crystal_breaks_base_when_broke_by_piston"),
					MoreGeodesGameTests::large_crystal_breaks_base_when_broke_by_piston);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"budding_emerald_grows_a_small_bud_on_an_adjacent_face"),
					BuddingGrowthTests::budding_emerald_grows_a_small_bud_on_an_adjacent_face);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"bud_grows_through_the_full_chain_and_stops_at_cluster"),
					BuddingGrowthTests::bud_grows_through_the_full_chain_and_stops_at_cluster);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"cluster_is_terminal_and_budding_block_survives"),
					BuddingGrowthTests::cluster_is_terminal_and_budding_block_survives);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE, "budding_gypsum_grows_only_upward"),
					BuddingGrowthTests::budding_gypsum_grows_only_upward);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"emerald_cluster_drops_emeralds_without_silk_touch"),
					ClusterHarvestTests::emerald_cluster_drops_emeralds_without_silk_touch);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"emerald_cluster_drops_itself_with_silk_touch"),
					ClusterHarvestTests::emerald_cluster_drops_itself_with_silk_touch);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"breaking_rose_upper_half_removes_lower_anchor"),
					ClusterHarvestTests::breaking_rose_upper_half_removes_lower_anchor);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"rose_placement_state_faces_the_clicked_side"),
					ClusterHarvestTests::rose_placement_state_faces_the_clicked_side);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"small_quartz_bud_oscillates_at_half_hertz"),
					QuartzOscillatorTests::small_quartz_bud_oscillates_at_half_hertz);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"quartz_cluster_oscillates_at_ten_hertz"),
					QuartzOscillatorTests::quartz_cluster_oscillates_at_ten_hertz);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"removing_power_resets_the_oscillator_to_off"),
					QuartzOscillatorTests::removing_power_resets_the_oscillator_to_off);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"tuned_locator_pings_a_matching_crystal"),
					LocatorTests::tuned_locator_pings_a_matching_crystal);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE, "untuned_locator_pings_nothing"),
					LocatorTests::untuned_locator_pings_nothing);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE, "wrongly_tuned_locator_pings_nothing"),
					LocatorTests::wrongly_tuned_locator_pings_nothing);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"echo_locator_detects_ore_through_stone_and_takes_damage"),
					LocatorTests::echo_locator_detects_ore_through_stone_and_takes_damage);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"echo_locator_ignores_blocks_outside_its_range"),
					LocatorTests::echo_locator_ignores_blocks_outside_its_range);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"echo_display_pings_and_becomes_visible_after_delay"),
					LocatorTests::echo_display_pings_and_becomes_visible_after_delay);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE, "echo_display_save_load_round_trip"),
					LocatorTests::echo_display_save_load_round_trip);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"tuned_locator_tuning_lookup_and_tooltip"),
					LocatorTests::tuned_locator_tuning_lookup_and_tooltip);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"unpowered_quartz_bud_never_oscillates"),
					EdgeCaseTests::unpowered_quartz_bud_never_oscillates);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"echo_locator_cooldown_is_per_player"),
					EdgeCaseTests::echo_locator_cooldown_is_per_player);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"emerald_cluster_drops_emeralds_with_bare_hand"),
					EdgeCaseTests::emerald_cluster_drops_emeralds_with_bare_hand);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"crystal_tuning_recipe_crafts_a_tuned_locator"),
					EdgeCaseTests::crystal_tuning_recipe_crafts_a_tuned_locator);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"geode_worldgen_features_are_registered"),
					EdgeCaseTests::geode_worldgen_features_are_registered);
			helper.register(Identifier.fromNamespaceAndPath(NAMESPACE,
					"config_biome_modifier_respects_the_config"),
					EdgeCaseTests::config_biome_modifier_respects_the_config);
		});
	}

	@SubscribeEvent
	public static void registerTests(RegisterGameTestsEvent event) {
		Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
				Identifier.fromNamespaceAndPath(NAMESPACE, "default"),
				new TestEnvironmentDefinition.AllOf(List.of()));

		registerTest(event, environment,
				"large_crystal_breaks_base_when_broke_by_piston",
				"geodes-test:large_crystal_break_tests.top.piston",
				MoreGeodesGameTests::large_crystal_breaks_base_when_broke_by_piston);

		String emptyRoom = "geodes-test:budding_growth_tests.empty5x4x5";
		registerTest(event, environment, "budding_emerald_grows_a_small_bud_on_an_adjacent_face",
				emptyRoom, BuddingGrowthTests::budding_emerald_grows_a_small_bud_on_an_adjacent_face);
		registerTest(event, environment, "bud_grows_through_the_full_chain_and_stops_at_cluster",
				emptyRoom, BuddingGrowthTests::bud_grows_through_the_full_chain_and_stops_at_cluster);
		registerTest(event, environment, "cluster_is_terminal_and_budding_block_survives",
				emptyRoom, BuddingGrowthTests::cluster_is_terminal_and_budding_block_survives);
		registerTest(event, environment, "budding_gypsum_grows_only_upward",
				emptyRoom, BuddingGrowthTests::budding_gypsum_grows_only_upward);
		registerTest(event, environment, "emerald_cluster_drops_emeralds_without_silk_touch",
				emptyRoom, ClusterHarvestTests::emerald_cluster_drops_emeralds_without_silk_touch);
		registerTest(event, environment, "emerald_cluster_drops_itself_with_silk_touch",
				emptyRoom, ClusterHarvestTests::emerald_cluster_drops_itself_with_silk_touch);
		registerTest(event, environment, "breaking_rose_upper_half_removes_lower_anchor",
				emptyRoom, ClusterHarvestTests::breaking_rose_upper_half_removes_lower_anchor);
		registerTest(event, environment, "rose_placement_state_faces_the_clicked_side",
				emptyRoom, ClusterHarvestTests::rose_placement_state_faces_the_clicked_side);
		registerTest(event, environment, "small_quartz_bud_oscillates_at_half_hertz",
				emptyRoom, QuartzOscillatorTests::small_quartz_bud_oscillates_at_half_hertz);
		registerTest(event, environment, "quartz_cluster_oscillates_at_ten_hertz",
				emptyRoom, QuartzOscillatorTests::quartz_cluster_oscillates_at_ten_hertz);
		registerTest(event, environment, "removing_power_resets_the_oscillator_to_off",
				emptyRoom, QuartzOscillatorTests::removing_power_resets_the_oscillator_to_off);
		registerTest(event, environment, "tuned_locator_pings_a_matching_crystal",
				emptyRoom, LocatorTests::tuned_locator_pings_a_matching_crystal);
		registerTest(event, environment, "untuned_locator_pings_nothing",
				emptyRoom, LocatorTests::untuned_locator_pings_nothing);
		registerTest(event, environment, "wrongly_tuned_locator_pings_nothing",
				emptyRoom, LocatorTests::wrongly_tuned_locator_pings_nothing);
		registerTest(event, environment, "echo_locator_detects_ore_through_stone_and_takes_damage",
				emptyRoom, LocatorTests::echo_locator_detects_ore_through_stone_and_takes_damage);
		registerTest(event, environment, "echo_locator_ignores_blocks_outside_its_range",
				emptyRoom, LocatorTests::echo_locator_ignores_blocks_outside_its_range);
		registerTest(event, environment, "echo_display_pings_and_becomes_visible_after_delay",
				emptyRoom, LocatorTests::echo_display_pings_and_becomes_visible_after_delay);
		registerTest(event, environment, "echo_display_save_load_round_trip",
				emptyRoom, LocatorTests::echo_display_save_load_round_trip);
		registerTest(event, environment, "tuned_locator_tuning_lookup_and_tooltip",
				emptyRoom, LocatorTests::tuned_locator_tuning_lookup_and_tooltip);

		registerTest(event, environment, "unpowered_quartz_bud_never_oscillates",
				emptyRoom, EdgeCaseTests::unpowered_quartz_bud_never_oscillates);
		registerTest(event, environment, "echo_locator_cooldown_is_per_player",
				emptyRoom, EdgeCaseTests::echo_locator_cooldown_is_per_player);
		registerTest(event, environment, "emerald_cluster_drops_emeralds_with_bare_hand",
				emptyRoom, EdgeCaseTests::emerald_cluster_drops_emeralds_with_bare_hand);
		registerTest(event, environment, "crystal_tuning_recipe_crafts_a_tuned_locator",
				emptyRoom, EdgeCaseTests::crystal_tuning_recipe_crafts_a_tuned_locator);
		registerTest(event, environment, "geode_worldgen_features_are_registered",
				emptyRoom, EdgeCaseTests::geode_worldgen_features_are_registered);
		registerTest(event, environment, "config_biome_modifier_respects_the_config",
				emptyRoom, EdgeCaseTests::config_biome_modifier_respects_the_config);

		GlobalTestReporter.replaceWith(new GametestXmlReporter());
	}

	private static void registerTest(
			RegisterGameTestsEvent event,
			Holder<TestEnvironmentDefinition<?>> environment,
			String name,
			String structure,
			java.util.function.Consumer<GameTestHelper> function
	) {
		Identifier id = Identifier.fromNamespaceAndPath(NAMESPACE, name);
		TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
				environment,
				Identifier.parse(structure),
				100, 0, true, Rotation.NONE);
		event.registerTest(id, new FunctionGameTestInstance(
				net.minecraft.resources.ResourceKey.create(
						net.minecraft.core.registries.Registries.TEST_FUNCTION, id),
				data));
	}
}
