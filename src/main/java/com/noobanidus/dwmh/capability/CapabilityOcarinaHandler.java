package com.noobanidus.dwmh.capability;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityOcarinaHandler implements ICapabilitySerializable<NBTTagCompound> {
    public static final String CAPABILITY_NAME = "ocarina_capability";

    public final static ResourceLocation IDENTIFIER = new ResourceLocation(DWMH.MODID, CAPABILITY_NAME);

    @CapabilityInject(CapabilityOcarina.class)
    public static Capability<CapabilityOcarina> INSTANCE = null;
    private final CapabilityOcarina capability;

    public CapabilityOcarinaHandler() {
        this.capability = new CapabilityOcarina();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == INSTANCE;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == INSTANCE) {
            return INSTANCE.cast(this.capability);
        }

        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        capability.deserializeNBT(nbt);
    }


    public static class NBT_Tags {
        public final static String OCARINA = "ocarina";
    }

    public static class CapabilityNameStorage implements Capability.IStorage<CapabilityOcarina> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CapabilityOcarina> capability, CapabilityOcarina instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag(NBT_Tags.OCARINA, instance.serializeNBT());
            return compound;
        }

        @Override
        public void readNBT(Capability<CapabilityOcarina> capability, CapabilityOcarina instance, EnumFacing side, NBTBase nbt) {
            if (nbt.getId() == 10) {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                if (tag.hasKey(NBT_Tags.OCARINA)) {
                    instance.deserializeNBT(tag.getCompoundTag(NBT_Tags.OCARINA));
                }
            }
        }
    }
}


