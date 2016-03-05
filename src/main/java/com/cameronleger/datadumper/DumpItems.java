package com.cameronleger.datadumper;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DumpItems {

    public static List<String> getAllItemInfo()
    {
        System.out.println("Getting Item Info");
        List<String> info = new ArrayList<String>();

        FMLControlledNamespacedRegistry<Item> itemsDiscovered = GameData.getItemRegistry();
        Iterator<Item> itemIterator = itemsDiscovered.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();

            info.add("<item>");

            info.add(String.format("  <id>%s</id>", itemsDiscovered.getId(item)));
            info.add(String.format("  <name>%s</name>", item.getUnlocalizedName()));
            info.add(String.format("  <unique>%s</unique>", GameRegistry.findUniqueIdentifierFor(item)));

            List<ItemStack> subitems = new ArrayList<ItemStack>();
            item.getSubItems(item, null, subitems);
            for (ItemStack subitem : subitems) {
                info.add("  <subitem>");
                info.add(String.format("    <name>%s</name>", subitem.getUnlocalizedName()));
                info.add(String.format("    <displayname>%s</displayname>", subitem.getDisplayName()));

                TextureAtlasSprite subitemTex = (TextureAtlasSprite) subitem.getIconIndex();
                info.add(String.format("    <icon>%s</icon>", subitemTex.getIconName()));

                info.add(String.format("    <damage>%s</damage>", subitem.getItemDamage()));
                if (subitem.getItemDamage() != subitem.getItemDamageForDisplay())
                    info.add(String.format("    <displaydamage>%s</displaydamage>", subitem.getItemDamageForDisplay()));

                info.add("  </subitem>");
            }

            info.add("</item>");
        }

        return info;
    }
}
