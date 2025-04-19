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
            "example_item",
            ItemSettings().maxCount(64),
            creativeTabKey = ItemGroups.COMBAT
        )

        magicGem = RegistryHelper.registerItem(
            "magic_gem",
            ItemSettings().maxCount(16),
            creativeTabKey = ItemGroups.INGREDIENTS
        )
    }
}