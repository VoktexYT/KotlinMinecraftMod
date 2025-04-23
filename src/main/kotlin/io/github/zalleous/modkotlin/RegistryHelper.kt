package io.github.zalleous.modkotlin

import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings as BlockSettings
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.item.Items
import net.minecraft.item.ItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.mob.PathAwareEntity

/**
 * Helper for streamlined registration of Items and Blocks.
 */
object RegistryHelper {
    const val MOD_ID = "modkotlin"

    inline fun <reified T : PathAwareEntity> registerMobEntity(
        name: String,
        spawnGroup: SpawnGroup = SpawnGroup.CREATURE,
        dimensions: EntityDimensions = EntityDimensions.fixed(0.75f, 0.75f),
        noinline factory: (EntityType<T>, net.minecraft.world.World) -> T,
        attributes: () -> DefaultAttributeContainer.Builder
    ): EntityType<T> {
        val id = Identifier.of(RegistryHelper.MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)

        val type = FabricEntityTypeBuilder.create(spawnGroup, factory)
            .dimensions(dimensions)
            .build(key)

        Registry.register(Registries.ENTITY_TYPE, id, type)
        FabricDefaultAttributeRegistry.register(type, attributes())

        return type
    }

    /**
     * Registers an Item with the given name, settings, and optional creative tab key.
     */
    fun registerItem(
        name: String,
        settingsBuilder: ItemSettings.() -> ItemSettings = { this },
        creativeTabKey: RegistryKey<ItemGroup>? = null
    ): Item {
        // 1. Build the Identifier and RegistryKey for your item
        val id = Identifier.of(MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.ITEM, id)

        // 2. Apply registryKey to settings *before* creating the Item
        val settings = ItemSettings()
            .registryKey(key)
            .settingsBuilder()

        // 3. Construct your Item and register it
        val item = Item(settings)
        Registry.register(Registries.ITEM, id, item)

        // 4. Optionally add to a creative tab
        creativeTabKey?.let { tabKey ->
            ItemGroupEvents.modifyEntriesEvent(tabKey)
                .register { entries -> entries.add(item) }
        }

        return item
    }


    /**
     * Registers a Block and its BlockItem with the given name,
     * block settings builder, item settings, and optional creative tab key.
     */
    fun registerBlock(
        name: String,
        blockSettingsBuilder: BlockSettings.() -> BlockSettings = { this },
        itemSettings: ItemSettings = ItemSettings(),
        creativeTabKey: RegistryKey<ItemGroup>? = null
    ): Block {
        // create identifier and block registry key
        val id = Identifier.of(MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.BLOCK, id)

        // build block settings including the registry key
        val bs = BlockSettings.create()
            .registryKey(key)
            .blockSettingsBuilder()

        // instantiate and register block
        val block = Block(bs)
        Registry.register(Registries.BLOCK, id, block)

        // instantiate and register BlockItem using the block's key
        val blockItem = Items.register(block, itemSettings)

        // add both block and blockItem to creative tab if provided
        creativeTabKey?.let { tabKey ->
            ItemGroupEvents.modifyEntriesEvent(tabKey)
                .register { entries ->
                    entries.add(block)
                    entries.add(blockItem)
                }
        }

        return block
    }
}