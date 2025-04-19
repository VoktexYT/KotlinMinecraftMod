package io.github.zalleous.modkotlin

import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings as BlockSettings
import net.minecraft.block.MapColor
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.registry.RegistryKey
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups

object ModBlocks {
    lateinit var stoneBlock: Block
    lateinit var oreBlock: Block

    /**
     * Call during initialization to register all blocks
     */
    fun register() {
        stoneBlock = RegistryHelper.registerBlock(
            name = "custom_stone",
            blockSettingsBuilder = { mapColor(MapColor.STONE_GRAY).strength(4.0f) },
            itemSettings = ItemSettings().maxCount(64),
            creativeTabKey = ItemGroups.BUILDING_BLOCKS
        )

        oreBlock = RegistryHelper.registerBlock(
            name = "custom_ore",
            blockSettingsBuilder = { mapColor(MapColor.RED).strength(3.0f).luminance { 7 } },
            itemSettings = ItemSettings().maxCount(64),
            creativeTabKey = ItemGroups.BUILDING_BLOCKS
        )
    }
}