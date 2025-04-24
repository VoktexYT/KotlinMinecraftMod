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
import net.minecraft.item.equipment.EquipmentItem
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent

object RegistryHelper {
    const val MOD_ID = "modkotlin"

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
        creativeTabKey?.let { tab ->
            ItemGroupEvents.modifyEntriesEvent(tab)
                .register { it.add(item) }
        }
        return item
    }

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
        creativeTabKey?.let { tab ->
            ItemGroupEvents.modifyEntriesEvent(tab)
                .register { entries ->
                    entries.add(block)
                    entries.add(blockItem)
                }
        }
        return block
    }

    data class RegisteredArmor(
        val material: ArmorMaterial,
        val items: Map<EquipmentType, Item>
    )

    fun registerArmor(
        name: String,
        creativeTabKey: RegistryKey<ItemGroup> = ItemGroups.COMBAT,
        configure: ArmorDsl.() -> Unit
    ): RegisteredArmor {
        val dsl = ArmorDsl(name, creativeTabKey)
        dsl.configure()
        return dsl.build()
    }

    class ArmorDsl(
        private val name: String,
        private val creativeTabKey: RegistryKey<ItemGroup>
    ) {
        private var builder: ArmorMaterialBuilder? = null

        fun material(durability: Int, helmet: Int, chestplate: Int, leggings: Int, boots: Int) = apply {
            builder = ArmorMaterialBuilder.start(durability, helmet, chestplate, leggings, boots)
        }
        fun enchantability(value: Int) = apply { builder?.enchantability(value) }
        fun equipSound(entry: RegistryEntry<SoundEvent>) = apply { builder?.equipSound(entry) }
        fun toughness(value: Float) = apply { builder?.toughness(value) }
        fun knockbackResistance(value: Float) = apply { builder?.knockbackRes(value) }
        fun repairTag(id: Identifier) = apply { builder?.repairTag(id) }
        fun asset(id: Identifier) = apply { builder?.asset(id) }

        internal fun build(): RegisteredArmor {
            val b = builder ?: throw IllegalStateException("Call material(...) before build()")
            val material = b.build()
            val matId = Identifier.of(MOD_ID, name)
            val matKey = RegistryKey.of(RegistryKeys.ARMOR_MATERIAL, matId)
            Registry.register(Registries.ARMOR_MATERIAL, matId, material)

            val items = mutableMapOf<EquipmentType, Item>()
            fun regPiece(type: EquipmentType, suffix: String) {
                val itemId = Identifier.of(MOD_ID, "${name}_$suffix")
                val itemKey = RegistryKey.of(RegistryKeys.ITEM, itemId)
                val settings = ItemSettings()
                    .equipmentType(type)
                    .armorMaterial(matKey)
                    .registryKey(itemKey)
                val armorItem = EquipmentItem(settings)
                Registry.register(Registries.ITEM, itemId, armorItem)
                ItemGroupEvents.modifyEntriesEvent(creativeTabKey)
                    .register { it.add(armorItem) }
                items[type] = armorItem
            }

            regPiece(EquipmentType.HELMET, "helmet")
            regPiece(EquipmentType.CHESTPLATE, "chestplate")
            regPiece(EquipmentType.LEGGINGS, "leggings")
            regPiece(EquipmentType.BOOTS, "boots")

            return RegisteredArmor(material, items)
        }
    }
}