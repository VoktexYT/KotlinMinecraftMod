package io.github.zalleous.modkotlin

import net.fabricmc.api.ModInitializer

class Modkotlin : ModInitializer {
    override fun onInitialize() {
        ModItems.register()
        println("modkotlin initialized: registered \${ModItems::class.java.simpleName} & \${ModBlocks::class.java.simpleName}")
    }
}
