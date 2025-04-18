package io.github.zalleous.modkotlin.datagen     // keep this in your *client* (or dedicated “datagen”) source‑set

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider   // ← correct package! :contentReference[oaicite:0]{index=0}
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.client.data.BlockStateModelGenerator                        // client‑side class in 1.21.5
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

@Environment(EnvType.CLIENT)               // datagen always runs with the client classes available
object ModkotlinDataGenerator : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()

        /* 1) providers whose constructor takes only FabricDataOutput */
        pack.addProvider(::ModModelProvider)

        /* 2) providers that ALSO need the registry‑future (tags, loot tables, etc.) */
        pack.addProvider { output: FabricDataOutput, registries: CompletableFuture<RegistryWrapper.WrapperLookup> ->
            ModBlockTagProvider(output, registries)
        }
    }
}

/* ---------- block‑state + item‑model generation ---------- */
class ModModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
        // gen.registerSimpleCubeAll(ModBlocks.EXAMPLE_BLOCK)
    }

    override fun generateItemModels(gen: ItemModelGenerator) {
        // gen.register(ModItems.EXAMPLE_ITEM, Models.GENERATED)
    }
}

/* ---------- block‑tags ---------- */
class ModBlockTagProvider(
    output: FabricDataOutput,
    registries: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.BlockTagProvider(output, registries) {

    override fun configure(lookup: RegistryWrapper.WrapperLookup) {
        // val mineable = tag(BlockTags.PICKAXE_MINEABLE)
        // mineable.add(ModBlocks.EXAMPLE_BLOCK)
    }
}
