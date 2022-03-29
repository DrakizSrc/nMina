package me.drakiz.plugins.nminas.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemConverter extends ItemStack {

    private ItemStack item;
    private int id;
    private int amount;
    private short data;

    public ItemConverter(int id, short data, int amount) {
        this.id = id;
        this.data = data;
        this.amount = amount;
        this.item = new ItemStack(Material.getMaterial(id), amount, data);
    }

    public ItemStack getItem() {
        return item;
    }
}
