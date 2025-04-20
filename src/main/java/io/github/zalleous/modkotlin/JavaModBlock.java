package io.github.zalleous.modkotlin;

import com.google.common.base.Function;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.awt.geom.RectangularShape;


public class JavaModBlock {
    public static final Block CONDENSED_DIRT = register(
            "condensed_dirt",
            Block::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS),
            true
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        // Crée un clé d'enregistrement pour le bloc
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Crée le bloc
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        // Si l'on doit enregistrer un item pour ce bloc
        if (shouldRegisterItem) {
            // Crée la clé d'enregistrement de l'item
            RegistryKey<Item> itemKey = keyOfItem(name);

            // Crée un BlockItem associé
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));

            // Enregistre l'item dans le registre
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        // Enregistre le bloc dans le registre
        return Registry.register(Registries.BLOCK, blockKey, block);
    }


    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("modkotlin", name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of("modkotlin", name));
    }

    public static void initialise() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((itemGroup) -> {
            itemGroup.add(CONDENSED_DIRT.asItem());
        });
    }

}
