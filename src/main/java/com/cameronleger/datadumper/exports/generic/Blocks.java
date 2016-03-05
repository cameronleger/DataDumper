package com.cameronleger.datadumper.exports.generic;

import com.cameronleger.datadumper.exports.IExport;
import com.cameronleger.datadumper.model.BlockData;
import net.minecraft.block.Block;
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

public class Blocks implements IExport {

    private List<BlockData> blockDataList;

    public Blocks(List<BlockData> blockDataList) {
        this.blockDataList = blockDataList;
    }

    public String getFileName() {
        return "blocks.xml";
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

        Element blocksElement = doc.createElement("blocks");
        doc.appendChild(blocksElement);

        for (BlockData blockData : blockDataList) {
            Element blockElement = doc.createElement("block");
            blocksElement.appendChild(blockElement);

            Element idElement = doc.createElement("id");
            idElement.appendChild(doc.createTextNode(String.valueOf(blockData.getId())));
            blockElement.appendChild(idElement);

            Block block = blockData.getBlock();

            Element uniqueElement = doc.createElement("unique");
            uniqueElement.appendChild(doc.createTextNode(blockData.getUniqueId()));
            blockElement.appendChild(uniqueElement);

            Element nameElement = doc.createElement("name");
            nameElement.appendChild(doc.createTextNode(block.getUnlocalizedName()));
            blockElement.appendChild(nameElement);

            Element localNameElement = doc.createElement("local_name");
            localNameElement.appendChild(doc.createTextNode(block.getLocalizedName()));
            blockElement.appendChild(localNameElement);

            Element texturesElement = doc.createElement("textures");
            blockElement.appendChild(texturesElement);

            String[] sides = new String[] {"top", "bottom", "north", "south", "east", "west"};
            for (int index = 0; index < sides.length; index++) {
                IIcon icon = block.getIcon(index, 0);
                if (icon != null) {
                    Element textureSideElement = doc.createElement(sides[index]);
                    textureSideElement.appendChild(doc.createTextNode(icon.getIconName()));
                    texturesElement.appendChild(textureSideElement);
                }
            }

            List<ItemStack> subBlocks = blockData.getSubBlocks();
            if (!subBlocks.isEmpty()) {
                Element subBlocksElement = doc.createElement("subblocks");
                blockElement.appendChild(subBlocksElement);
                for(ItemStack subBlock : subBlocks) {
                    Element subBlockElement = doc.createElement("subblock");
                    subBlocksElement.appendChild(subBlockElement);

                    Element subNameElement = doc.createElement("name");
                    subNameElement.appendChild(doc.createTextNode(subBlock.getUnlocalizedName()));
                    subBlockElement.appendChild(subNameElement);

                    Element subDisplayNameElement = doc.createElement("display_name");
                    subDisplayNameElement.appendChild(doc.createTextNode(subBlock.getDisplayName()));
                    subBlockElement.appendChild(subDisplayNameElement);

                    Element subDamageElement = doc.createElement("damage");
                    subDamageElement.appendChild(doc.createTextNode(String.valueOf(subBlock.getItemDamage())));
                    subBlockElement.appendChild(subDamageElement);

                    Element subTexturesElement = doc.createElement("textures");
                    subBlockElement.appendChild(subTexturesElement);

                    for (int index = 0; index < sides.length; index++) {
                        IIcon icon = block.getIcon(index, subBlock.getItemDamage());
                        if (icon != null) {
                            Element textureSideElement = doc.createElement(sides[index]);
                            textureSideElement.appendChild(doc.createTextNode(icon.getIconName()));
                            subTexturesElement.appendChild(textureSideElement);
                        }
                    }
                }
            }
        }

        return new DOMSource(doc);
    }
}
