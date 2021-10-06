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

import net.lavabucket.boomtrees.config.Config;
import net.lavabucket.boomtrees.registry.ModBlocks;
import net.lavabucket.boomtrees.registry.ModConfiguredFeatures;
import net.lavabucket.boomtrees.registry.ModItems;
import net.lavabucket.boomtrees.worldgen.BoomTreeGeneration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/** This class contains the mod entry point and general mod constants. */
@Mod(BoomTrees.MOD_ID)
public class BoomTrees {

    /** Mod identifier. The value here should match an entry in the META-INF/mods.toml file. */
    public static final String MOD_ID = "boomtrees";
    /** Log4j marker for mod logs. */
    public static final Marker MARKER = MarkerManager.getMarker(MOD_ID);

    /** Mod entry point. */
    public BoomTrees() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.register(ModBlocks.class);
        modBus.register(ModConfiguredFeatures.class);
        modBus.register(ModItems.class);
        modBus.register(Config.class);

        forgeBus.register(BoomTreeGeneration.class);
    }

}
