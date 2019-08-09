package com.noobanidus.dwmh.world;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
  public void read(CompoundNBT nbt) {
    entityToOwner.clear();
    entityToId.clear();
    trackedEntities.clear();
    ListNBT owners = nbt.getList("owners", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < owners.size(); i++) {
      CompoundNBT thisEntry = owners.getCompound(i);
      UUID owner = thisEntry.getUniqueId("owner");
      UUID entity = thisEntry.getUniqueId("entity");
      entityToOwner.put(entity, owner);
    }
    ListNBT ids = nbt.getList("ids", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < ids.size(); i++) {
      CompoundNBT thisEntry = ids.getCompound(i);
      UUID entity = thisEntry.getUniqueId("entity");
      int id = thisEntry.getInt("id");
      entityToId.put(entity, id);
    }
    ListNBT tracked = nbt.getList("tracked", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tracked.size(); i++) {
      CompoundNBT thisEntry = tracked.getCompound(i);
      UUID entity = thisEntry.getUniqueId("entity");
      trackedEntities.add(entity);
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    ListNBT owners = new ListNBT();
    for (Map.Entry<UUID, UUID> entry : entityToOwner.entrySet()) {
      CompoundNBT thisEntry = new CompoundNBT();
      thisEntry.putUniqueId("entity", entry.getKey());
      thisEntry.putUniqueId("owner", entry.getValue());
      owners.add(thisEntry);
    }
    ListNBT ids = new ListNBT();
    for (Map.Entry<UUID, Integer> entry : entityToId.entrySet()) {
      CompoundNBT thisEntry = new CompoundNBT();
      thisEntry.putUniqueId("entity", entry.getKey());
      thisEntry.putInt("id", entry.getValue());
      ids.add(thisEntry);
    }
    ListNBT tracked = new ListNBT();
    for (UUID entity : trackedEntities) {
      CompoundNBT thisEntry = new CompoundNBT();
      thisEntry.putUniqueId("entity", entity);
      tracked.add(thisEntry);
    }
    CompoundNBT result = new CompoundNBT();
    result.put("owners", owners);
    result.put("ids", ids);
    result.put("tracked", tracked);
    return result;
  }
}
