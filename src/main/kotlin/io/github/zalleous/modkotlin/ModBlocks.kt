package io.github.zalleous.modkotlin

import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.S2CConfigurationChannelEvents
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.MapColor
import net.minecraft.block.PillarBlock
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.GameMode
import net.minecraft.world.World

object ModItems {
    lateinit var hammerItem: Item

    fun customItem() {
        hammerItem = RegistryHelper.registerItem(
            "custom-item",
            settingsBuilder = { maxCount(4) },
            creativeTabKey = ItemGroups.COMBAT
        )
    }

    var lastHitDirection: Direction? = null


    fun registerDirectionTracker() {
        AttackBlockCallback.EVENT.register { player, world, hand, pos, direction ->
            lastHitDirection = direction
            ActionResult.PASS
        }
    }

    fun setupHammerBlockBreaking() {
        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, blockEntity ->
            val direction = lastHitDirection ?: Direction.UP // fallback au besoin

            val range = -1..1
            val positions = mutableListOf<BlockPos>()

            for (i in range) {
                for (j in range) {
                    val targetPos = when (direction.axis) {
                        Direction.Axis.Y -> pos.add(i, 0, j) // face haut/bas → plan XZ
                        Direction.Axis.Z -> pos.add(i, j, 0) // face nord/sud → plan XY
                        Direction.Axis.X -> pos.add(0, j, i) // face est/ouest → plan YZ
                    }
                    if (targetPos != pos) positions += targetPos
                }
            }

            val stack = player.getStackInHand(Hand.MAIN_HAND)
            if (stack.item == hammerItem) {
                for (target in positions) {
                    if (world.getBlockState(target).getHardness(world, target) >= 0) {
                        world.breakBlock(target, player.gameMode == GameMode.SURVIVAL, player)
                    }
                }
            }
        }
    }

    /**
     * Call during initialization to register all blocks
     */
    fun register() {
        customItem()

        registerDirectionTracker()
        setupHammerBlockBreaking()

    }
}