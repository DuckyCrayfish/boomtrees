/*
 * Copyright (C) 2021 Nick Iacullo
 *
 * This file is part of BoomTrees.
 *
 * BoomTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoomTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BoomTrees.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lavabucket.boomtrees.worldgen;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.lavabucket.boomtrees.config.Config;
import net.lavabucket.boomtrees.registry.ModConfiguredFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** This class handles natural BoomTree generation in the world. */
public class BoomTreeGeneration {

    private static final Map<ResourceLocation, Consumer<BiomeGenerationSettingsBuilder>> modifiers = Map.ofEntries(
        Map.entry(Biomes.FOREST.location(), BoomTreeGeneration::modifyForest),
        Map.entry(Biomes.CRIMSON_FOREST.location(), BoomTreeGeneration::modifyCrimsonForest),
        Map.entry(Biomes.WARPED_FOREST.location(), BoomTreeGeneration::modifyWarpedForest)
    );

    /**
     * Called when a biome configuration is being loaded for a world. All biome tree modifications
     * are initiated here.
     *
     * @param event the event provided by the Forge event bus
     */
    @SubscribeEvent
    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        ResourceLocation biome = event.getName();
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        Consumer<BiomeGenerationSettingsBuilder> modifier = modifiers.get(biome);
        if (modifier != null) {
            modifier.accept(generation);
        }
    }

    private static void modifyForest(BiomeGenerationSettingsBuilder generation) {
        if (Config.COMMON.oakSpawn.get()) {
            generation.addFeature(VEGETAL_DECORATION, ModConfiguredFeatures.OAK_BOOMTREES.get());
        }
    }

    private static void modifyCrimsonForest(BiomeGenerationSettingsBuilder generation) {
        if (!Config.COMMON.crimsonSpawn.get()) {
            return;
        }

        List<Supplier<ConfiguredFeature<?, ?>>> features =
                generation.getFeatures(VEGETAL_DECORATION);

        findFeature(features, feature -> isFeature(feature.get(), Feature.HUGE_FUNGUS))
                .ifPresent(fungiFeature -> {
                    features.remove(fungiFeature);
                    features.add(() -> ModConfiguredFeatures.CRIMSON_FOREST_FUNGI.get());
                });

        features.add(() -> ModConfiguredFeatures.CRIMSON_FOREST_BOOMFUNGI.get());
    }

    private static void modifyWarpedForest(BiomeGenerationSettingsBuilder generation) {
        if (!Config.COMMON.warpedSpawn.get()) {
            return;
        }

        List<Supplier<ConfiguredFeature<?, ?>>> features =
                generation.getFeatures(VEGETAL_DECORATION);

        findFeature(features, feature -> isFeature(feature.get(), Feature.HUGE_FUNGUS))
                .ifPresent(fungiFeature -> {
                    features.remove(fungiFeature);
                    features.add(() -> ModConfiguredFeatures.WARPED_FOREST_FUNGI.get());
                });

        features.add(() -> ModConfiguredFeatures.WARPED_FOREST_BOOMFUNGI.get());
    }

    /*
     * Checks if configuredFeature is a version of feature. Accounts for configuredFeature
     * decorations.
     */
    private static boolean isFeature(ConfiguredFeature<?, ?> configuredFeature,
            Feature<?> feature) {

        if (configuredFeature.feature().equals(feature)) {
            return true;
        }

        while (configuredFeature.feature() instanceof DecoratedFeature) {
            DecoratedFeatureConfiguration decorated =
                    (DecoratedFeatureConfiguration) configuredFeature.config();

            configuredFeature = decorated.feature.get();
            if (configuredFeature.feature().equals(feature)) {
                return true;
            }
        }

        return false;
    }

    /*
     * Returns an Optional feature that matches predicate from featureList.
     */
    private static Optional<Supplier<ConfiguredFeature<?, ?>>> findFeature(
                List<Supplier<ConfiguredFeature<?, ?>>> featureList,
                Predicate<Supplier<ConfiguredFeature<?, ?>>> predicate) {

        return featureList.stream().filter(predicate).findFirst();
    }

}
