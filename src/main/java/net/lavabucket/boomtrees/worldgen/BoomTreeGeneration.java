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

import net.lavabucket.boomtrees.registry.ModConfiguredFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
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
        if (biome.equals(Biomes.FOREST.location())) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.FOREST_BOOMTREES);
        } else if (biome.equals(Biomes.CRIMSON_FOREST.location())) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.CRIMSON_FOREST_BOOMFUNGI);
        } else if (biome.equals(Biomes.WARPED_FOREST.location())) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.WARPED_FOREST_BOOMFUNGI);
        }
    }

}
