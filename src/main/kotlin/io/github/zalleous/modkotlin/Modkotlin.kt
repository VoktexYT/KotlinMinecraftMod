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
    /**
     * prototypeExplosionWoodenAxeWhenAttackBlock()
     *
     * This prototype is use to make a TNT explosion
     * when player attack a block with a wooden axe
     *
     * AttackBlockEvent:
     *
     * (RIGHT CLICK EVENT ATTACK BLOCK)
     *  AttackBlockCallback.EVENT.register { player, world, hand, direct, hitResult }
     *      direct    -> block position
     *      hitResult -> Direction
     *
     *  (GET THE PLAYER HAND'S ITEM)
     *      player.getStackInHand(hand).item
     *
     * (CREATE TNT EXPLOSION)
     *      world.createExplosion(player,
     *
     *          (Position X:Y:Z Double type (Not int))
     *          direct.x.toDouble(), direct.y.toDouble(), direct.z.toDouble(),
     *
     *          (Power of the explosion (Float type))
     *          4f,
     *
     *          World.ExplosionSourceType.TNT
     *      )
     */
    fun prototypeExplosionWoodenAxeWhenAttackBlock() {
        AttackBlockCallback.EVENT.register { player, world, hand, direct, hitResult ->
            println(player.getStackInHand(hand))

            if (!world.isClient && player.getStackInHand(hand).item == Items.WOODEN_AXE) {
                world.createExplosion(player, direct.x.toDouble(), direct.y.toDouble(), direct.z.toDouble(), 4f, World.ExplosionSourceType.TNT)
            }
            ActionResult.PASS
        }
    }

    /**
     * prototypeDarknessEffectWhenDirtBlockAttackBlock()
     *
     * This method is used for add player effect
     * when he attacks a block with the dirt block
     *
     * (ADD POTION EFFECT TO PLAYER)
     * player.addStatusEffect(StatusEffectInstance(StatusEffects.DARKNESS, 100,         10)
     *                                               ^ Type of effect      ^ duration ^ power
     */
    fun prototypeDarknessEffectWhenDirtBlockAttackBlock() {
        AttackBlockCallback.EVENT.register { player, world, hand, direct, hitResult ->
            if (!world.isClient && player.getStackInHand(hand).item == Items.DIRT) {
                player.addStatusEffect(StatusEffectInstance(StatusEffects.DARKNESS, 100, 10), player)
            }
            ActionResult.PASS
        }
    }

    /**
     * prototypeSendMessgeWhenPlayerInteractWithAStick()
     *
     * Send chat message to player when he
     * left-clicks on a block with a stick
     *
     * (LEFT CLICK EVENT)
     * UseBlockCallback.EVENT.register
     *      ^ Event when leftClick on block
     *
     * (SEND CHAT MESSAGE TO PLAYER)
     *  player.sendMessage(Text.of("Hey stickman!"), false)
     *       ^ Send message   ^ text format ^ string   ^ overlay
     */
    fun prototypeSendMessgeWhenPlayerInteractWithAStick() {
        UseBlockCallback.EVENT.register { player, world, hand, result ->
            if (player.getStackInHand(hand).item == Items.STICK) {
                player.sendMessage(Text.of("Hey stickman!"), false)
            }
            ActionResult.PASS
        }
    }

    /**
     * prototypeFireStick()
     *
     * This method is used to fire every block than the player want with his blaze stick
     *
     * (GET PLAYER HAND ITEM)
     * val stack = player.getStackInHand(hand)
     *
     * (CHANGE BLOCK STATE WITH AN OTHER)
     * world.setBlockState(firePos, Blocks.FIRE.defaultState)
     *                     ^ position     ^ Other block
     *
     * (PLAY PLAYER ATTACK ANIMATION)
     * player.swingHand(hand)
     */
    fun prototypeFireStick() {
        UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
            val stack = player.getStackInHand(hand)

            // If the player item is a blaze rod
            if (stack.item == Items.BLAZE_ROD) {
                val clickedPos = hitResult.blockPos

                // Get the position of the block above it
                val firePos = clickedPos.up()

                // Check if the space above is air (or replaceable)
                if (world.getBlockState(firePos).isReplaceable) {

                    // Change block state
                    world.setBlockState(firePos, Blocks.FIRE.defaultState)

                    // Play attack hand animation
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
