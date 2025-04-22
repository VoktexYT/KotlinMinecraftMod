package io.github.zalleous.modkotlin

import net.minecraft.item.Item
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.registry.RegistryKey
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups

object ModItems {
    lateinit var exampleItem: Item
    lateinit var magicGem: Item

    /**
     * Call during initialization to register all items
     */
    fun register() {
        exampleItem = RegistryHelper.registerItem(
            name = "example_item",
            settingsBuilder = { maxCount(64) },
            creativeTabKey = ItemGroups.COMBAT
        )

        magicGem = RegistryHelper.registerItem(
            name = "magic_gem",
            settingsBuilder = { maxCount(16) },
            creativeTabKey = ItemGroups.INGREDIENTS
        )
    }
}