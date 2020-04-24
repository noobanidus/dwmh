package com.noobanidus.dwmh.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GetName implements IMessage {
  private int entityId;

  public GetName(int entityId) {
    this.entityId = entityId;
  }

  public GetName() {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.entityId = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entityId);
  }

  public static class Handler implements IMessageHandler<GetName, IMessage> {
    @Override
    public IMessage onMessage(GetName message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
      return null;
    }

    private void handle(GetName message, MessageContext ctx) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      World world = player.world;
      Entity target = world.getEntityByID(message.entityId);
      if (target != null) {
        SendName packet = new SendName(target.getDisplayName());
        Networking.INSTANCE.sendToServer(packet);
      }
    }
  }
}
