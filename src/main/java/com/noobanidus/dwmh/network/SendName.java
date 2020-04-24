package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.util.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Arrays;

public class SendName implements IMessage {
  private ITextComponent name;

  public SendName(ITextComponent name) {
    this.name = name;
  }

  public SendName() {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.name = ITextComponent.Serializer.fromJsonLenient(ByteBufUtils.readUTF8String(buf));
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(name));
  }

  public static class Handler implements IMessageHandler<SendName, IMessage> {
    @Override
    public IMessage onMessage(SendName message, MessageContext ctx) {
      FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> handle(message, ctx));
      return null;
    }

    private void handle(SendName message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      if (player == null) {
        return;
      }
      for (EnumHand hand : Arrays.asList(EnumHand.MAIN_HAND, EnumHand.OFF_HAND)) {
        if (player.getHeldItem(hand).getItem() == ItemRegistry.OCARINA) {
          ItemStack stack = player.getHeldItem(hand);
          NBTTagCompound tag = Util.getOrCreateTagCompound(stack);
          tag.setString("name", message.name.getFormattedText());
          stack.setTagCompound(tag);
          player.setHeldItem(hand, stack);
          player.sendAllContents(player.openContainer, player.openContainer.getInventory());
          break;
        }
      }
    }
  }
}
