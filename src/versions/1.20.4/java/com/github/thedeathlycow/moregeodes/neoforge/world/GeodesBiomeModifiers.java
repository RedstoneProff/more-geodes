package com.github.thedeathlycow.moregeodes.neoforge.world;

import com.github.thedeathlycow.moregeodes.MoreGeodes;
import com.github.thedeathlycow.moregeodes.config.GeodesConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public final class GeodesBiomeModifiers {
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MoreGeodes.MODID);

    private record ConfigAddFeatures(
            HolderSet<Biome> biomes,
            Holder<PlacedFeature> feature,
            GenerationStep.Decoration step,
            String configKey,
            Optional<String> requiresMod
    ) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD && this.biomes.contains(biome) && this.passes()) {
                builder.getGenerationSettings().addFeature(this.step, this.feature);
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return CONFIG_ADD_FEATURES_CODEC;
        }

        private boolean passes() {
            if (this.requiresMod.isPresent() && !MoreGeodes.isModLoaded(this.requiresMod.get())) {
                return false;
            }
            return configEnabled(this.configKey).getAsBoolean();
        }
    }

    private static final Codec<ConfigAddFeatures> CONFIG_ADD_FEATURES_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigAddFeatures::biomes),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(ConfigAddFeatures::feature),
                    GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ConfigAddFeatures::step),
                    Codec.STRING.fieldOf("config_key").forGetter(ConfigAddFeatures::configKey),
                    Codec.STRING.optionalFieldOf("requires_mod").forGetter(ConfigAddFeatures::requiresMod)
            ).apply(instance, ConfigAddFeatures::new));

    public static void register(IEventBus modEventBus) {
        BIOME_MODIFIER_SERIALIZERS.register("config_add_features", () -> CONFIG_ADD_FEATURES_CODEC);
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    private static BooleanSupplier configEnabled(String configKey) {
        GeodesConfig config = MoreGeodes.CONFIG;
        return switch (configKey) {
            case "generate_emerald_geodes" -> config::generateEmeraldGeodes;
            case "generate_quartz_geodes" -> config::generateQuartzGeodes;
            case "generate_diamond_geodes" -> config::generateDiamondGeodes;
            case "generate_echo_geodes" -> config::generateEchoGeodes;
            case "generate_lapis_geodes" -> config::generateLapisGeodes;
            case "generate_gypsum_patches" -> config::generateGypsumPatches;
            case "generate_certus_geodes" -> config::generateCertusGeodes;
            case "generate_bismuth_geodes" -> config::generateBismuthGeodes;
            default -> () -> false;
        };
    }

    private GeodesBiomeModifiers() {
    }
}
