package com.noobanidus.dwmh.world;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class EntityData extends WorldSavedData {
  public static final String id = "DudeWheresMyHorse-EntityTracking";

  public Set<UUID> trackedEntities = new HashSet<>();
  public Map<UUID, UUID> entityToOwner = new HashMap<>();
  public Object2IntOpenHashMap<UUID> entityToId = new Object2IntOpenHashMap<>();

  public EntityData() {
    super(id);
  }

  public EntityData(String name) {
    super(name);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    entityToOwner.clear();
    entityToId.clear();
    trackedEntities.clear();
    NBTTagList owners = nbt.getTagList("owners", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < owners.tagCount(); i++) {
      NBTTagCompound thisEntry = owners.getCompoundTagAt(i);
      UUID owner = thisEntry.getUniqueId("owner");
      UUID entity = thisEntry.getUniqueId("entity");
      entityToOwner.put(entity, owner);
    }
    NBTTagList ids = nbt.getTagList("ids", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < ids.tagCount(); i++) {
      NBTTagCompound thisEntry = ids.getCompoundTagAt(i);
      UUID entity = thisEntry.getUniqueId("entity");
      int id = thisEntry.getInteger("id");
      entityToId.put(entity, id);
    }
    NBTTagList tracked = nbt.getTagList("tracked", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tracked.tagCount(); i++) {
      NBTTagCompound thisEntry = tracked.getCompoundTagAt(i);
      UUID entity = thisEntry.getUniqueId("entity");
      trackedEntities.add(entity);
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    NBTTagList owners = new NBTTagList();
    for (Map.Entry<UUID, UUID> entry : entityToOwner.entrySet()) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entry.getKey());
      thisEntry.setUniqueId("owner", entry.getValue());
      owners.appendTag(thisEntry);
    }
    NBTTagList ids = new NBTTagList();
    for (Map.Entry<UUID, Integer> entry : entityToId.entrySet()) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entry.getKey());
      thisEntry.setInteger("id", entry.getValue());
      ids.appendTag(thisEntry);
    }
    NBTTagList tracked = new NBTTagList();
    for (UUID entity : trackedEntities) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entity);
      tracked.appendTag(thisEntry);
    }
    NBTTagCompound result = new NBTTagCompound();
    result.setTag("owners", owners);
    result.setTag("ids", ids);
    result.setTag("tracked", tracked);
    return result;
  }
}
