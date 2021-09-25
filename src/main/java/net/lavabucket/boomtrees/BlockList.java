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

import net.lavabucket.boomtrees.block.BoomLog;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlockList {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BoomTrees.MOD_ID);
    public static final RegistryObject<Block> OAK_BOOMLOG = BLOCKS.register("oak_boomlog", () -> new BoomLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

    @SubscribeEvent
    public static void onConstructModEvent(FMLConstructModEvent event) {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
    }

}
