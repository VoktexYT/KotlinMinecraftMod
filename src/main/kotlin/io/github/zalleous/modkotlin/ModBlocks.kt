package io.github.zalleous.modkotlin

import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.item.ItemGroups
import net.minecraft.sound.BlockSoundGroup

object ModBlocks {
    lateinit var soundBlock: Block
    lateinit var customBlock: Block
    lateinit var luminanceBlock: Block

    fun soundBlock() {
        soundBlock = RegistryHelper.registerBlock(
            name = "soundblock",
            blockSettingsBuilder = {
                mapColor(MapColor.BLACK)
                    .strength(0.05f)
                    .sounds(BlockSoundGroup.CHAIN)
            },
            itemSettings = ItemSettings().maxCount(64),
            creativeTabKey = ItemGroups.BUILDING_BLOCKS
        )
    }

    fun luminanceBlock() {
        luminanceBlock = RegistryHelper.registerBlock(
            name = "luminanceblock",
            blockSettingsBuilder = {
                mapColor(MapColor.BLACK)
                    .strength(0.05f)
                    .luminance { 15 }
            },
            itemSettings = ItemSettings().maxCount(64),
            creativeTabKey = ItemGroups.BUILDING_BLOCKS
        )
    }

    fun customBlock() {
        customBlock = RegistryHelper.registerBlock(
            name = "customblock",
            blockSettingsBuilder = {
                mapColor(MapColor.BLACK)
                    .strength(0.05f)
            },
            itemSettings = ItemSettings().maxCount(64),
            creativeTabKey = ItemGroups.BUILDING_BLOCKS
        )
    }

    /**
     * Call during initialization to register all blocks
     */
    fun register() {
        soundBlock()
        customBlock()
        luminanceBlock()
    }
}