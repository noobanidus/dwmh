package com.noobanidus.dwmh.config;

import net.minecraft.nbt.NBTTagCompound;

public class ClientStorage {
    private NBTTagCompound localConfig;
    private NBTTagCompound serverConfig = null;

    public ClientStorage() {
        this.localConfig = ConfigHandler.serialize();
    }

    public void updateFromServer(NBTTagCompound serverConfig) {
        this.serverConfig = serverConfig;
    }

    private NBTTagCompound getTagCompound() {
        // Too soon to be initialising from the serverConfig as we haven't even
        // logged into a server yet.
        if (serverConfig == null) return localConfig;
        return serverConfig;
    }

    public boolean getBoolean(String category, String variable) {
        NBTTagCompound comp = getTagCompound();
        return comp.getCompoundTag(category).getBoolean(variable);
    }

    public int getInteger(String category, String variable) {
        NBTTagCompound comp = getTagCompound();
        return comp.getCompoundTag(category).getInteger(variable);
    }

    public String getString(String category, String variable) {
        NBTTagCompound comp = getTagCompound();
        return comp.getCompoundTag(category).getString(variable);
    }

    public double getDouble(String category, String variable) {
        NBTTagCompound comp = getTagCompound();
        return comp.getCompoundTag(category).getDouble(variable);
    }
}
