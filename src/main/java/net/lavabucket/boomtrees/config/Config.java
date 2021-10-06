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

package net.lavabucket.boomtrees.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Configuration class.
 */
public class Config {

    public static final CommonConfig COMMON = new CommonConfig(new Builder());

    /**
     * Register this class's configs with the mod loading context.
     * @param event  the event, provided by the mod event bus
     */
    @SubscribeEvent
    public static void onConstructModEvent(FMLConstructModEvent event) {
        final ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, COMMON.spec);
    }

    /**
     * Common configuration file. Most of these should go in a server config, however biome
     * generation must be modified in the {@link BiomeLoadingEvent}, which is called prior to server
     * config loading.
    */
    public static class CommonConfig {

        public final ForgeConfigSpec spec;

        public final BooleanValue oakSpawn;
        public final IntValue oakRarity;
        public final BooleanValue crimsonSpawn;
        public final BooleanValue warpedSpawn;

        /**
         * Constructs an instance of a common config.
         * @param builder  a Forge config builder instance
         */
        public CommonConfig(final ForgeConfigSpec.Builder builder) {

            builder.push("worldgen"); // worldgen

                builder.comment("Oak BoomTrees");
                builder.push("oak"); // worldgen.oak

                    oakSpawn = builder
                        .comment("Whether or not to spawn this type of BoomTree.")
                        .define("spawn", true);

                    oakRarity = builder
                        .comment("Oak BoomTrees will spawn at a rate of 1 tree every <rarity> chunks. Higher numbers mean more rare.")
                        .defineInRange("rarity", 8, 0, Integer.MAX_VALUE);

                builder.pop(); // worlgen.oak

                builder.comment("Crimson BoomFungi");
                builder.push("crimson"); // worldgen.crimson

                    crimsonSpawn = builder
                        .comment("Whether or not to spawn this type of BoomTree.")
                        .define("spawn", true);

                builder.pop(); // worlgen.crimson

                builder.comment("Warped BoomFungi");
                builder.push("warped"); // worldgen.warped

                    warpedSpawn = builder
                        .comment("Whether or not to spawn this type of BoomTree.")
                        .define("spawn", true);

                builder.pop(); // worlgen.warped

            builder.pop(); // worldgen

            spec = builder.build();
        }

    }

}
