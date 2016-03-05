package com.cameronleger.datadumper.model;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BlockData {
    private Integer id;
    private String uniqueId;
    private Block block;
    private List<ItemStack> subBlocks;

    public BlockData(Integer id, String uniqueId, Block block, List<ItemStack> subBlocks) {
        setId(id);
        setUniqueId(uniqueId);
        setBlock(block);
        setSubBlocks(subBlocks);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public List<ItemStack> getSubBlocks() {
        return subBlocks;
    }

    public void setSubBlocks(List<ItemStack> subBlocks) {
        this.subBlocks = subBlocks;
    }
}
