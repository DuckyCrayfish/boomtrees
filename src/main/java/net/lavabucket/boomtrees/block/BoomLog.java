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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BoomLog extends RotatedPillarBlock {

    public BoomLog(Properties properties) {
        super(properties);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult blockHitResult, Projectile projectile) {
        if (!level.isClientSide()) {
            BlockPos position = blockHitResult.getBlockPos();
            explode(state, level, position);
        }
    }

    @Override
    public void catchFire(BlockState state, Level level, BlockPos position, Direction face, LivingEntity igniter) {
        explode(state, level, position);
        super.catchFire(state, level, position, face, igniter);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos position, Player player) {
        explode(state, level, position);
    }

    public void explode(BlockState state, Level level, BlockPos position) {
        level.explode(null, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, 2.0F, Explosion.BlockInteraction.NONE);
    }

}
