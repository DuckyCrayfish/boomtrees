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

package net.lavabucket.boomtrees;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ConfiguredFeatureList {
    private static final Map<String, ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = new HashMap<>();

    private static <T extends FeatureConfiguration> ConfiguredFeature<T, ?> register(String key, ConfiguredFeature<T, ?> configuredFeature) {
        CONFIGURED_FEATURES.put(key, configuredFeature);
        return configuredFeature;
    }

    public static ConfiguredFeature<TreeConfiguration, ?> OAK_BOOMTREE = register("oak_boomtree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(BlockList.OAK_BOOMLOG.get().defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), new SimpleStateProvider(Blocks.OAK_LEAVES.defaultBlockState()), new SimpleStateProvider(Blocks.OAK_SAPLING.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build()));

    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
        CONFIGURED_FEATURES.forEach((key, configuredFeature) -> {
                    ResourceLocation rl = new ResourceLocation(BoomTrees.MOD_ID, key);
                    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, rl, configuredFeature);
                });
    }

}
