package com.noobanidus.dwmh;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;

public class ChunkLoader implements ForgeChunkManager.OrderedLoadingCallback {
  public static Map<ForgeChunkManager.Ticket, UUID> ticketToUUID = new HashMap<>();
  public static Map<UUID, Loader> UUIDloaderMap = new HashMap<>();

  @Override
  public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount) {
    return Collections.emptyList();
  }

  @Override
  public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
    // NOOP
  }

  public static boolean requestTicket (int dimension, ChunkPos pos, UUID uuid) {
    World world = DimensionManager.getWorld(dimension);
    ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(DWMH.instance, world, ForgeChunkManager.Type.NORMAL);
    if (ticket == null) {
      return false;
    }
    Loader l = new Loader(pos, uuid);
    UUIDloaderMap.put(uuid, l);
    ticketToUUID.put(ticket, uuid);
    ForgeChunkManager.forceChunk(ticket, pos);

  }

  public static class Loader implements Runnable {
    private ChunkPos pos;
    private UUID uuid;

    public Loader(ChunkPos pos, UUID uuid) {
      this.pos = pos;
      this.uuid = uuid;
    }

    @Override
    public void run() {

    }
  }
}
