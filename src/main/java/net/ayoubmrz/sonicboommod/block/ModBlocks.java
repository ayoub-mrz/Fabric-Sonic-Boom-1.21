package net.ayoubmrz.sonicboommod.block;

import net.ayoubmrz.sonicboommod.SonicBoomMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModBlocks {

    public static final Block EGLE_STATUE = registerBlock("egle_statue",
            properties -> new EgleStatueBlock(properties.nonOpaque()));

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SonicBoomMod.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(SonicBoomMod.MOD_ID, name), toRegister);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(SonicBoomMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey().rarity(Rarity.EPIC).maxCount(1)
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SonicBoomMod.MOD_ID, name)))));
    }

    public static void registerModBlocks() {
        SonicBoomMod.LOGGER.info("Registering Mod Blocks for " + SonicBoomMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.EGLE_STATUE);
        });
    }
}