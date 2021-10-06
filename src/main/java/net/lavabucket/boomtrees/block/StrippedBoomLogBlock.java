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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** A stripped {@code BoomLog}. Regrows back into a BoomLog. */
public class StrippedBoomLogBlock extends RotatedPillarBlock {

    private static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    private static final int AGE_MAX = BlockStateProperties.MAX_AGE_2;

    protected final Block regrown;

    private final int flammability;
    private final int fireSpreadSpeed;

    /**
     * Instantiates a new block object.
     *
     * @param regrown  the regrown version of this block
     * @param properties  the properties of the new block
     */
    public StrippedBoomLogBlock(Supplier<Block> regrown, Properties properties) {
        this(regrown, 5, 5, properties);
    }

    /**
     * Instantiates a new block object.
     *
     * @param regrown  the regrown version of this block
     * @param flammability  the flammability of the new block
     * @param fireSpreadSpeed  the speed at which fire spreads from the new block
     * @param properties  the properties of the new block
     */
    public StrippedBoomLogBlock(Supplier<Block> regrown, int flammability, int fireSpreadSpeed,
            Properties properties) {

        super(properties);
        this.regrown = regrown.get();
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
    }

    /**
     * Adds all of this block's properties to the definition of its BlockStates.
     * @param builder  the state definition builder
     */
    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getAgeProperty());
    }

    /** {@return the flammability of this block} */
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return flammability;
    }

    /** {@return the fire spread speed of this block} */
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos,
            Direction face) {
        return fireSpreadSpeed;
    }


    /** {@return this block's 'age' property} */
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    /** {@return the max value of this block's 'age' property} */
    public int getMaxAge() {
        return AGE_MAX;
    }

    /**
     * {@return the 'age' property value of {@code blockState}}
     * @param blockState  the {@code BlockState} whose property will be queried
     */
    protected int getAge(BlockState blockState) {
        return blockState.getValue(getAgeProperty());
    }

    /**
     * {@return true if the 'age' property of {@code blockState} is at its max, false otherwise}
     * @param blockState  the {@code BlockState} whose property will be queried
     */
    public boolean isMaxAge(BlockState blockState) {
        return blockState.getValue(getAgeProperty()) >= getMaxAge();
    }

    /**
     * Returns the {@code BlockState} for {@code blockState} after being aged once.
     * If {@code blockState} is already maximum age, returns regrown version.
     *
     * @param blockState  the {@code BlockState} to age
     * @return a {@code BlockState} with an age 1 greater than {@code blockState}, or regrown
     * {@code BlockState} if age is max
     */
    public BlockState incrementAge(BlockState blockState) {
        int age = getAge(blockState);
        if (age < getMaxAge()) {
            return blockState.setValue(getAgeProperty(), age + 1);
        } else {
            return regrow(blockState);
        }
    }

    /**
     * Called by Minecraft when a block of this type receives a tick.
     * Regrows the bark on the ticked block.
     *
     * @param blockState  the {@code BlockState} of the block
     * @param level  the level in which the block exists
     * @param pos  the position of the block
     * @param random  the random number generator for the level
     */
    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, Random random) {
        if (hasFireAdjacent(level, pos)) {
            return;
        }

        BlockState agedState = incrementAge(blockState);
        level.setBlockAndUpdate(pos, agedState);
    }

    /**
     * Returns the regrown version of {@code blockState}.
     *
     * @param blockState  the {@code BlockState} to convert to a regrown version
     * @return  the regrown version of {@code blockState}
     */
    public BlockState regrow(BlockState blockState) {
        return regrown.withPropertiesOf(blockState);
    }

    private boolean hasFireAdjacent(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = pos.relative(direction);
            if (level.getBlockState(adjacent).getBlock() instanceof BaseFireBlock) {
                return true;
            }
        }
        return false;
    }

}
