package com.noobanidus.dwmh.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class EntityData extends WorldSavedData {
  public static final String id = "DudeWheresMyHorse-EntityTracking";

  public Set<UUID> trackedEntities = new HashSet<>();
  public Map<UUID, UUID> entityToOwner = new HashMap<>();
  // Dynamically reconstructed but also modified
  public Map<UUID, Set<UUID>> ownerToEntities = new HashMap<>();
  public Object2IntOpenHashMap<UUID> entityToId = new Object2IntOpenHashMap<>();

  public Map<UUID, NBTTagCompound> savedEntities = new HashMap<>();
  // List of entities that have been unloaded and saved
  public Set<UUID> storedEntities = new HashSet<>();
  // List of entities that have been recreated from saved data
  public Set<UUID> restoredEntities = new HashSet<>();

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
    ownerToEntities.clear();
    savedEntities.clear();
    storedEntities.clear();
    restoredEntities.clear();
    NBTTagList owners = nbt.getTagList("owners", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < owners.tagCount(); i++) {
      NBTTagCompound thisEntry = owners.getCompoundTagAt(i);
      UUID owner = thisEntry.getUniqueId("owner");
      UUID entity = thisEntry.getUniqueId("entity");
      entityToOwner.put(entity, owner);
      ownerToEntities.computeIfAbsent(owner, o -> new HashSet<>()).add(entity);
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

    NBTTagCompound result = new NBTTagCompound();
    result.setTag("owners", owners);
    result.setTag("ids", ids);
    result.setTag("tracked", tracked);
    result.setTag("savedData", entityData);
    result.setTag("stored", stored);
    result.setTag("restored", restored);
    return result;
  }
}
