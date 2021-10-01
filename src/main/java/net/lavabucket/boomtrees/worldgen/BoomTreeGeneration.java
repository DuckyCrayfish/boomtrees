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

import java.util.List;
import java.util.function.Supplier;

import net.lavabucket.boomtrees.registry.ModConfiguredFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** This class handles natural BoomTree generation in the world. */
public class BoomTreeGeneration {

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

        if (biome.equals(Biomes.FOREST.location())) {
            modifyForest(generation);
        } else if (biome.equals(Biomes.CRIMSON_FOREST.location())) {
            modifyCrimsonForest(generation);
        } else if (biome.equals(Biomes.WARPED_FOREST.location())) {
            modifyWarpedForest(generation);
        }
    }

    private static void modifyForest(BiomeGenerationSettingsBuilder generation) {
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.FOREST_BOOMTREES);
    }

    private static void modifyCrimsonForest(BiomeGenerationSettingsBuilder generation) {
        List<Supplier<ConfiguredFeature<?, ?>>> features = generation.getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION);

        for (Supplier<ConfiguredFeature<?, ?>> feature : features) {
            Feature<?> baseFeature = unwrapFeature(feature.get());
            if (baseFeature.equals(Feature.HUGE_FUNGUS)) {
                features.remove(feature);
                features.add(() -> ModConfiguredFeatures.CRIMSON_FUNGI);
                break;
            }
        }

        features.add(() -> ModConfiguredFeatures.CRIMSON_FOREST_BOOMFUNGI);
    }

    private static void modifyWarpedForest(BiomeGenerationSettingsBuilder generation) {
        List<Supplier<ConfiguredFeature<?, ?>>> features = generation.getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION);

        for (Supplier<ConfiguredFeature<?, ?>> feature : features) {
            Feature<?> baseFeature = unwrapFeature(feature.get());
            if (baseFeature.equals(Feature.HUGE_FUNGUS)) {
                features.remove(feature);
                features.add(() -> ModConfiguredFeatures.WARPED_FUNGI);
                break;
            }
        }

        features.add(() -> ModConfiguredFeatures.WARPED_FOREST_BOOMFUNGI);
    }

    private static Feature<?> unwrapFeature(ConfiguredFeature<?, ?> feature) {
        while (feature.feature() instanceof DecoratedFeature) {
            feature = ((DecoratedFeatureConfiguration)feature.config()).feature.get();
        }

        return feature.feature();
    }

}
