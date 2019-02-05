package com.noobanidus.dwmh.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityNameHandler implements ICapabilitySerializable<NBTTagString> {
    public final static ResourceLocation IDENTIFIER = new ResourceLocation("dwmh", "owner_capability");

    @CapabilityInject(CapabilityName.class)
    public static Capability<CapabilityName> INSTANCE = null;
    private static String OWNER_KEY = "owner";
    private final CapabilityName capability;

    public CapabilityNameHandler() {
        this.capability = new CapabilityName();
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

    public static class CapabilityNameStorage implements Capability.IStorage<CapabilityName> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CapabilityName> capability, CapabilityName instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<CapabilityName> capability, CapabilityName instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagString) nbt);
        }
    }
}

