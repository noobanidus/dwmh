package noobanidus.mods.dwmh.world;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.dwmh.types.DimBlockPos;

import java.util.*;

public class EntityData extends WorldSavedData {
  public static final String id = "DudeWheresMyHorse-EntityTracking";

  public Set<UUID> trackedEntities = new HashSet<>();
  public Map<UUID, UUID> entityToOwner = new HashMap<>();
  public Map<UUID, DimBlockPos> lastKnownLocation = new HashMap<>();

  public EntityData() {
    super(id);
  }

  public EntityData(String name) {
    super(name);
  }

  @Override
  public void read(CompoundNBT nbt) {
    entityToOwner.clear();
    trackedEntities.clear();
    lastKnownLocation.clear();
    ListNBT owners = nbt.getList("owners", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < owners.size(); i++) {
      CompoundNBT thisEntry = owners.getCompound(i);
      UUID owner = thisEntry.getUniqueId("owner");
      UUID entity = thisEntry.getUniqueId("entity");
      entityToOwner.put(entity, owner);
    }
    ListNBT tracked = nbt.getList("tracked", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tracked.size(); i++) {
      CompoundNBT thisEntry = tracked.getCompound(i);
      UUID entity = thisEntry.getUniqueId("entity");
      trackedEntities.add(entity);
    }
    if (nbt.contains("lastknown")) {
      ListNBT lastKnown = nbt.getList("lastknown", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < lastKnown.size(); i++) {
        CompoundNBT thisEntry = lastKnown.getCompound(i);
        BlockPos pos = BlockPos.fromLong(thisEntry.getLong("pos"));
        int dimId = thisEntry.getInt("dim");
        UUID entity = thisEntry.getUniqueId("entity");
        lastKnownLocation.put(entity, new DimBlockPos(pos, DimensionType.getById(dimId)));
      }
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
    ListNBT tracked = new ListNBT();
    for (UUID entity : trackedEntities) {
      CompoundNBT thisEntry = new CompoundNBT();
      thisEntry.putUniqueId("entity", entity);
      tracked.add(thisEntry);
    }
    ListNBT lastknown = new ListNBT();
    for (Map.Entry<UUID, DimBlockPos> entry : lastKnownLocation.entrySet()) {
      CompoundNBT thisEntry = new CompoundNBT();
      thisEntry.putUniqueId("entity", entry.getKey());
      thisEntry.putLong("pos", entry.getValue().getPos().toLong());
      thisEntry.putInt("dim", entry.getValue().getDim().getId());
      lastknown.add(thisEntry);
    }
    CompoundNBT result = new CompoundNBT();
    result.put("owners", owners);
    result.put("tracked", tracked);
    result.put("lastknown", lastknown);
    return result;
  }
}
