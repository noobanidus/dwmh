package com.noobanidus.dwmh.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class EntityData extends WorldSavedData {
  public static final String id = "DudeWheresMyHorse-EntityTracking";

  public Set<UUID> trackedEntities = new HashSet<>();
  public Map<UUID, UUID> entityToOwner = new HashMap<>();
  // Dynamically reconstructed but also modified
  public Map<UUID, List<UUID>> ownerToEntities = new HashMap<>();

  // List of entities that have been unloaded and saved
  public Set<UUID> storedEntities = new HashSet<>();
  // List of entities that have been recreated from saved data
  public Set<UUID> restoredEntities = new HashSet<>();

  public Map<UUID, NBTTagCompound> savedEntities = new HashMap<>();
  public Map<UUID, String> entityToName = new HashMap<>();
  public Map<UUID, ResourceLocation> entityToResourceLocation = new HashMap<>();

  public EntityData() {
    super(id);
  }

  public EntityData(String name) {
    super(name);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    entityToOwner.clear();
    trackedEntities.clear();
    ownerToEntities.clear();
    savedEntities.clear();
    storedEntities.clear();
    restoredEntities.clear();
    entityToName.clear();
    entityToResourceLocation.clear();
    NBTTagList owners = nbt.getTagList("owners", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < owners.tagCount(); i++) {
      NBTTagCompound thisEntry = owners.getCompoundTagAt(i);
      UUID owner = thisEntry.getUniqueId("owner");
      UUID entity = thisEntry.getUniqueId("entity");
      entityToOwner.put(entity, owner);
      ownerToEntities.computeIfAbsent(owner, o -> new ArrayList<>()).add(entity);
    }
    NBTTagList tracked = nbt.getTagList("tracked", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tracked.tagCount(); i++) {
      NBTTagCompound thisEntry = tracked.getCompoundTagAt(i);
      UUID entity = thisEntry.getUniqueId("entity");
      trackedEntities.add(entity);
    }
    if (nbt.hasKey("savedData")) {
      NBTTagList entityData = nbt.getTagList("savedData", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < entityData.tagCount(); i++) {
        NBTTagCompound entry = entityData.getCompoundTagAt(i);
        UUID entityId = entry.getUniqueId("id");
        NBTTagCompound data = entry.getCompoundTag("data");
        savedEntities.put(entityId, data);
      }
    }
    if (nbt.hasKey("stored")) {
      NBTTagList stored = nbt.getTagList("stored", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < stored.tagCount(); i++) {
        NBTTagCompound thisEntry = stored.getCompoundTagAt(i);
        UUID entity = thisEntry.getUniqueId("entity");
        storedEntities.add(entity);
      }
    }
    if (nbt.hasKey("restored")) {
      NBTTagList restore = nbt.getTagList("restore", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < restore.tagCount(); i++) {
        NBTTagCompound thisEntry = restore.getCompoundTagAt(i);
        UUID entity = thisEntry.getUniqueId("entity");
        restoredEntities.add(entity);
      }
    }
    if (nbt.hasKey("names")) {
      NBTTagList names = nbt.getTagList("names", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < names.tagCount(); i++) {
        NBTTagCompound thisEntry = names.getCompoundTagAt(i);
        UUID entity = thisEntry.getUniqueId("entity");
        String name = thisEntry.getString("name");
        entityToName.put(entity, name);
      }
    }
    if (nbt.hasKey("resources")) {
      NBTTagList resources = nbt.getTagList("resources", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < resources.tagCount(); i++) {
        NBTTagCompound thisEntry = resources.getCompoundTagAt(i);
        UUID entity = thisEntry.getUniqueId("entity");
        ResourceLocation rl = new ResourceLocation(thisEntry.getString("resource"));
        entityToResourceLocation.put(entity, rl);
      }
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
    NBTTagList tracked = new NBTTagList();
    for (UUID entity : trackedEntities) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entity);
      tracked.appendTag(thisEntry);
    }
    NBTTagList entityData = new NBTTagList();
    for (Map.Entry<UUID, NBTTagCompound> entry : savedEntities.entrySet()) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("id", entry.getKey());
      thisEntry.setTag("data", entry.getValue());
    }

    // Stored entities
    NBTTagList stored = new NBTTagList();
    for (UUID entity : storedEntities) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entity);
      stored.appendTag(thisEntry);
    }

    // Restored entities
    NBTTagList restored = new NBTTagList();
    for (UUID entity : restoredEntities) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entity);
      restored.appendTag(thisEntry);
    }
    NBTTagList names = new NBTTagList();
    for (Map.Entry<UUID, String> name : entityToName.entrySet()) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", name.getKey());
      thisEntry.setString("name", name.getValue());
      names.appendTag(thisEntry);
    }
    NBTTagList rls = new NBTTagList();
    for (Map.Entry<UUID, ResourceLocation> entry : entityToResourceLocation.entrySet()) {
      NBTTagCompound thisEntry = new NBTTagCompound();
      thisEntry.setUniqueId("entity", entry.getKey());
      thisEntry.setString("resource", entry.getValue().toString());
      rls.appendTag(thisEntry);
    }
    NBTTagCompound result = new NBTTagCompound();
    result.setTag("owners", owners);
    result.setTag("tracked", tracked);
    result.setTag("savedData", entityData);
    result.setTag("stored", stored);
    result.setTag("restored", restored);
    result.setTag("names", names);
    result.setTag("resources", rls);
    return result;
  }
}
