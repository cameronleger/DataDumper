package com.cameronleger.datadumper.exports;

import com.cameronleger.datadumper.model.BlockData;
import com.cameronleger.datadumper.model.ItemData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.util.List;

public class Generic {
    public static class Blocks implements IExport {

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

                try {
                    Helpers.addTextElement(doc, "id", String.valueOf(blockData.getId()), blockElement);
                } catch (Exception e) {
                    System.out.println("Unable to get id for block");
                    // not much else we can do then...
                    continue;
                }

                Block block = blockData.getBlock();

                try {
                    Helpers.addTextElement(doc, "unique", blockData.getUniqueId(), blockElement);
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get unique for block %s", blockData.getId()));
                    Helpers.addTextElement(doc, "unique", "EXCEPTION", blockElement);
                }

                try {
                    Helpers.addTextElement(doc, "name", block.getUnlocalizedName(), blockElement);
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get name for block %s", blockData.getId()));
                    Helpers.addTextElement(doc, "name", "EXCEPTION", blockElement);
                }

                try {
                    Helpers.addTextElement(doc, "local_name", block.getLocalizedName(), blockElement);
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get local_name for block %s", blockData.getId()));
                    Helpers.addTextElement(doc, "local_name", "EXCEPTION", blockElement);
                }

                try {
                    if (block.getRenderType() > 41)
                        Helpers.addTextElement(doc, "render_type", String.valueOf(block.getRenderType()), blockElement);
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get render_type for block %s", blockData.getId()));
                    Helpers.addTextElement(doc, "render_type", "EXCEPTION", blockElement);
                }

                Element texturesElement = doc.createElement("textures");
                blockElement.appendChild(texturesElement);

                String[] sides = new String[] {"top", "bottom", "north", "south", "east", "west"};
                for (int index = 0; index < sides.length; index++) {
                    try {
                        IIcon icon = block.getIcon(index, 0);
                        if (icon != null) {
                            Element textureSideElement = doc.createElement(sides[index]);
                            textureSideElement.appendChild(doc.createTextNode(icon.getIconName()));
                            texturesElement.appendChild(textureSideElement);
                        }
                    } catch (Exception e) {
                        System.out.println(String.format("Unable to get texture for block %s:0 side %s",
                                blockData.getId(), index));
                    }
                }

                List<ItemStack> subBlocks = blockData.getSubBlocks();
                if (!subBlocks.isEmpty()) {
                    Element subBlocksElement = doc.createElement("subblocks");
                    blockElement.appendChild(subBlocksElement);
                    for(ItemStack subBlock : subBlocks) {
                        Element subBlockElement = doc.createElement("subblock");
                        subBlocksElement.appendChild(subBlockElement);

                        try {
                            Helpers.addTextElement(doc, "damage",
                                    String.valueOf(subBlock.getItemDamage()), subBlockElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get damage for block %s",
                                    blockData.getId()));
                            // not much else we can do...
                            continue;
                        }

                        try {
                            Helpers.addTextElement(doc, "name", subBlock.getUnlocalizedName(), subBlockElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get name for block %s:%s",
                                    blockData.getId(), subBlock.getItemDamage()));
                            Helpers.addTextElement(doc, "name", "EXCEPTION", subBlockElement);
                        }

                        try {
                            Helpers.addTextElement(doc, "display_name", subBlock.getDisplayName(), subBlockElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get display_name for block %s:%s",
                                    blockData.getId(), subBlock.getItemDamage()));
                            Helpers.addTextElement(doc, "display_name", "EXCEPTION", subBlockElement);
                        }

                        Element subTexturesElement = doc.createElement("textures");
                        subBlockElement.appendChild(subTexturesElement);

                        for (int index = 0; index < sides.length; index++) {
                            try {
                                IIcon icon = block.getIcon(index, subBlock.getItemDamage());
                                if (icon != null) {
                                    Element textureSideElement = doc.createElement(sides[index]);
                                    textureSideElement.appendChild(doc.createTextNode(icon.getIconName()));
                                    subTexturesElement.appendChild(textureSideElement);
                                }
                            } catch (Exception e) {
                                System.out.println(String.format("Unable to get texture for block %s:%s side %s",
                                        blockData.getId(), subBlock.getItemDamage(), index));
                            }
                        }
                    }
                }
            }

            return new DOMSource(doc);
        }
    }


    public static class Items implements IExport {

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
                Element itemElement = doc.createElement("item");
                blocksElement.appendChild(itemElement);

                try {
                    Helpers.addTextElement(doc, "id", String.valueOf(itemData.getId()), itemElement);
                } catch (Exception e) {
                    System.out.println("Unable to get id for item");
                    // not much else we can do then...
                    continue;
                }

                Item item = itemData.getItem();

                try {
                    Helpers.addTextElement(doc, "unique", itemData.getUniqueId(), itemElement);
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get unique for item %s", itemData.getId()));
                    Helpers.addTextElement(doc, "unique", "EXCEPTION", itemElement);
                }

                try {
                    Helpers.addTextElement(doc, "name", item.getUnlocalizedName(), itemElement);
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get name for item %s", itemData.getId()));
                    Helpers.addTextElement(doc, "name", "EXCEPTION", itemElement);
                }

                List<ItemStack> subItems = itemData.getSubItems();
                if (!subItems.isEmpty()) {
                    Element subBlocksElement = doc.createElement("subitems");
                    itemElement.appendChild(subBlocksElement);
                    for(ItemStack subItem : subItems) {
                        Element subItemElement = doc.createElement("subitem");
                        subBlocksElement.appendChild(subItemElement);

                        try {
                            Helpers.addTextElement(doc, "damage",
                                    String.valueOf(subItem.getItemDamage()), subItemElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get damage for item %s",
                                    itemData.getId()));
                            // not much else we can do...
                            continue;
                        }

                        try {
                            Helpers.addTextElement(doc, "name", subItem.getUnlocalizedName(), subItemElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get name for item %s", itemData.getId()));
                            Helpers.addTextElement(doc, "name", "EXCEPTION", itemElement);
                        }

                        try {
                            Helpers.addTextElement(doc, "display_name", subItem.getDisplayName(), subItemElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get display_name for item %s",
                                    itemData.getId()));
                            Helpers.addTextElement(doc, "display_name", "EXCEPTION", itemElement);
                        }


                        try {
                            TextureAtlasSprite subItemTexture = (TextureAtlasSprite) subItem.getIconIndex();

                            Element subTextureElement = doc.createElement("texture");
                            subTextureElement.appendChild(doc.createTextNode(subItemTexture.getIconName()));
                            subItemElement.appendChild(subTextureElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get texture for item %s:0s",
                                    itemData.getId()));
                        }
                    }
                }
            }

            return new DOMSource(doc);
        }
    }
}
