package io.github.zalleous.modkotlin

import net.fabricmc.fabric.api.networking.v1.S2CConfigurationChannelEvents
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.PillarBlock
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup

object ModItems {
    lateinit var customBlock: Block

    fun customBlock() {
        customBlock = RegistryHelper.registerBlock(
            name = "newblock",
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
        customBlock()
    }
}