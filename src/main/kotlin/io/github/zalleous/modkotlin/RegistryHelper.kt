package io.github.zalleous.modkotlin

import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings as BlockSettings
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings as ItemSettings
import net.minecraft.item.Items
import net.minecraft.item.ItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.equipment.ArmorMaterial
import net.minecraft.item.EquipmentItem
import net.minecraft.item.equipment.EquipmentType

/**
 * Helper for streamlined registration of Items and Blocks.
 */
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

    /**
     * Registers a full 4-piece armor set (helmet, chestplate, leggings, boots)
     * made from the supplied [material].
     *
     * @param setName        base registry name, e.g. `"mythril"`
     * @param material       the ArmorMaterial to use
     * @param creativeTabKey optional creative-tab key (defaults to COMBAT)
     *
     * @return a Map<Type, ArmorItem> for further use
     */
    data class RegisteredArmor(
        val material: net.minecraft.item.equipment.ArmorMaterial,
        val items   : Map<EquipmentType, Item>
    )

    fun register(
        // material properties
        materialName    : String,
        materialBuilder : ArmorMaterialBuilder.() -> Unit,

        // item properties
        baseItemName    : String,                 // e.g. "copper"
        itemSettings    : ItemSettings = ItemSettings(),

        // creative tab
        tabKey          : RegistryKey<net.minecraft.item.ItemGroup>? = null
    ): RegisteredArmor {

        /* ───── 1. build / register the material ───── */
        val materialId = Identifier.of(RegistryHelper.MOD_ID, materialName)
        val materialKey = RegistryKey.of(
            RegistryKeys.ARMOR_MATERIAL,
            materialId
        )

        val material = ArmorMaterialBuilder
            .start( /* supply your defaults here */ 15, 2, 5, 4, 2)
            .apply(materialBuilder)         // ← user-supplied tweaks
            .build()

        Registry.register(Registries.ARMOR_MATERIAL, materialId, material)

        /* ───── 2. register the four wearable items ───── */
        val result = mutableMapOf<EquipmentType, Item>()

        fun registerPiece(type: EquipmentType, suffix: String) {
            val id  = Identifier.of(RegistryHelper.MOD_ID, "${baseItemName}_$suffix")
            val key = RegistryKey.of(RegistryKeys.ITEM, id)

            val item = Item(
                ItemSettings()
                    .equipmentType(type)  // ← tells the game what slot
                    .armorMaterial(materialKey)
                    .let { s ->
                        // copy caller’s extra settings (maxCount, rarity…)
                        itemSettings.copyFrom(s)
                    }
                    .registryKey(key)
            )

            Registry.register(Registries.ITEM, id, item)
            result[type] = item

            tabKey?.let { tab ->
                ItemGroupEvents.modifyEntriesEvent(tab)
                    .register { it.add(item) }
            }
        }

        registerPiece(EquipmentType.HELMET,     "helmet")
        registerPiece(EquipmentType.CHESTPLATE, "chestplate")
        registerPiece(EquipmentType.LEGGINGS,   "leggings")
        registerPiece(EquipmentType.BOOTS,      "boots")

        return RegisteredArmor(material, result)
    }
}