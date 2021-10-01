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

package net.lavabucket.boomtrees.registry;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.lavabucket.boomtrees.BoomTrees;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/** This class contains all features registered by BoomTrees. */
public class ModConfiguredFeatures {

    private static final Map<String, ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = new HashMap<>();

    private static <T extends FeatureConfiguration> ConfiguredFeature<T, ?> register(String key, ConfiguredFeature<T, ?> configuredFeature) {
        CONFIGURED_FEATURES.put(key, configuredFeature);
        return configuredFeature;
    }

    public static final ConfiguredFeature<TreeConfiguration, ?> OAK_BOOMTREE = register("oak_boomtree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(ModBlocks.OAK_BOOMLOG.get().defaultBlockState()), new StraightTrunkPlacer(5, 2, 0), new SimpleStateProvider(Blocks.OAK_LEAVES.defaultBlockState()), new SimpleStateProvider(Blocks.OAK_SAPLING.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build()));

    public static final ConfiguredFeature<?, ?> CRIMSON_BOOMFUNGUS = register("crimson_boomfungus", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), ModBlocks.CRIMSON_BOOMSTEM.get().defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false)));
    public static final ConfiguredFeature<?, ?> WARPED_BOOMFUNGUS = register("warped_boomfungus", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), ModBlocks.WARPED_BOOMSTEM.get().defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false)));

    public static final ConfiguredFeature<?, ?> FOREST_BOOMTREES = register("forest_boomtrees", Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(), OAK_BOOMTREE)).decorated(Features.Decorators.HEIGHTMAP_WITH_TREE_THRESHOLD_SQUARED).rarity(8));
    public static final ConfiguredFeature<?, ?> CRIMSON_FOREST_BOOMFUNGI = register("crimson_forest_boomfungi", CRIMSON_BOOMFUNGUS.decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(4))));
    public static final ConfiguredFeature<?, ?> WARPED_FOREST_BOOMFUNGI = register("warped_forest_boomfungi", WARPED_BOOMFUNGUS.decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(4))));

    public static final ConfiguredFeature<?, ?> CRIMSON_FUNGI =  Feature.HUGE_FUNGUS.configured(HugeFungusConfiguration.HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(4)));
    public static final ConfiguredFeature<?, ?> WARPED_FUNGI = Feature.HUGE_FUNGUS.configured(HugeFungusConfiguration.HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG).decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(4)));

    /**
     * Called by Forge during mod setup. Registers all features listed in this class.
     * @param event  the event provided by the mod event bus
     */
    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
        CONFIGURED_FEATURES.forEach((key, configuredFeature) -> {
                    ResourceLocation rl = new ResourceLocation(BoomTrees.MOD_ID, key);
                    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, rl, configuredFeature);
                });
    }

}
