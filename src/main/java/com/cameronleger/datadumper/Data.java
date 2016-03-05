package com.cameronleger.datadumper;

import com.cameronleger.datadumper.model.BlockData;
import com.cameronleger.datadumper.model.ItemData;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class Data {

    public static List<BlockData> blocksWithData()
    {
        // retrieve the current blocks from FML
        FMLControlledNamespacedRegistry<Block> blocksDiscovered = GameData.getBlockRegistry();
        Iterator<Block> blockIterator = blocksDiscovered.iterator();

        List<BlockData> blocksWithData = new ArrayList<BlockData>();
        while(blockIterator.hasNext()) {
            Block block = blockIterator.next();
            List<ItemStack> subBlocks = new ArrayList<ItemStack>();

            // convert the block to an item to check for metadata
            Item blockItem = Item.getItemFromBlock(block);
            if (blockItem != null) {
                blockItem.getSubItems(blockItem, null, subBlocks); // subBlocks = output
            }

            blocksWithData.add(new BlockData(blocksDiscovered.getId(block),
                    String.valueOf(GameRegistry.findUniqueIdentifierFor(block)),
                    block, subBlocks));
        }

        return blocksWithData;
    }

    public static List<ItemData> itemsWithData()
    {
        // retrieve the current items from FML
        FMLControlledNamespacedRegistry<Item> itemsDiscovered = GameData.getItemRegistry();
        Iterator<Item> itemIterator = itemsDiscovered.iterator();

        List<ItemData> itemsWithData = new ArrayList<ItemData>();
        while(itemIterator.hasNext()) {
            Item item = itemIterator.next();
            List<ItemStack> subItems = new ArrayList<ItemStack>();
            item.getSubItems(item, null, subItems); // subItems = output
            itemsWithData.add(new ItemData(itemsDiscovered.getId(item),
                    String.valueOf(GameRegistry.findUniqueIdentifierFor(item)),
                    item, subItems));
        }

        return itemsWithData;
    }
}
