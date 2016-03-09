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

        private static final int BLOCK_FLOWER = 1;
        private static final int BLOCK_TORCH = 2;
        private static final int BLOCK_FIRE = 3;
        private static final int BLOCK_FLUID = 4;
        private static final int BLOCK_REDSTONE_WIRE = 5;
        private static final int BLOCK_CROPS = 6;
        private static final int BLOCK_DOOR = 7;
        private static final int BLOCK_LADDER = 8;
        private static final int BLOCK_MINECART_TRACK = 9;
        private static final int BLOCK_STAIRS = 10;
        private static final int BLOCK_FENCE = 11;
        private static final int BLOCK_LEVER = 12;
        private static final int BLOCK_CACTUS = 13;
        private static final int BLOCK_BED = 14;
        private static final int BLOCK_REDSTONE_REPEATER = 15;
        private static final int BLOCK_PISTON_BASE = 16;
        private static final int BLOCK_PISTON_EXT = 17;
        private static final int BLOCK_PANE = 18;
        private static final int BLOCK_STEM = 19;
        private static final int BLOCK_VINE = 20;
        private static final int BLOCK_FENCE_GATE = 21;
        private static final int BLOCK_LILYPAD = 23;
        private static final int BLOCK_CAULDRON = 24;
        private static final int BLOCK_BREWING_STAND = 25;
        private static final int BLOCK_END_PORTAL_FRAME = 26;
        private static final int BLOCK_DRAGON_EGG = 27;
        private static final int BLOCK_COCOA = 28;
        private static final int BLOCK_TRIPWIRE_SRC = 29;
        private static final int BLOCK_TRIPWIRE = 30;
        private static final int BLOCK_LOG = 31;
        private static final int BLOCK_WALL = 32;
        private static final int BLOCK_FLOWER_POT = 33;
        private static final int BLOCK_BEACON = 34;
        private static final int BLOCK_ANVIL = 35;
        private static final int BLOCK_REDSTONE_DIODE = 36;
        private static final int BLOCK_REDSTONE_COMPARATOR = 37;
        private static final int BLOCK_HOPPER = 38;
        private static final int BLOCK_QUARTZ = 39;
        private static final int BLOCK_DOUBLE_PLANT = 40;
        private static final int BLOCK_STAINED_GLASS = 41;

        // Biomes-o-Plenty (perhaps not always unique?
        private static final int BLOCK_BOP_FLOWER1 = 50;
        private static final int BLOCK_BOP_FLOWER2 = 51;
        private static final int BLOCK_BOP_DOUBLE_PLANT = 54;
        private static final int BLOCK_BOP_FLUID = 42;

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

                Map<String, String> properties = getAdditionalProperties(blockData);
                if (!properties.isEmpty()) {
                    for (String key : properties.keySet()) {
                        Helpers.addTextElement(doc, key, properties.get(key), blockElement);
                    }

                    // flower pots have a more complex system
                    if (blockData.getBlock().getRenderType() == BLOCK_FLOWER_POT) {
                        Element meshRootElement = doc.createElement("mesh");
                        String[] fp = new String[]{
                                "fp_empty",
                                "fp_red_fl",
                                "fp_yellow_fl",
                                "fp_oak_sapl",
                                "fp_spruce_sapl",
                                "fp_birch_sapl",
                                "fp_jungle_sapl",
                                "fp_red_mush",
                                "fp_brown_mush",
                                "fp_cactus",
                                "fp_dead_bush",
                                "fp_fern"
                        };
                        for (int index = 0; index < fp.length; index++) {
                            Element meshElement = doc.createElement("mesh");
                            meshElement.setAttribute("data", String.valueOf(index));
                            meshElement.appendChild(doc.createTextNode(
                                    String.format("models/flowerpot.obj#%s", fp[index])));
                            meshRootElement.appendChild(meshElement);
                        }
                        blockElement.appendChild(meshRootElement);
                    }

                    // anvils have a more complex system
                    if (blockData.getBlock().getRenderType() == BLOCK_ANVIL) {
                        Element meshRootElement = doc.createElement("mesh");

                        Element meshElement = doc.createElement("mesh");
                        meshElement.setAttribute("data", "0");
                        meshElement.setAttribute("mask", "1");
                        meshElement.appendChild(doc.createTextNode("models/anvil.obj"));
                        meshRootElement.appendChild(meshElement);

                        meshElement = doc.createElement("mesh");
                        meshElement.setAttribute("data", "1");
                        meshElement.setAttribute("mask", "1");
                        Element rotateElement = doc.createElement("rotate");
                        rotateElement.setAttribute("data", "1");
                        rotateElement.setAttribute("mask", "1");
                        Helpers.addTextElement(doc, "mesh", "models/anvil.obj", rotateElement);
                        meshElement.appendChild(rotateElement);
                        meshRootElement.appendChild(meshElement);

                        blockElement.appendChild(meshRootElement);
                    }

                    if (blockData.getBlock().getRenderType() == BLOCK_END_PORTAL_FRAME) {
                        Element meshRootElement = doc.createElement("mesh");

                        Element meshElement = doc.createElement("mesh");
                        meshElement.appendChild(doc.createTextNode("models/endportal_frame.obj#base"));
                        meshRootElement.appendChild(meshElement);

                        meshElement = doc.createElement("mesh");
                        meshElement.setAttribute("data", "4");
                        meshElement.setAttribute("mask", "4");
                        meshElement.appendChild(doc.createTextNode("models/endportal_frame.obj#eye"));
                        meshRootElement.appendChild(meshElement);

                        blockElement.appendChild(meshRootElement);
                    }
                }

                // condense all textures into one data-less material if possible
                Map<Integer, String> damageTextureMap = new HashMap<Integer, String>();
                for (ItemStack subBlock : subBlocks) {
                    List<String> texturesList = TextureTools.getTexturesList(block, subBlock.getItemDamage());
                    if (texturesList != null && !texturesList.isEmpty()) {
                        damageTextureMap.put(subBlock.getItemDamage(), String.join(", ", texturesList));
                    }
                }
                Set<String> uniqueTextures = new HashSet<String>(damageTextureMap.values());
                if (uniqueTextures.size() == 1) {
                    Helpers.addTextElement(doc, "materials",
                            TextureTools.createUniqueTextureName(uniqueTextures.iterator().next()), blockElement);

                // for naturally data-less blocks, just add the one element
                } else if (subBlocks.size() <= 1) {
                    List<String> texturesList = TextureTools.getTexturesList(block);
                    if (texturesList != null && !texturesList.isEmpty()) {
                        Helpers.addTextElement(doc, "materials",
                                TextureTools.createUniqueTextureName(String.join(", ", texturesList)), blockElement);
                    }

                // for data-rich blocks, loop over and add the data attribute
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

                // some blocks might not have related blocks, so add one with no metadata
                if (subBlocks.isEmpty()) {
                    for (String textureName : TextureTools.getTexturesList(block)) {
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
                } else {
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
            }

            for (String textureName : textures.keySet()) {
                Texture texture = textures.get(textureName);
                List<TextureReference> textureReferences = texture.getTextureReferences();

                Element fileElement = doc.createElement("file");
                fileElement.setAttribute("name", texture.getFileName());
                if (textureReferences.size() <= 1) {
                    fileElement.setAttribute("cols", "1");
                    // double chests are 128x64 and jMc2Obj will crash unless this is set
                    if (texture.getFileName().contains("normal_double")) {
                        fileElement.setAttribute("rows", "1");
                    } else {
                        fileElement.setAttribute("rows", "*");
                    }
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

    private static Map<String, String> getAdditionalProperties(BlockData block) {
        Map<String, String> properties = new HashMap<String, String>();

        String uniqueId = block.getUniqueId();
        if (uniqueId.equalsIgnoreCase("minecraft:grass") ||
                uniqueId.equalsIgnoreCase("minecraft:dirt")) {
            properties.put("model", "DirtGrass");
        }

        if (uniqueId.equalsIgnoreCase("minecraft:chest")) {
            properties.put("model", "Chest");
        }

        if (uniqueId.equalsIgnoreCase("minecraft:stone_button") ||
                uniqueId.equalsIgnoreCase("minecraft:wooden_button")) {
            properties.put("model", "Button");
        }

        if (uniqueId.equalsIgnoreCase("minecraft:snow_layer")) {
            properties.put("model", "Snow");
        }

        switch (block.getBlock().getRenderType()) {
            case Blocks.BLOCK_TORCH:
                properties.put("model", "Torch");
                break;
            case Blocks.BLOCK_FIRE:
                properties.put("model", "Fire");
                break;
            case Blocks.BLOCK_BOP_FLUID:
            case Blocks.BLOCK_FLUID:
                properties.put("model", "Liquid");
                break;
            case Blocks.BLOCK_REDSTONE_WIRE:
                properties.put("model", "RedstoneWire");
                break;
            case Blocks.BLOCK_FLOWER:
            case Blocks.BLOCK_CROPS:
            case Blocks.BLOCK_BOP_FLOWER1:
            case Blocks.BLOCK_BOP_FLOWER2:
                properties.put("model", "Cross");
                break;
            case Blocks.BLOCK_DOOR:
                properties.put("model", "Door");
                break;
            case Blocks.BLOCK_LADDER:
                properties.put("model", "Ladder");
                break;
            case Blocks.BLOCK_MINECART_TRACK:
                properties.put("model", "Rails");
                break;
            case Blocks.BLOCK_STAIRS:
                properties.put("model", "Stairs");
                break;
            case Blocks.BLOCK_FENCE:
                properties.put("model", "Fence");
                break;
            case Blocks.BLOCK_LEVER:
                properties.put("model", "Lever");
                break;
            case Blocks.BLOCK_CACTUS:
                properties.put("model", "Cactus");
                break;
            case Blocks.BLOCK_BED:
                properties.put("model", "Bed");
                break;
            case Blocks.BLOCK_REDSTONE_REPEATER:
                properties.put("model", "RedstoneRepeater");
                break;
            case Blocks.BLOCK_PISTON_BASE:
                properties.put("model", "PistonBase");
                break;
            case Blocks.BLOCK_PISTON_EXT:
                properties.put("model", "PistonArm");
                break;
            case Blocks.BLOCK_PANE:
                properties.put("model", "Pane");
                break;
            case Blocks.BLOCK_STEM:
                properties.put("model", "Stalk");
                break;
            case Blocks.BLOCK_VINE:
                properties.put("model", "Vines");
                break;
            case Blocks.BLOCK_FENCE_GATE:
                properties.put("model", "FenceGate");
                break;
            case Blocks.BLOCK_LILYPAD:
                properties.put("model", "Lilypad");
                break;
            case Blocks.BLOCK_CAULDRON:
                properties.put("model", "Mesh");
                properties.put("mesh", "models/cauldron.obj");
                break;
            case Blocks.BLOCK_BREWING_STAND:
                properties.put("model", "Mesh");
                properties.put("mesh", "models/brewing_stand.obj");
                break;
            case Blocks.BLOCK_END_PORTAL_FRAME:
                properties.put("model", "Mesh");
                // the nested mesh happens elsewhere
                break;
            case Blocks.BLOCK_DRAGON_EGG:
                properties.put("model", "Mesh");
                properties.put("mesh", "models/enderdragon_egg.obj");
                break;
            case Blocks.BLOCK_COCOA:
                properties.put("model", "CocoaPlant");
                break;
            case Blocks.BLOCK_TRIPWIRE_SRC:
                properties.put("model", "TripwireHook");
                break;
            case Blocks.BLOCK_TRIPWIRE:
                properties.put("model", "Tripwire");
                break;
            case Blocks.BLOCK_LOG:
                properties.put("model", "WoodLog");
                break;
            case Blocks.BLOCK_WALL:
                properties.put("model", "Wall");
                break;
            case Blocks.BLOCK_FLOWER_POT:
                properties.put("model", "Mesh");
                // the nested mesh happens elsewhere
                break;
            case Blocks.BLOCK_BEACON:
                properties.put("model", "Mesh");
                properties.put("mesh", "models/beacon.obj");
                break;
            case Blocks.BLOCK_ANVIL:
                properties.put("model", "Mesh");
                // the nested mesh happens elsewhere
                break;
            case Blocks.BLOCK_REDSTONE_DIODE: // unused? but possibly a repeater
            case Blocks.BLOCK_REDSTONE_COMPARATOR:
                properties.put("model", "RedstoneRepeater");
                break;
            case Blocks.BLOCK_HOPPER:
                properties.put("model", "Mesh");
                properties.put("mesh", "models/hopper.obj");
                break;
            case Blocks.BLOCK_QUARTZ:
                properties.put("model", "Quartz");
                break;
            case Blocks.BLOCK_DOUBLE_PLANT:
            case Blocks.BLOCK_BOP_DOUBLE_PLANT:
                properties.put("model", "DoublePlant");
                break;
            case Blocks.BLOCK_STAINED_GLASS: // normal block
                break;
            default:
                break;
        }

        return properties;
    }

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
            List<String> textures = new ArrayList<String>();
            int renderType = block.getRenderType();

            // dirt and grass need slight customization
            try {
                if (block.getUnlocalizedName().equalsIgnoreCase("tile.grass")) {
                    textures.add("grass_top");
                    textures.add("grass_side");
                    textures.add("grass_side_snowed");
                    textures.add("dirt");
                    return textures;
                }
            } catch (Exception e) {
                // oh well
            }
            try {
                if (block.getUnlocalizedName().equalsIgnoreCase("tile.dirt")) {
                    switch (damage) {
                        case 0:
                            textures.add("dirt");
                            textures.add("dirt");
                            textures.add("dirt");
                            textures.add("dirt");
                            break;
                        case 1:
                            textures.add("dirt");
                            textures.add("dirt");
                            textures.add("dirt");
                            textures.add("dirt");
                            break;
                        case 2:
                            textures.add("dirt_podzol_top");
                            textures.add("dirt_podzol_side");
                            textures.add("grass_side_snowed");
                            textures.add("dirt");
                            break;
                        default:
                            // nothing after this in normal jMc2Obj config, but let's try dirt
                            textures.add("dirt");
                            break;
                    }
                    return textures;
                }
            } catch (Exception e) {
                // oh well
            }

            // chests don't specify the right thing at all, do it manually
            try {
                if (block.getUnlocalizedName().equalsIgnoreCase("tile.chest")) {
                    textures.add("chest/normal");
                    textures.add("chest/normal_double");
                    return textures;
                }
            } catch (Exception e) {
                // oh well
            }

            // piston blocks don't specify the right thing at all, do it manually
            if (renderType == Blocks.BLOCK_PISTON_BASE) {
                textures.add("piston_top_normal");
                textures.add("piston_top_normal");
                textures.add("piston_side");
                textures.add("piston_bottom");
                return textures;
            }
            if (renderType == Blocks.BLOCK_PISTON_EXT) {
                textures.add("piston_top_normal");
                textures.add("piston_top_sticky");
                textures.add("piston_side");
                return textures;
            }

            // redstone wires don't specify the right thing at all, do it manually
            if (renderType == Blocks.BLOCK_REDSTONE_WIRE) {
                textures.add("redstone_dust_cross");
                textures.add("redstone_dust_cross_overlay");
                textures.add("redstone_dust_line");
                textures.add("redstone_dust_line_overlay");
                return textures;
            }

            // rails don't specify the right thing at all, do it manually
            if (renderType == Blocks.BLOCK_MINECART_TRACK) {
                textures.add("rail_normal");
                textures.add("rail_normal_turned");
                return textures;
            }

            // beds don't specify the right thing at all, do it manually
            if (renderType == Blocks.BLOCK_BED) {
                textures.add("bed_head_top");
                textures.add("bed_head_side");
                textures.add("bed_head_end");
                textures.add("bed_feet_top");
                textures.add("bed_feet_side");
                textures.add("bed_feet_end");
                return textures;
            }

            // check for all sides and add them if they appear
            // note, it's unknown whether the sides are named correctly, but the order should be correct
            String[] sides = new String[] {"bottom", "top", "north", "south", "east", "west"};
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

            // some modded blocks don't have textures setup
            if (allTextures.isEmpty()) {
                return textures;
            }

            // jMc2Obj uses top, side, side, side, side, bottom
            // so we have to move the bottom to the end
            if (allTextures.size() == 6) {
                allTextures.add(allTextures.remove(0));
            }

            // redstone repeaters need an additional material for the sides
            if (textures.size() > 0 &&
                    (renderType == Blocks.BLOCK_REDSTONE_REPEATER || renderType == Blocks.BLOCK_REDSTONE_COMPARATOR)) {
                textures.add(1, "stone_slab_side");
                return textures;
            }

            // short-form for having the same material on all sides
            if (new HashSet<String>(allTextures).size() == 1) {
                textures.add(allTextures.get(0));

                // but if it was liquid, it must have two
                if (renderType == Blocks.BLOCK_FLUID || renderType == Blocks.BLOCK_BOP_FLUID) {
                    textures.add(textures.get(0));
                }

                // door blocks don't specify the top texture, but the Door model needs it
                // so we do a little replacement
                if (renderType == Blocks.BLOCK_DOOR) {
                    textures.add(0, allTextures.get(0).replace("bottom", "top").replace("lower", "upper"));
                }

                // lever blocks don't specify the base texture, but the Lever model needs it
                if (renderType == Blocks.BLOCK_LEVER) {
                    textures.add("cobblestone");
                }

                // pane blocks don't specify the side texture, but the Pane model needs it
                if (renderType == Blocks.BLOCK_PANE) {
                    textures.add(textures.get(0));
                }

                // stem blocks don't specify the connected texture, but the Stalk model needs it
                if (renderType == Blocks.BLOCK_STEM) {
                    textures.add(0, textures.get(0).replace("dis", ""));
                }

                // cocoa blocks don't specify the other stage texture, but the Cocoa model needs it
                if (renderType == Blocks.BLOCK_COCOA) {
                    textures.add(0, textures.get(0).replace("2", "1"));
                    textures.add(0, textures.get(0).replace("1", "0"));
                }

                // tripwire source blocks don't specify the other part textures, but the TripwireHook model needs it
                if (renderType == Blocks.BLOCK_TRIPWIRE_SRC) {
                    textures.add(textures.get(0).replace("_source", ""));
                    textures.add(0, "planks_oak");
                }

                // some woodlog blocks don't specify the side part textures, but the WoodLog model needs it
                if (renderType == Blocks.BLOCK_LOG) {
                    textures.add(textures.get(0));
                }

                // some doubleplant blocks don't specify the top textures, but the DoublePlant model needs it
                if (renderType == Blocks.BLOCK_DOUBLE_PLANT || renderType == Blocks.BLOCK_BOP_DOUBLE_PLANT) {
                    textures.add(textures.get(0).replace("bottom", "top"));
                }

                return textures;
            }

            // short-form for having different materials for top/bottom and sides
            if (allTextures.get(0).equals(allTextures.get(5)) &&
                    allTextures.get(1).equals(allTextures.get(2)) &&
                    allTextures.get(1).equals(allTextures.get(3)) &&
                    allTextures.get(1).equals(allTextures.get(4))) {
                textures.add(allTextures.get(0));
                textures.add(allTextures.get(1));
                return textures;
            }

            // short-form for having different materials for top and bottom and sides
            if (allTextures.get(1).equals(allTextures.get(2)) &&
                    allTextures.get(1).equals(allTextures.get(3)) &&
                    allTextures.get(1).equals(allTextures.get(4))) {
                textures.add(allTextures.get(0));
                textures.add(allTextures.get(1));
                textures.add(allTextures.get(5));
                return textures;
            }

            // something different for most sides
            return allTextures;
        }

        private static String convertBlockTextureNameToPath(String texture) {
            if (texture != null && texture.startsWith("chest/")) {
                return convertTextureNameToPath(texture, "entity");
            }
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
