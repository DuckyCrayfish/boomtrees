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
import net.minecraft.world.level.BlockGetter;
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
    private final int flammability;
    private final int fireSpreadSpeed;

    public BoomLog(Properties properties) {
        this(400, 5, properties);
    }

    public BoomLog(int flammability, int fireSpreadSpeed, Properties properties) {
        super(properties);
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return flammability;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return fireSpreadSpeed;
    }

    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult hitResult,
            Projectile projectile) {

        if (!level.isClientSide()) {
            BlockPos position = hitResult.getBlockPos();
            explode(blockState, level, position);
        }
    }

    @Override
    public void catchFire(BlockState blockState, Level level, BlockPos position, Direction face,
            LivingEntity igniter) {
        explode(blockState, level, position);
        super.catchFire(blockState, level, position, face, igniter);
    }

    @Override
    public void attack(BlockState blockState, Level level, BlockPos position, Player player) {
        explode(blockState, level, position);
    }

    @Override
    public BlockState getToolModifiedState(BlockState blockState, Level level, BlockPos position,
            Player player, ItemStack tool, ToolAction toolAction) {

        if (tool.canPerformAction(toolAction) && ToolActions.AXE_STRIP.equals(toolAction)) {
            if (!level.isClientSide()) {
                dropStripResources(blockState, level, position, player, tool);
            }
            return strip(blockState);
        } else {
            return super.getToolModifiedState(blockState, level, position, player, tool, toolAction);
        }
    }

    public void explode(BlockState blockState, Level level, BlockPos position) {
        Vec3 center = Vec3.atCenterOf(position);
        float radius = 2.0F;
        Explosion.BlockInteraction interaction = Explosion.BlockInteraction.NONE;
        BlockState strippedState = strip(blockState);

        level.explode(null, center.x, center.y, center.z, radius, interaction);
        level.setBlockAndUpdate(position, strippedState);
    }

    public void dropStripResources(BlockState blockState, Level level, BlockPos position,
            @Nullable Entity harvester, ItemStack tool) {

        List<ItemStack> drops = getStripDrops(blockState, level, position, harvester, tool);
        Vec3 vectorToPlayer = Vec3.atCenterOf(position).vectorTo(harvester.position());
        Direction direction = Direction.getNearest(vectorToPlayer.x, vectorToPlayer.y, vectorToPlayer.z);

        drops.forEach(drop -> popResourceFromFace(level, position, direction, drop));
    }

    public ResourceLocation getStripLootTable() {
        return STRIP_LOOT_TABLE;
    }

    public List<ItemStack> getStripDrops(BlockState blockState, Level level, BlockPos position,
            @Nullable Entity harvester, ItemStack tool) {

        ResourceLocation lootTableLocation = getStripLootTable();
        if (lootTableLocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        }

        LootTable lootTable = level.getServer().getLootTables().get(lootTableLocation);

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

    public BlockState strip(BlockState state) {
        return Blocks.STRIPPED_OAK_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
    }

}
