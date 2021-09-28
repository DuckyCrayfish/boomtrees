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

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.lavabucket.boomtrees.BoomTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class BoomLog extends RotatedPillarBlock {

    /** The default loot table used for log stripping when no custom loot table is provided. */
    public static final ResourceLocation DEFAULT_STRIP_LOOT_TABLE =
            new ResourceLocation("boomtrees", "gameplay/boomtrees/strip/default");

    private final Supplier<Block> stripped;
    private final int flammability;
    private final int fireSpreadSpeed;

    /**
     * Instantiates a new block object.
     *
     * @param stripped  the stripped version of this block
     * @param properties  the properties of the new block
     */
    public BoomLog(Supplier<Block> stripped, Properties properties) {
        this(stripped, 400, 5, properties);
    }

    /**
     * Instantiates a new block object.
     *
     * @param stripped  the stripped version of this block
     * @param flammability  the flammability of the new block
     * @param fireSpreadSpeed  the speed at which fire spreads from the new block
     * @param properties  the properties of the new block
     */
    public BoomLog(Supplier<Block> stripped, int flammability, int fireSpreadSpeed, Properties properties) {
        super(properties);
        this.stripped = stripped;
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

    /**
     * This method is called when this block is hit by a projectile.
     * <p>This method triggers a bark explosion.
     *
     * @param level  the level in which the block exists
     * @param blockState  the {@code BlockState} of the block
     * @param hitResult  the {@code BlockHitResult} of the projectile landing
     * @param projectile  the projectile that landed
     */
    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult hitResult,
            Projectile projectile) {

        if (!level.isClientSide()) {
            BlockPos position = hitResult.getBlockPos();
            explode(blockState, level, position);
        }
    }

    /**
     * This method is called when this block is consumed by fire.
     * <p>This method triggers a bark explosion.
     *
     * @param level  the level in which the block exists
     * @param blockState  the {@code BlockState} of the block
     * @param position  the position of the block
     * @param face  the face that caught on fire
     * @param igniter  the entity that ignited the block
     */
    @Override
    public void catchFire(BlockState blockState, Level level, BlockPos position, Direction face,
            LivingEntity igniter) {
        triggerNeighbors(level, position);
    }

    /**
     * This method is called when this block is attacked by a player.
     * <p>This method triggers a bark explosion.
     *
     * @param blockState  the {@code BlockState} of the block
     * @param level  the level in which the block exists
     * @param position  the position of the block
     * @param player  the player who attacked the block
     */
    @Override
    public void attack(BlockState blockState, Level level, BlockPos position, Player player) {
        explode(blockState, level, position);
    }

    /**
     * This method is called when a player uses a tool on this block.
     *
     * <p>This method strips the bark off this block and drops loot when called upon by an axe strip
     * {@code ToolAction}.
     *
     * @param blockState  the {@code BlockState} of the block
     * @param level  the level in which the block exists
     * @param position  the position of the block
     * @param player  the player who used the tool on this block
     * @param tool  the {@code ItemStack} of the tool that was used on this block
     * @param toolAction  the {@code ToolAction} that was performed on this block
     * @return the new {@code BlockState} if the {@code ToolAction} was successful, null otherwise
     */
    @Override
    public BlockState getToolModifiedState(BlockState blockState, Level level, BlockPos position,
            Player player, ItemStack tool, ToolAction toolAction) {

                this.getLootTable();
        if (tool.canPerformAction(toolAction) && ToolActions.AXE_STRIP.equals(toolAction)) {
            if (!level.isClientSide()) {
                dropStripResources(blockState, level, position, player, tool);
            }
            return strip(blockState);
        } else {
            return super.getToolModifiedState(blockState, level, position, player, tool, toolAction);
        }
    }

    /**
     * Triggers the {@code BoomLog} to explode, stripping off the bark.
     *
     * @param blockState  the {@code BlockState} of the block to explode
     * @param level  the level in which the block exists
     * @param position  the position of the block
     */
    public void explode(BlockState blockState, Level level, BlockPos position) {
        Vec3 center = Vec3.atCenterOf(position);
        float radius = 2.0F;
        Explosion.BlockInteraction interaction = Explosion.BlockInteraction.NONE;

        level.explode(null, center.x, center.y, center.z, radius, interaction);

        BlockState strippedState = strip(blockState);
        level.setBlockAndUpdate(position, strippedState);

        triggerNeighbors(level, position);
    }

    public void triggerNeighbors(Level level, BlockPos position) {
        triggerNeighbor(level, position.west());
        triggerNeighbor(level, position.east());
        triggerNeighbor(level, position.below());
        triggerNeighbor(level, position.above());
        triggerNeighbor(level, position.north());
        triggerNeighbor(level, position.south());
    }

    public void triggerNeighbor(Level level, BlockPos position) {
        Block block = level.getBlockState(position).getBlock();
        if (block instanceof BoomLog) {
            BoomLog log = (BoomLog) block;
            log.explode(level.getBlockState(position), level, position);
        }
    }

    /**
     * Drops the loot from an entity stripping the bark off of this block.
     *
     * @param blockState  the {@code BlockState} of the block that was stripped
     * @param level  the level in which the block exists
     * @param position  the position of the block
     * @param harvester  the entity who stripped the bark
     * @param tool  the tool used to strip the bark
     */
    public void dropStripResources(BlockState blockState, Level level, BlockPos position,
            @Nullable Entity harvester, ItemStack tool) {

        List<ItemStack> drops = getStripDrops(blockState, level, position, harvester, tool);
        Vec3 vectorToPlayer = Vec3.atCenterOf(position).vectorTo(harvester.position());
        Direction direction = Direction.getNearest(vectorToPlayer.x, vectorToPlayer.y, vectorToPlayer.z);

        drops.forEach(drop -> popResourceFromFace(level, position, direction, drop));
    }

    /**
     * Returns the loot table for the stripping of this block.
     *
     * <p>This method first checks for a custom loot table that matches the resource ID of this
     * block in gameplay/boomtrees/strip/. If none exists, this method instead returns the default
     * loot table {@link #DEFAULT_STRIP_LOOT_TABLE}.
     *
     * @param server  the running Minecraft server
     * @return the stripping loot table
     */
    public LootTable getStripLootTable(MinecraftServer server) {
        String namespace = getRegistryName().getNamespace();
        String path = getRegistryName().getPath();
        ResourceLocation customLocation =
                new ResourceLocation(namespace, "gameplay/" + BoomTrees.MOD_ID + "/strip/" + path);
        LootTable customLootTable = server.getLootTables().get(customLocation);

        if (customLootTable.equals(LootTable.EMPTY)) {
            return server.getLootTables().get(DEFAULT_STRIP_LOOT_TABLE);
        } else {
            return customLootTable;
        }
    }

    /**
     * Generates and returns drops for the stripping of this block.
     *
     * @param blockState  the {@code BlockState} of the block that was stripped
     * @param level  the level in which the block exists
     * @param position  the position of the block
     * @param harvester  the entity who stripped the bark
     * @param tool  the tool used to strip the bark
     * @return drops for the stripping of this block
     */
    public List<ItemStack> getStripDrops(BlockState blockState, Level level, BlockPos position,
            @Nullable Entity harvester, ItemStack tool) {

        LootTable lootTable = getStripLootTable(level.getServer());

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

    /**
     * {@return the new {@code BlockState} after a block with state {@code blockState} is stripped}
     * @param blockState  the state of the block to be stripped
     */
    public BlockState strip(BlockState blockState) {
        return stripped.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
    }

}
