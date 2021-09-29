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

package net.lavabucket.boomtrees.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/** A stripped {@code BoomLog}. Regrows back into a BoomLog. */
public class StrippedBoomLogBlock extends RotatedPillarBlock {

    private final int flammability;
    private final int fireSpreadSpeed;
    protected final Supplier<Block> unstripped;

    /**
     * Instantiates a new block object.
     *
     * @param unstripped  the unstripped version of this block
     * @param properties  the properties of the new block
     */
    public StrippedBoomLogBlock(Supplier<Block> unstripped, Properties properties) {
        this(unstripped, 5, 5, properties);
    }

    /**
     * Instantiates a new block object.
     *
     * @param unstripped  the unstripped version of this block
     * @param flammability  the flammability of the new block
     * @param fireSpreadSpeed  the speed at which fire spreads from the new block
     * @param properties  the properties of the new block
     */
    public StrippedBoomLogBlock(Supplier<Block> unstripped, int flammability, int fireSpreadSpeed, Properties properties) {
        super(properties);
        this.unstripped = unstripped;
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
    }

    /** {@return the flammability of this block} */
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return flammability;
    }

    /** {@return the fire spread speed of this block} */
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return fireSpreadSpeed;
    }

    /** {@return true, indicating that this block should receive random ticks} */
    @Override
    public boolean isRandomlyTicking(BlockState p_52288_) {
        return true;
    }

    /**
     * Called by Minecraft when a block of this type receives a random tick.
     * Regrows the bark on the ticked block.
     *
     * @param blockState  the {@code BlockState} of the block
     * @param level  the level in which the block exists
     * @param position  the position of the block
     * @param random  the random number generator for the level
     */
    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos position, Random random) {
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = position.relative(direction);
            if (level.getBlockState(adjacent).getBlock() instanceof BaseFireBlock) {
                return;
            }
        }

        level.setBlockAndUpdate(position, unstrip(blockState));
    }

    /**
     * Returns the unstripped version of {@code blockState}.
     * Assumes {@code blockState} is an instance of this block.
     *
     * @param blockState  the {@code BlockState} to convert to an unstripped version
     * @return  the unstripped version of {@code blockState}
     */
    public BlockState unstrip(BlockState blockState) {
        return unstripped.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
    }

}
