package io.github.zalleous.modkotlin.util

/* ─────────────── imports ─────────────── */
import net.minecraft.item.equipment.ArmorMaterial
import net.minecraft.item.equipment.EquipmentAsset
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.item.Item
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier

/* ─────────── custom registry key ─────────── */
/**  registry root  →  minecraft:equipment_assets  */
private val EQUIPMENT_ASSETS_REGISTRY: RegistryKey<Registry<EquipmentAsset>> =
    RegistryKey.ofRegistry(Identifier.of("minecraft", "equipment_assets"))

/* ─────────────── builder ─────────────── */

class ArmorMaterialBuilder private constructor(
    private var durability: Int,
    private val defence: MutableMap<EquipmentType, Int>,
) {
    private var enchantability      = 15
    private var equipSound: RegistryEntry<SoundEvent> =
        SoundEvents.ITEM_ARMOR_EQUIP_GENERIC
    private var toughness           = 0f
    private var knockbackResistance = 0f
    private var repairTag: TagKey<Item> =
        TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "iron_ingots"))

    /** Defaults to the vanilla “generic” equipment asset. */
    private var assetKey: RegistryKey<EquipmentAsset> =
        RegistryKey.of(EQUIPMENT_ASSETS_REGISTRY, Identifier.of("minecraft", "generic"))

    /* ---- fluent setters ---- */

    fun enchantability(value: Int)                  = apply { enchantability       = value }
    fun equipSound(entry: RegistryEntry<SoundEvent>)= apply { equipSound          = entry }
    fun toughness(value: Float)                     = apply { toughness            = value }
    fun knockbackRes(value: Float)                  = apply { knockbackResistance  = value }
    fun repairTag(id: Identifier) = apply {
        repairTag = TagKey.of(RegistryKeys.ITEM, id)
    }
    fun asset(id: Identifier) = apply {
        assetKey = RegistryKey.of(EQUIPMENT_ASSETS_REGISTRY, id)
    }

    /* ---- build ---- */
    fun build(): ArmorMaterial =
        ArmorMaterial(
            durability,
            defence.toMap(),
            enchantability,
            equipSound,
            toughness,
            knockbackResistance,
            repairTag,
            assetKey
        )

    /* ---- entry point ---- */
    companion object {
        /**
         * durability = multiplier applied to the vanilla base values
         * head/chest/legs/feet = protection amounts for each slot
         */
        fun start(
            durability: Int,
            head: Int, chest: Int, legs: Int, feet: Int
        ) = ArmorMaterialBuilder(
            durability,
            mutableMapOf(
                EquipmentType.HELMET     to head,
                EquipmentType.CHESTPLATE to chest,
                EquipmentType.LEGGINGS   to legs,
                EquipmentType.BOOTS      to feet
            )
        )
    }
}
