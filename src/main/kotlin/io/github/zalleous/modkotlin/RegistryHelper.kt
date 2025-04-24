package io.github.zalleous.modkotlin

import io.github.zalleous.modkotlin.util.ArmorMaterialBuilder
import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings as BlockSettings
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.item.Items
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.item.equipment.ArmorMaterial
import net.minecraft.item.EquipmentItem
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent

object RegistryHelper {
    const val MOD_ID = "modkotlin"

    /**
     * Registers an Item with the given name, settings, and optional creative tab key.
     */
    fun registerItem(
        name: String,
        settingsBuilder: ItemSettings.() -> ItemSettings = { this },
        creativeTabKey: RegistryKey<ItemGroup>? = null
    ): Item {
        val id = Identifier.of(MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.ITEM, id)
        val settings = ItemSettings()
            .registryKey(key)
            .settingsBuilder()
        val item = Item(settings)
        Registry.register(Registries.ITEM, id, item)
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
        val id = Identifier.of(MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.BLOCK, id)
        val bs = BlockSettings.create()
            .registryKey(key)
            .blockSettingsBuilder()
        val block = Block(bs)
        Registry.register(Registries.BLOCK, id, block)
        val blockItem = Items.register(block, itemSettings)
        creativeTabKey?.let { tabKey ->
            ItemGroupEvents.modifyEntriesEvent(tabKey)
                .register { entries ->
                    entries.add(block)
                    entries.add(blockItem)
                }
        }
        return block
    }

    /**
     * Holds the registered armor material and its four armor items.
     */
    data class RegisteredArmor(
        val material: ArmorMaterial,
        val items: Map<EquipmentType, Item>
    )

    /**
     * DSL entry point for registering a full 4-piece armor set.
     *
     * Example:
     * val dragonArmor = RegistryHelper.registerArmor("dragon") {
     *     material(15, 2, 5, 4, 2)
     *     toughness(3.0f)
     *     enchantability(10)
     * }
     */
    fun registerArmor(
        name: String,
        creativeTabKey: RegistryKey<ItemGroup> = ItemGroups.COMBAT,
        block: ArmorMaterialDsl.() -> Unit
    ): RegisteredArmor {
        val dsl = ArmorMaterialDsl(name, creativeTabKey)
        dsl.block()
        return dsl.register()
    }

    class ArmorMaterialDsl(
        private val name: String,
        private val creativeTabKey: RegistryKey<ItemGroup>
    ) {
        private var builder: ArmorMaterialBuilder? = null

        /** Initialize base durability multiplier and per-piece defenses. */
        fun material(durability: Int, helmet: Int, chestplate: Int, leggings: Int, boots: Int) {
            builder = ArmorMaterialBuilder.start(durability, helmet, chestplate, leggings, boots)
        }

        fun enchantability(value: Int) = apply { builder?.enchantability(value) }
        fun equipSound(entry: RegistryEntry<SoundEvent>) = apply { builder?.equipSound(entry) }
        fun toughness(value: Float) = apply { builder?.toughness(value) }
        fun knockbackResistance(value: Float) = apply { builder?.knockbackRes(value) }
        fun repairTag(id: Identifier) = apply { builder?.repairTag(id) }
        fun asset(id: Identifier) = apply { builder?.asset(id) }

        internal fun register(): RegisteredArmor {
            val b = builder
                ?: throw IllegalStateException("Call material(...) before registering armor!")
            val material = b.build()

            // Register the ArmorMaterial
            val matId = Identifier.of(MOD_ID, name)
            val matKey = RegistryKey.of(RegistryKeys.ARMOR_MATERIAL, matId)
            Registry.register(Registries.ARMOR_MATERIAL, matId, material)

            // Register the four armor items
            val items = mutableMapOf<EquipmentType, Item>()
            fun registerPiece(type: EquipmentType, suffix: String) {
                val itemId = Identifier.of(MOD_ID, "$name_$suffix")
                val itemKey = RegistryKey.of(RegistryKeys.ITEM, itemId)
                val settings = ItemSettings()
                    .equipmentType(type)
                    .armorMaterial(matKey)
                    .registryKey(itemKey)
                val armorItem = EquipmentItem(settings)
                Registry.register(Registries.ITEM, itemId, armorItem)
                items[type] = armorItem
                ItemGroupEvents.modifyEntriesEvent(creativeTabKey)
                    .register { entries -> entries.add(armorItem) }
            }

            registerPiece(EquipmentType.HELMET, "helmet")
            registerPiece(EquipmentType.CHESTPLATE, "chestplate")
            registerPiece(EquipmentType.LEGGINGS, "leggings")
            registerPiece(EquipmentType.BOOTS, "boots")

            return RegisteredArmor(material, items)
        }
    }
}
