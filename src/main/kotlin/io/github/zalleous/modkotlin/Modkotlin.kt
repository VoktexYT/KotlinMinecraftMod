package io.github.zalleous.modkotlin

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.ActionResult
import net.minecraft.world.World
import net.minecraft.item.Items
import net.minecraft.block.Blocks
import net.minecraft.text.Text

class Modkotlin : ModInitializer {
    fun prototypeExplosionWoodenAxeWhenAttackBlock() {
        AttackBlockCallback.EVENT.register { player, world, hand, direct, hitResult ->
            println(player.getStackInHand(hand))

            if (!world.isClient && player.getStackInHand(hand).item == Items.WOODEN_AXE) {
                world.createExplosion(player, direct.x.toDouble(), direct.y.toDouble(), direct.z.toDouble(), 4f, World.ExplosionSourceType.TNT)
            }
            ActionResult.PASS
        }
    }

    fun prototypeDarknessEffectWhenDirtBlockAttackBlock() {
        AttackBlockCallback.EVENT.register { player, world, hand, direct, hitResult ->
            if (!world.isClient && player.getStackInHand(hand).item == Items.DIRT) {
                player.addStatusEffect(StatusEffectInstance(StatusEffects.DARKNESS, 100, 10), player)
            }
            ActionResult.PASS
        }
    }

    fun prototypeSendMessgeWhenPlayerInteractWithAStick() {
        UseBlockCallback.EVENT.register { player, world, hand, result ->
            if (player.getStackInHand(hand).item == Items.STICK) {
                player.sendMessage(Text.of("Hey stickman!"), false)
            }
            ActionResult.PASS
        }
    }

    fun prototypeFireStick() {
        UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
            val stack = player.getStackInHand(hand)

            if (stack.item == Items.BLAZE_ROD) {
                val clickedPos = hitResult.blockPos
                val firePos = clickedPos.up()

                // Check if the space above is air (or replaceable)
                if (world.getBlockState(firePos).isReplaceable) {
                    world.setBlockState(firePos, Blocks.FIRE.defaultState)
                    player.swingHand(hand)
                }
            }

            ActionResult.PASS
        }
    }

    override fun onInitialize() {
//        prototypeExplosionWoodenAxeWhenAttackBlock()
//        prototypeDarknessEffectWhenDirtBlockAttackBlock()
//        prototypeSendMessgeWhenPlayerInteractWithAStick()
//        prototypeFireStick()
    }
}
