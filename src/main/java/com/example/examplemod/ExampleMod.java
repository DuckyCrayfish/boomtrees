/*
 * Copyright (C) 2021 Your Name
 *
 * This file is part of examplemod.
 *
 * examplemod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * examplemod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with examplemod.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.examplemod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/** This class contains the mod entry point and general mod constants. */
@Mod(ExampleMod.MOD_ID)
public class ExampleMod {

    /** Mod identifier. The value here should match an entry in the META-INF/mods.toml file. */
    public static final String MOD_ID = "examplemod";
    /** Log4j marker for mod logs. */
    public static final Marker MARKER = MarkerManager.getMarker(MOD_ID);

    private static final Logger LOGGER = LogManager.getLogger();

    /** Mod entry point. */
    public ExampleMod() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::setup);
        forgeBus.addListener(this::onServerStarting);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info(MARKER, "HELLO FROM PREINIT");
    }

    private void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info(MARKER, "HELLO FROM SERVER START");
    }

}
