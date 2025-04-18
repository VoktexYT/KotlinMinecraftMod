package io.github.zalleous.modkotlin.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text

class ModkotlinClient : ClientModInitializer {

    fun leftClickEvent(callback: (ClientPlayerEntity) -> Unit) {
        ClientTickEvents.END_CLIENT_TICK.register { client: MinecraftClient ->
            val player = client.player ?: return@register

            if (client.currentScreen == null && client.options.attackKey.isPressed) {
                callback(player)
            }
        }
    }

    fun rightClickEvent(callback: (ClientPlayerEntity) -> Unit) {
        ClientTickEvents.END_CLIENT_TICK.register { client: MinecraftClient ->
            val player = client.player ?: return@register

            if (client.currentScreen == null && client.options.useKey.isPressed) {
                callback(player)
            }
        }
    }

    fun sendMessageTo(player: ClientPlayerEntity, text: String) {
        player.sendMessage(Text.of(text), false)
    }


    override fun onInitializeClient() {
        leftClickEvent { player -> sendMessageTo(player, "LEFT") }
        rightClickEvent { player -> sendMessageTo(player, "RIGHT") }
    }
}
