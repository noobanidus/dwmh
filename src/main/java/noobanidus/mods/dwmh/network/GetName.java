package noobanidus.mods.dwmh.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GetName {
  private int entityId;

  public GetName(PacketBuffer buffer) {
    this.entityId = buffer.readInt();
  }

  public GetName(int entityId) {
    this.entityId = entityId;
  }

  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.entityId);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      PlayerEntity player = Minecraft.getInstance().player;
      World world = player.world;
      Entity target = world.getEntityByID(entityId);
      if (target != null) {
        SendName packet = new SendName(target.getName());
        Networking.sendToServer(packet);
      }
    });
  }
}
