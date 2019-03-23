package com.noobanidus.dwmh.capability;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityOwnHandler implements ICapabilitySerializable<NBTTagString> {
    public static final String CAPABILITY_NAME = "owner_capability";

    public final static ResourceLocation IDENTIFIER = new ResourceLocation(DWMH.MODID, CAPABILITY_NAME);

    @CapabilityInject(CapabilityOwner.class)
    public static Capability<CapabilityOwner> INSTANCE = null;
    private final CapabilityOwner capability;

    public CapabilityOwnHandler() {
        this.capability = new CapabilityOwner();
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
    public NBTTagString serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagString nbt) {
        capability.deserializeNBT(nbt);
    }

    public static class NBT_Tags {
        public final static String OWNER = "owner";
    }

    public static class CapabilityNameStorage implements Capability.IStorage<CapabilityOwner> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CapabilityOwner> capability, CapabilityOwner instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag(NBT_Tags.OWNER, instance.serializeNBT());
            return compound;
        }

        @Override
        public void readNBT(Capability<CapabilityOwner> capability, CapabilityOwner instance, EnumFacing side, NBTBase nbt) {
            if (nbt.getId() == 10) {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                if (tag.hasKey(NBT_Tags.OWNER)) {
                    instance.deserializeNBT((NBTTagString) tag.getTag(NBT_Tags.OWNER));
                    return;
                }
            }

            // If there's invalid NBT, just return a null
            instance.setOwner(null);
        }
    }
}

