package com.noobanidus.dwmh.config;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabDWMH extends CreativeTabs {
    public CreativeTabDWMH(int id, String id2) {
        super(id, id2);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(Registrar.ocarina);
    }
}
