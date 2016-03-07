package com.cameronleger.datadumper.exports;

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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class Jmc2Obj {
    public static class Blocks implements IExport {

        private List<BlockData> blockDataList;

        public Blocks(List<BlockData> blockDataList) {
            this.blockDataList = blockDataList;
        }

        public String getFileName() {
            return "blocks.conf";
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
                if (blockData.getId() == 0) {
                    continue; // skip air
                }

                Block block = blockData.getBlock();
                List<ItemStack> subBlocks = blockData.getSubBlocks();

                Element blockElement = doc.createElement("block");
                blockElement.setAttribute("id", String.valueOf(blockData.getId()));

                // the first subblock (damage=0) usually has the best name
                if (!subBlocks.isEmpty()) {
                    try {
                        blockElement.setAttribute("name", subBlocks.get(0).getDisplayName());
                    } catch (Exception e) {
                        try {
                            blockElement.setAttribute("name", String.valueOf(block.getUnlocalizedName()));
                        } catch (Exception e2) {
                            System.out.println(String.format("Unable to get name for block %s", blockData.getId()));
                            blockElement.setAttribute("name", "EXCEPTION");
                        }
                    }
                } else {
                    try {
                        blockElement.setAttribute("name", String.valueOf(block.getUnlocalizedName()));
                    } catch (Exception e) {
                        System.out.println(String.format("Unable to get name for block %s", blockData.getId()));
                        blockElement.setAttribute("name", "EXCEPTION");
                    }
                }

                blocksElement.appendChild(blockElement);

                if (subBlocks.size() <= 1) {
                    List<String> texturesList = TextureTools.getTexturesList(block);
                    if (texturesList != null && !texturesList.isEmpty()) {
                        for (int index = 0; index < texturesList.size(); index++) {
                            texturesList.set(index, TextureTools.createUniqueTextureName(texturesList.get(index)));
                        }
                        try {
                            Helpers.addTextElement(doc, "materials", String.join(", ", texturesList), blockElement);
                        } catch (Exception e) {
                            System.out.println(String.format("Unable to get materials for block %s",
                                    blockData.getId()));
                            Helpers.addTextElement(doc, "materials", "EXCEPTION", blockElement);
                        }
                    }
                } else {
                    Map<Integer, String> damageTextureMap = new HashMap<Integer, String>();
                    for (ItemStack subBlock : subBlocks) {
                        List<String> texturesList = TextureTools.getTexturesList(block, subBlock.getItemDamage());
                        if (texturesList != null && !texturesList.isEmpty()) {
                            damageTextureMap.put(subBlock.getItemDamage(), String.join(", ", texturesList));
                        }
                    }
                    // condense all textures into one data-less material
                    Set<String> uniqueTextures = new HashSet<String>(damageTextureMap.values());
                    if (uniqueTextures.size() == 1) {
                        Helpers.addTextElement(doc, "materials",
                                TextureTools.createUniqueTextureName(uniqueTextures.iterator().next()), blockElement);
                    } else {
                        for (int damage = 0; damage < 16; damage++) {
                            if (damageTextureMap.containsKey(damage)) {
                                Element materialElement = Helpers.addTextElement(doc, "materials",
                                        TextureTools.createUniqueTextureName(damageTextureMap.get(damage)),
                                        blockElement);
                                materialElement.setAttribute("data", String.valueOf(damage));
                            }
                        }
                    }
                }
            }

            return new DOMSource(doc);
        }
    }

    public static class TexSplit implements IExport {

        private List<BlockData> blockDataList;

        public TexSplit(List<BlockData> blockDataList) {
            this.blockDataList = blockDataList;
        }

        public String getFileName() {
            return "texsplit_1.6.conf";
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

            Element texsplitElement = doc.createElement("texsplit");
            doc.appendChild(texsplitElement);

            Map<String, Texture> textures = new ConcurrentSkipListMap<String, Texture>();
            for (BlockData blockData : blockDataList) {
                if (blockData.getId() == 0) {
                    continue; // skip air
                }

                Block block = blockData.getBlock();
                List<ItemStack> subBlocks = blockData.getSubBlocks();

                for (ItemStack subBlock : subBlocks) {
                    for (String textureName : TextureTools.getTexturesList(block, subBlock.getItemDamage())) {
                        String textureFilePath = TextureTools.convertBlockTextureNameToPath(textureName);
                        Texture texture;
                        if (textures.containsKey(textureFilePath)) {
                            texture = textures.get(textureFilePath);
                        } else {
                            texture = new Texture(textureFilePath);
                        }
                        texture.addTextureReference(new TextureReference(textureName,
                                TextureTools.getColumnFromTextureName(textureName)));
                        textures.put(textureFilePath, texture);
                    }
                }
            }

            for (String textureName : textures.keySet()) {
                Texture texture = textures.get(textureName);
                List<TextureReference> textureReferences = texture.getTextureReferences();

                Element fileElement = doc.createElement("file");
                fileElement.setAttribute("name", texture.getFileName());
                if (textureReferences.size() <= 1) {
                    fileElement.setAttribute("cols", "1");
                    fileElement.setAttribute("rows", "*");
                } else {
                    int cols = 1;
                    int rows = 1;
                    for (TextureReference textureReference : textureReferences) {
                        cols = Math.max(cols, textureReference.getColumn());
                        rows = Math.max(rows, textureReference.getRow());
                    }
                    fileElement.setAttribute("cols", String.valueOf(cols));
                    fileElement.setAttribute("rows", String.valueOf(rows));
                }
                fileElement.setAttribute("source", "texturepack");
                texsplitElement.appendChild(fileElement);

                if (textureReferences.size() <= 1) {
                    Element texElement = doc.createElement("tex");
                    texElement.setAttribute("name",
                            TextureTools.createUniqueTextureName(textureReferences.get(0).getTextureName()));
                    fileElement.appendChild(texElement);
                } else {
                    for (TextureReference textureReference : textureReferences) {
                        Element texElement = doc.createElement("tex");
                        texElement.setAttribute("name",
                                TextureTools.createUniqueTextureName(textureReference.getTextureName()));
                        texElement.setAttribute("pos", String.format("%s,%s",
                                textureReference.getRow(), textureReference.getColumn()));
                        fileElement.appendChild(texElement);
                    }
                }
            }

            return new DOMSource(doc);
        }
    }

    // TODO: texture names (for IC2) can end with :0 where 0 is the column of the texture file they're in
    private static class Texture {
        private String fileName;
        private List<TextureReference> textureReferences;

        public Texture(String fileName) {
            setFileName(fileName);
            setTextureReferences(new ArrayList<TextureReference>());
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public List<TextureReference> getTextureReferences() {
            return textureReferences;
        }

        public void setTextureReferences(List<TextureReference> textureReferences) {
            this.textureReferences = textureReferences;
        }

        public void addTextureReference(TextureReference textureReference) {
            for (TextureReference existingTexRef : textureReferences) {
                if (existingTexRef.getTextureName().equals(textureReference.getTextureName())) {
                    return;
                }
            }
            this.textureReferences.add(textureReference);
        }
    }

    private static class TextureReference {
        private String textureName;
        private int column;
        private int row;

        public TextureReference(String textureName, int column) {
            setTextureName(textureName);
            setColumn(column);
            setRow(1);
        }

        public String getTextureName() {
            return textureName;
        }

        public void setTextureName(String textureName) {
            this.textureName = textureName;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    private static class TextureTools {
        private static List<String> getTexturesList(Block block) {
            return getTexturesList(block, 0);
        }

        private static List<String> getTexturesList(Block block, int damage) {
            List<String> allTextures = new ArrayList<String>();
            String[] sides = new String[] {"top", "bottom", "north", "south", "east", "west"};
            for (int index = 0; index < sides.length; index++) {
                try {
                    IIcon icon = block.getIcon(index, damage);
                    if (icon != null) {
                        allTextures.add(icon.getIconName());
                    }
                } catch (Exception e) {
                    System.out.println(String.format("Unable to get texture for block %s side %s",
                            block.getLocalizedName(), index));
                    allTextures.add("EXCEPTION");
                }
            }
            List<String> textures = new ArrayList<String>();
            // some modded blocks don't have textures setup
            if (allTextures.isEmpty()) {
                return textures;
            }
            // short-form for having the same material on all sides
            if (new HashSet<String>(allTextures).size() == 1) {
                textures.add(allTextures.get(0));
                return textures;
            }
            // short-form for having different materials for top/bottom and sides
            if (allTextures.get(0).equals(allTextures.get(1)) &&
                    allTextures.get(2).equals(allTextures.get(3)) &&
                    allTextures.get(2).equals(allTextures.get(4)) &&
                    allTextures.get(2).equals(allTextures.get(5))) {
                textures.add(allTextures.get(0));
                textures.add(allTextures.get(2));
                return textures;
            }
            // short-form for having different materials for top and bottom and sides
            if (allTextures.get(2).equals(allTextures.get(3)) &&
                    allTextures.get(2).equals(allTextures.get(4)) &&
                    allTextures.get(2).equals(allTextures.get(5))) {
                textures.add(allTextures.get(0));
                textures.add(allTextures.get(2));
                textures.add(allTextures.get(1));
                return textures;
            }
            // something different for most sides
            return allTextures;
        }

        private static String convertBlockTextureNameToPath(String texture) {
            return convertTextureNameToPath(texture, "blocks");
        }

        private static String convertTextureNameToPath(String texture, String type) {
            if (texture == null) {
                return null;
            }
            String pathTemplate = "assets/%s/textures/%s/%s.png";
            if (texture.contains(":")) {
                String[] textureSplit = texture.split(":", 2);
                String modId = textureSplit[0];
                String texturePath = textureSplit[1];
                // assume the other : is at the end such as blockWithSideIndex:0
                if (texturePath.contains(":")) {
                    texturePath = texturePath.split(":")[0];
                }
                return String.format(pathTemplate, modId.toLowerCase(), type, texturePath);
            } else {
                return String.format(pathTemplate, "minecraft", type, texture);
            }
        }

        private static String createUniqueTextureName(String texture) {
            return texture.replaceAll("[:/.]", "_");
        }

        private static int getColumnFromTextureName(String texture) {
            if (texture == null || !texture.contains(":")) {
                return 0;
            }
            String[] textureSplit = texture.split(":", 2);
            if (!textureSplit[1].contains(":")) {
                return 0;
            }
            try {
                textureSplit = textureSplit[1].split(":");
                return Integer.parseInt(textureSplit[textureSplit.length - 1]) + 1;
            } catch (Exception e) {
                System.out.println(String.format("Unable to parse column number from texture %s", texture));
                return 0;
            }
        }
    }

}
