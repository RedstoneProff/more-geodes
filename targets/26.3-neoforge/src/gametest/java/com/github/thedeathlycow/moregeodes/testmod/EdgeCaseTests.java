package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.blocks.CrystalClusterBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.blocks.QuartzClusterBlock;
import com.github.thedeathlycow.moregeodes.features.ModPlacedFeatures;
import com.github.thedeathlycow.moregeodes.item.ModItems;
import com.github.thedeathlycow.moregeodes.item.TunedLocatorData;
import com.github.thedeathlycow.moregeodes.item.recipe.GeodesRecipeSerializers;
import com.github.thedeathlycow.moregeodes.item.recipe.TuningRecipe;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EdgeCaseTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    public static void unpowered_quartz_bud_never_oscillates(GameTestHelper context) {
        context.setBlock(POS, ((QuartzClusterBlock) ModBlocks.SMALL_QUARTZ_BUD).defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP)
                .setValue(QuartzClusterBlock.POWERED, false));

        context.runAtTickTime(30, () -> context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, false));
        context.runAtTickTime(60, () -> {
            context.assertBlockProperty(POS, QuartzClusterBlock.POWERED, false);
            context.succeed();
        });
    }

    public static void echo_locator_cooldown_is_per_player(GameTestHelper context) {
        context.setBlock(POS.north(), Blocks.IRON_ORE);

        Player playerA = playerAt(context, POS);
        ItemStack locatorA = new ItemStack(ModItems.ECHO_LOCATOR);
        playerA.setItemInHand(InteractionHand.MAIN_HAND, locatorA);
        ModItems.ECHO_LOCATOR.use(context.getLevel(), playerA, InteractionHand.MAIN_HAND);
        context.assertTrue(locatorA.getDamageValue() == 1,
                "player A's locator must detect the ore and take 1 damage");
        context.assertTrue(playerA.getCooldowns().isOnCooldown(new ItemStack(ModItems.ECHO_LOCATOR)),
                "player A must be on cooldown after use");

        Player playerB = playerAt(context, POS);
        ItemStack locatorB = new ItemStack(ModItems.ECHO_LOCATOR);
        playerB.setItemInHand(InteractionHand.MAIN_HAND, locatorB);
        context.assertTrue(!playerB.getCooldowns().isOnCooldown(locatorB),
                "a fresh player must not inherit another player's item cooldown");
        ModItems.ECHO_LOCATOR.use(context.getLevel(), playerB, InteractionHand.MAIN_HAND);
        context.assertTrue(locatorB.getDamageValue() == 1,
                "player B's locator must detect the ore while player A is on cooldown");
        context.succeed();
    }

    public static void emerald_cluster_drops_emeralds_with_bare_hand(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CLUSTER.defaultBlockState()
                .setValue(CrystalClusterBlock.FACING, Direction.UP));

        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
        ModBlocks.EMERALD_CLUSTER.playerDestroy(
                context.getLevel(), player, context.absolutePos(POS),
                context.getBlockState(POS), null, ItemStack.EMPTY
        );
        context.setBlock(POS, Blocks.AIR);

        context.assertItemEntityCountIs(Items.EMERALD, POS, 2.0, 4);
        context.succeed();
    }

    public static void crystal_tuning_recipe_crafts_a_tuned_locator(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        RecipeManager recipes = level.recipeAccess();

        ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> recipeId = ResourceKey.create(
                Registries.RECIPE, Identifier.fromNamespaceAndPath("geodes", "tuning/amethyst_tuning"));
        RecipeHolder<?> holder = recipes.byKey(recipeId).orElse(null);
        context.assertTrue(holder != null, "the shipped amethyst tuning recipe must be loaded by the datapack");
        context.assertTrue(holder.value() instanceof TuningRecipe,
                "the crystal_tuning recipe must deserialize into a TuningRecipe, got " + holder.value().getClass());
        TuningRecipe recipe = (TuningRecipe) holder.value();

        context.assertTrue(recipe.getTuningId().equals(Identifier.parse("geodes:amethyst")),
                "the amethyst tuning recipe must target geodes:amethyst, got " + recipe.getTuningId());
        RecipeSerializer<?> serializer = recipe.getSerializer();
        context.assertTrue(serializer == GeodesRecipeSerializers.CRYSTAL_TUNING,
                "the recipe must report the geodes crystal_tuning serializer");

        CraftingInput input = CraftingInput.of(2, 1, List.of(
                new ItemStack(Items.AMETHYST_SHARD),
                new ItemStack(ModItems.CRYSTAL_LOCATOR)));
        context.assertTrue(recipe.matches(input, level),
                "the tuning recipe must match amethyst shard + crystal locator");
        ItemStack result = recipe.assemble(input);
        context.assertTrue(result.is(ModItems.TUNED_CRYSTAL_LOCATOR),
                "crafting must produce the tuned crystal locator, got " + result);
        context.assertTrue(Identifier.parse("geodes:amethyst").equals(result.get(TunedLocatorData.TUNING_COMPONENT)),
                "the crafted locator must carry the geodes:amethyst tuning component, got "
                        + result.get(TunedLocatorData.TUNING_COMPONENT));

        ItemStack tabStack = TunedLocatorData.makeTunedLocatorStack("geodes:amethyst");
        context.assertTrue(Identifier.parse("geodes:amethyst").equals(tabStack.get(TunedLocatorData.TUNING_COMPONENT)),
                "makeTunedLocatorStack must set the tuning component");

        CraftingInput wrong = CraftingInput.of(1, 1, List.of(new ItemStack(Items.AMETHYST_SHARD)));
        context.assertTrue(!recipe.matches(wrong, level),
                "the tuning recipe must not match with the locator ingredient missing");
        context.succeed();
    }

    public static void geode_worldgen_features_are_registered(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        RegistryAccess access = level.registryAccess();

        HolderLookup.RegistryLookup<PlacedFeature> placed = access.lookupOrThrow(Registries.PLACED_FEATURE);
        List<String> unconditional = List.of(
                "emerald_geode", "emerald_geode_extra", "quartz_geode", "diamond_geode",
                "echo_geode", "lapis_geode", "lapis_geode_extra", "gypsum_patch");
        for (String name : unconditional) {
            ResourceKey<PlacedFeature> key = ResourceKey.create(
                    Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("geodes", name));
            context.assertTrue(placed.get(key).isPresent(),
                    "placed feature geodes:" + name + " must be registered by the datapack");
        }

        assertConditionalFeature(context, placed, "certus_geode", MoreGeodes.isAE2Loaded());
        assertConditionalFeature(context, placed, "bismuth_geode", MoreGeodes.isSpectrumLoaded());

        HolderLookup.RegistryLookup<net.minecraft.world.level.levelgen.feature.Feature> configured =
                access.lookupOrThrow(Registries.FEATURE);
        List<String> configuredNames = List.of(
                "emerald_geode", "quartz_geode", "diamond_geode", "echo_geode",
                "lapis_geode", "gypsum_patch");
        for (String name : configuredNames) {
            ResourceKey<net.minecraft.world.level.levelgen.feature.Feature> key = ResourceKey.create(
                    Registries.FEATURE, Identifier.fromNamespaceAndPath("geodes", name));
            context.assertTrue(configured.get(key).isPresent(),
                    "configured feature geodes:" + name + " must be registered by the datapack");
        }
        assertConditionalConfigured(context, configured, "certus_geode", MoreGeodes.isAE2Loaded());
        assertConditionalConfigured(context, configured, "bismuth_geode", MoreGeodes.isSpectrumLoaded());

        HolderSet.Named<Biome> tag = access.lookupOrThrow(Registries.BIOME)
                .getOrThrow(TagKey.create(Registries.BIOME, Identifier.parse("geodes:has_emerald_geode")));
        context.assertTrue(tag.stream().anyMatch(holder -> holder.is(Biomes.PLAINS)),
                "geodes:has_emerald_geode must cover overworld biomes such as plains");
        context.succeed();
    }

    private static void assertConditionalFeature(
            GameTestHelper context,
            HolderLookup.RegistryLookup<PlacedFeature> placed,
            String name,
            boolean modLoaded
    ) {
        ResourceKey<PlacedFeature> key = ResourceKey.create(
                Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("geodes", name));
        boolean registered = placed.get(key).isPresent();
        context.assertTrue(registered == modLoaded,
                "placed feature geodes:" + name + " registration must match its mod being loaded (modLoaded="
                        + modLoaded + ", registered=" + registered + ")");
    }

    private static void assertConditionalConfigured(
            GameTestHelper context,
            HolderLookup.RegistryLookup<net.minecraft.world.level.levelgen.feature.Feature> configured,
            String name,
            boolean modLoaded
    ) {
        ResourceKey<net.minecraft.world.level.levelgen.feature.Feature> key = ResourceKey.create(
                Registries.FEATURE, Identifier.fromNamespaceAndPath("geodes", name));
        boolean registered = configured.get(key).isPresent();
        context.assertTrue(registered == modLoaded,
                "configured feature geodes:" + name + " registration must match its mod being loaded (modLoaded="
                        + modLoaded + ", registered=" + registered + ")");
    }

    public static void config_biome_modifier_respects_the_config(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        RegistryAccess access = level.registryAccess();

        var codec = NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS
                .getValue(Identifier.parse("geodes:config_add_features"));
        context.assertTrue(codec != null, "the config_add_features serializer must be registered");

        JsonObject json = readShippedModifierJson(context, "emerald_geode");
        BiomeModifier modifier = decode(context, codec, json);

        Holder<Biome> plains = access.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
        Holder<PlacedFeature> emeraldGeode = access.lookupOrThrow(Registries.PLACED_FEATURE)
                .getOrThrow(ModPlacedFeatures.EMERALD_GEODE);

        ModifiableBiomeInfo.BiomeInfo.Builder builder = builderFor(plains);
        modifier.modify(access, plains, BiomeModifier.Phase.ADD, builder);
        context.assertTrue(featureList(builder, GenerationStep.Decoration.UNDERGROUND_DECORATION).contains(emeraldGeode),
                "with the config enabled the emerald geode feature must be added to tagged biomes");

        Path disabledDir = writeTempConfig(context, "generate_emerald_geodes=false\n");
        MoreGeodes.CONFIG.loadConfig(disabledDir);
        context.assertTrue(!MoreGeodes.CONFIG.generateEmeraldGeodes(),
                "the reloaded config must disable emerald geodes");
        builder = builderFor(plains);
        modifier.modify(access, plains, BiomeModifier.Phase.ADD, builder);
        context.assertTrue(!featureList(builder, GenerationStep.Decoration.UNDERGROUND_DECORATION).contains(emeraldGeode),
                "with generate_emerald_geodes=false the feature must not be added");

        JsonObject typoJson = new JsonObject();
        typoJson.addProperty("type", "geodes:config_add_features");
        typoJson.addProperty("biomes", "minecraft:plains");
        typoJson.addProperty("feature", "geodes:emerald_geode");
        typoJson.addProperty("step", "underground_decoration");
        typoJson.addProperty("config_key", "generate_typo_geodes");
        BiomeModifier typoModifier = decode(context, codec, typoJson);
        builder = builderFor(plains);
        typoModifier.modify(access, plains, BiomeModifier.Phase.ADD, builder);
        context.assertTrue(!featureList(builder, GenerationStep.Decoration.UNDERGROUND_DECORATION).contains(emeraldGeode),
                "an unknown config_key must disable the modifier");

        restoreDefaultConfig(context);
        context.succeed();
    }

    private static JsonObject readShippedModifierJson(GameTestHelper context, String name) {
        String resource = "/data/geodes/neoforge/biome_modifier/" + name + ".json";
        try (InputStream in = MoreGeodes.class.getResourceAsStream(resource)) {
            context.assertTrue(in != null, "shipped biome modifier JSON missing from the runtime classpath: " + resource);
            return JsonParser.parseReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("could not read " + resource, e);
        }
    }

    private static BiomeModifier decode(
            GameTestHelper context,
            com.mojang.serialization.MapCodec<? extends BiomeModifier> codec,
            JsonObject json
    ) {
        RegistryOps<JsonElement> ops = RegistryOps.create(
                JsonOps.INSTANCE, context.getLevel().registryAccess());
        var result = codec.codec().decode(ops, json).getOrThrow();
        return result.getFirst();
    }

    private static ModifiableBiomeInfo.BiomeInfo.Builder builderFor(Holder<Biome> biome) {
        ModifiableBiomeInfo.BiomeInfo original = biome.value().modifiableBiomeInfo().getOriginalBiomeInfo();
        return ModifiableBiomeInfo.BiomeInfo.Builder.copyOf(Biomes.PLAINS, original);
    }

    private static List<Holder<PlacedFeature>> featureList(
            ModifiableBiomeInfo.BiomeInfo.Builder builder,
            GenerationStep.Decoration step
    ) {
        return builder.getGenerationSettings().getFeatures(step);
    }

    private static Path writeTempConfig(GameTestHelper context, String content) {
        try {
            Path dir = Files.createTempDirectory("geodes-gametest-config");
            Files.writeString(dir.resolve("geodes.properties"), content, StandardCharsets.UTF_8);
            return dir;
        } catch (Exception e) {
            throw new RuntimeException("could not write the temp config", e);
        }
    }

    private static void restoreDefaultConfig(GameTestHelper context) {
        StringBuilder allTrue = new StringBuilder();
        for (String key : List.of(
                "generate_emerald_geodes", "generate_quartz_geodes", "generate_diamond_geodes",
                "generate_echo_geodes", "generate_lapis_geodes", "generate_gypsum_patches",
                "generate_certus_geodes", "generate_bismuth_geodes")) {
            allTrue.append(key).append("=true\n");
        }
        Path dir = writeTempConfig(context, allTrue.toString());
        MoreGeodes.CONFIG.loadConfig(dir);
        context.assertTrue(MoreGeodes.CONFIG.generateEmeraldGeodes(),
                "config restore must re-enable emerald geodes");
    }

    private static Player playerAt(GameTestHelper context, BlockPos relativePos) {
        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        BlockPos abs = context.absolutePos(relativePos);
        player.setPos(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5);
        return player;
    }
}
