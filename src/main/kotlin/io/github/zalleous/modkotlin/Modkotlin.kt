package io.github.zalleous.modkotlin

import net.fabricmc.api.ModInitializer

object Modkotlin : ModInitializer {
    override fun onInitialize() {
        ModItems.register()
        ModBlocks.register()
        println("modkotlin initialized: registered \${ModItems::class.java.simpleName} & \${ModBlocks::class.java.simpleName}")
    }
}