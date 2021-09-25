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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class BoomLog extends RotatedPillarBlock {

    public static final ResourceLocation STRIP_LOOT_TABLE = new ResourceLocation("boomtrees", "gameplay/boomlog_strip");

    public BoomLog(Properties properties) {
        super(properties);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        if (!level.isClientSide()) {
            BlockPos position = hitResult.getBlockPos();
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

    @Override
    public BlockState getToolModifiedState(BlockState blockState, Level world, BlockPos position,
            Player player, ItemStack tool, ToolAction toolAction) {

        if (tool.canPerformAction(toolAction) && ToolActions.AXE_STRIP.equals(toolAction)) {
            if (!world.isClientSide()) {
                dropStripResources(blockState, world, position, player, tool);
            }
            return getStrippedState(blockState);
        } else {
            return super.getToolModifiedState(blockState, world, position, player, tool, toolAction);
        }
    }

    public void explode(BlockState state, Level level, BlockPos position) {
        level.explode(null, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, 2.0F, Explosion.BlockInteraction.NONE);
        BlockState strippedState = getStrippedState(state);
        level.setBlockAndUpdate(position, strippedState);
    }

    public void dropStripResources(BlockState blockState, Level level, BlockPos position, @Nullable Entity harvester, ItemStack tool) {
        List<ItemStack> drops = getStripDrops(blockState, level, position, harvester, tool);
        Vec3 vectorToPlayer = Vec3.atCenterOf(position).vectorTo(harvester.position());
        Direction direction = Direction.getNearest(vectorToPlayer.x, vectorToPlayer.y, vectorToPlayer.z);

        drops.forEach(drop -> popResourceFromFace(level, position, direction, drop));
    }

    public ResourceLocation getStripLootTable() {
        return STRIP_LOOT_TABLE;
    }

    public List<ItemStack> getStripDrops(BlockState blockState, Level level, BlockPos position, @Nullable Entity harvester, ItemStack tool) {
        ResourceLocation resourcelocation = getLootTable();
        if (resourcelocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootTable lootTable = level.getServer().getLootTables().get(getStripLootTable());

            LootContext.Builder lootBuilder = (new LootContext.Builder((ServerLevel) level))
                    .withRandom(level.random)
                    .withParameter(LootContextParams.BLOCK_STATE, blockState)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(position))
                    .withParameter(LootContextParams.TOOL, tool)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, harvester);

            LootContext lootContext = lootBuilder.create(LootContextParamSets.BLOCK);
            List<ItemStack> drops = lootTable.getRandomItems(lootContext);
            return drops;
        }
    }

    public BlockState getStrippedState(BlockState state) {
        return Blocks.STRIPPED_OAK_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
    }

}
