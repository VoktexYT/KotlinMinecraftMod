package org.kotlinmod.test;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.world.entity.EntityType;

@Mod("test")
public class Test {
    // Créer un logger pour afficher des messages dans la console
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();

        // Affiche un message dans la console
        System.out.println("Le joueur a cliqué droit sur un bloc !");

        // Affiche également un message dans le chat du joueur
        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.literal("Vous avez cliqué droit sur un bloc !"), true);
        }
    }
    public Test() {
        // Affiche un message lors de l'initialisation du mod
        LOGGER.info("Le mod a été chargé avec succès!");
        MinecraftForge.EVENT_BUS.register(this);
    }
}
