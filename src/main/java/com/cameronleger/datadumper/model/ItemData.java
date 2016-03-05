package com.cameronleger.datadumper.model;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemData {
    private Integer id;
    private String uniqueId;
    private Item item;
    private List<ItemStack> subItems;

    public ItemData(Integer id, String uniqueId, Item item, List<ItemStack> subItems) {
        setId(id);
        setUniqueId(uniqueId);
        setItem(item);
        setSubItems(subItems);
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<ItemStack> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<ItemStack> subItems) {
        this.subItems = subItems;
    }
}
