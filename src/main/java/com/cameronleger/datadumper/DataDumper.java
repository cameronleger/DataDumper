package com.cameronleger.datadumper;


import com.cameronleger.datadumper.exports.Generic;
import com.cameronleger.datadumper.exports.IExport;
import com.cameronleger.datadumper.exports.Jmc2Obj;
import com.cameronleger.datadumper.model.BlockData;
import com.cameronleger.datadumper.model.ItemData;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Mod(modid= DataDumper.MOD_ID, name="Block and Item Data Dumper", version= DataDumper.VERSION)
public class DataDumper {

    public static final String MOD_ID = "datadumper";
    public static final String VERSION = "1.0.0";

    // The instance of your mod that Forge uses.
    @Instance(DataDumper.MOD_ID)
    public static DataDumper instance;

    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        File dump = new File("dump");
        if (!dump.exists()) {
            if (!dump.mkdir()) {
                System.out.println("Unable to create dump directory that didn't exist");
                return;
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        List<BlockData> blocks = Data.blocksWithData();
        List<ItemData> items = Data.itemsWithData();

        List<IExport> exporters = new ArrayList<IExport>();
        exporters.add(new Generic.Blocks(blocks));
        exporters.add(new Generic.Items(items));
        exporters.add(new Jmc2Obj.Blocks(blocks));
        exporters.add(new Jmc2Obj.TexSplit(blocks));

        for (IExport exporter : exporters) {
            String fileName = exporter.getFileName();
            System.out.println(String.format("Dumping Info to %s", fileName));
            StreamResult result = new StreamResult(new File(dump, fileName));

            Source export = exporter.export();
            if (export == null) {
                System.out.println("Dump failed");
                continue;
            }

            try {
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(export, result);
            } catch (TransformerConfigurationException e) {
                System.out.println("Unable to create a new XML Transformer");
                e.printStackTrace();
            } catch (TransformerException e) {
                System.out.println("Unable to transform XML into file");
                e.printStackTrace();
            }
        }
    }
}
