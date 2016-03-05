package com.cameronleger.datadumper.exports.generic;

import com.cameronleger.datadumper.exports.IExport;
import com.cameronleger.datadumper.model.ItemData;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.util.List;

public class Items implements IExport {

    private List<ItemData> itemDataList;

    public Items(List<ItemData> itemDataList) {
        this.itemDataList = itemDataList;
    }

    public String getFileName() {
        return "items.xml";
    }

    public Source export() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("Unable to create a new Document");
            e.printStackTrace();
            return null;
        }

        Document doc = docBuilder.newDocument();

        Element blocksElement = doc.createElement("items");
        doc.appendChild(blocksElement);

        for (ItemData itemData : itemDataList) {
            Element blockElement = doc.createElement("item");
            blocksElement.appendChild(blockElement);

            Element idElement = doc.createElement("id");
            idElement.appendChild(doc.createTextNode(String.valueOf(itemData.getId())));
            blockElement.appendChild(idElement);

            Item item = itemData.getItem();

            Element uniqueElement = doc.createElement("unique");
            uniqueElement.appendChild(doc.createTextNode(itemData.getUniqueId()));
            blockElement.appendChild(uniqueElement);

            Element nameElement = doc.createElement("name");
            nameElement.appendChild(doc.createTextNode(item.getUnlocalizedName()));
            blockElement.appendChild(nameElement);

            List<ItemStack> subItems = itemData.getSubItems();
            if (!subItems.isEmpty()) {
                Element subBlocksElement = doc.createElement("subitems");
                blockElement.appendChild(subBlocksElement);
                for(ItemStack subItem : subItems) {
                    Element subBlockElement = doc.createElement("subitem");
                    subBlocksElement.appendChild(subBlockElement);

                    Element subNameElement = doc.createElement("name");
                    subNameElement.appendChild(doc.createTextNode(subItem.getUnlocalizedName()));
                    subBlockElement.appendChild(subNameElement);

                    Element subDisplayNameElement = doc.createElement("display_name");
                    subDisplayNameElement.appendChild(doc.createTextNode(subItem.getDisplayName()));
                    subBlockElement.appendChild(subDisplayNameElement);

                    Element subDamageElement = doc.createElement("damage");
                    subDamageElement.appendChild(doc.createTextNode(String.valueOf(subItem.getItemDamage())));
                    subBlockElement.appendChild(subDamageElement);

                    TextureAtlasSprite subItemTexture = (TextureAtlasSprite) subItem.getIconIndex();

                    Element subTextureElement = doc.createElement("texture");
                    subTextureElement.appendChild(doc.createTextNode(subItemTexture.getIconName()));
                    subBlockElement.appendChild(subTextureElement);

                }
            }
        }

        return new DOMSource(doc);
    }
}
