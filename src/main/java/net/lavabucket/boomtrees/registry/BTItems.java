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

import net.lavabucket.boomtrees.BoomTrees;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class BTItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BoomTrees.MOD_ID);

    public static final RegistryObject<Item> OAK_BOOMLOG_ITEM = ITEMS.register("oak_boomlog", () -> new BlockItem(BTBlocks.OAK_BOOMLOG.get(), (new Item.Properties()).tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    public static final RegistryObject<Item> STRIPPED_OAK_BOOMLOG = ITEMS.register("stripped_oak_boomlog", () -> new BlockItem(BTBlocks.STRIPPED_OAK_BOOMLOG.get(), (new Item.Properties()).tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    /**
     * Called when the mod is being created. This method registers all items listed in this class.
     * @param event  the event provided by the mod event bus
     */
    @SubscribeEvent
    public static void onConstructModEvent(FMLConstructModEvent event) {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
    }

}
