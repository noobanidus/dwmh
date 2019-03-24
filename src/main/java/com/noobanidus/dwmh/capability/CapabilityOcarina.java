package com.noobanidus.dwmh.capability;

import com.noobanidus.dwmh.items.ItemOcarina;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityOcarina implements INBTSerializable<NBTTagCompound> {
    private ItemOcarina.Mode main;
    private ItemOcarina.Mode sneak;

    public ItemOcarina.Mode getMain() {
        return main;
    }

    public void setMain(ItemOcarina.Mode main) {
        this.main = main;
    }

    public ItemOcarina.Mode getSneak() {
        return sneak;
    }

    public void setSneak(ItemOcarina.Mode sneak) {
        this.sneak = sneak;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound result = new NBTTagCompound();
        if (this.main == null) this.main = ItemOcarina.Mode.LIST;
        if (this.sneak == null) this.sneak = ItemOcarina.Mode.SUMMON;

        result.setShort("main", (short) this.main.ordinal());
        result.setShort("sneak", (short) this.sneak.ordinal());

        return result;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.main = ItemOcarina.Mode.fromOrdinal(nbt.getShort("main"));
        this.main = ItemOcarina.Mode.fromOrdinal(nbt.getShort("sneak"));
    }
}

