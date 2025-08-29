package net.ayoubmrz.sonicboommod.block;

import net.ayoubmrz.sonicboommod.SonicBoomMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModBlocks {

    public static final Block EGLE_STATUE = registerBlock("egle_statue",
            new EgleStatueBlock(AbstractBlock.Settings.create().nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(SonicBoomMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(SonicBoomMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    }

    public static void registerModBlocks() {
        SonicBoomMod.LOGGER.info("Registering Mod Blocks for " + SonicBoomMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.EGLE_STATUE);
        });
    }
}