package noobanidus.mods.dwmh.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import noobanidus.mods.dwmh.init.ItemRegistry;
import noobanidus.mods.dwmh.util.Util;

import java.util.Arrays;
import java.util.function.Supplier;

public class SendName {
  private ITextComponent name;

  public SendName(PacketBuffer buffer) {
    this.name = buffer.readTextComponent();
  }

  public SendName(ITextComponent name) {
    this.name = name;
  }

  public void encode(PacketBuffer buffer) {
    buffer.writeTextComponent(this.name);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      ServerPlayerEntity player = context.get().getSender();
      if (player == null) {
        return;
      }
      for (Hand hand : Arrays.asList(Hand.MAIN_HAND, Hand.OFF_HAND)) {
        if (player.getHeldItem(hand).getItem() == ItemRegistry.OCARINA) {
          ItemStack stack = player.getHeldItem(hand);
          CompoundNBT tag = Util.getOrCreateTagCompound(stack);
          tag.putString("name", this.name.getFormattedText());
          stack.setTag(tag);
          player.setHeldItem(hand, stack);
          player.sendAllContents(player.openContainer, player.openContainer.getInventory());
          break;
        }
      }
    });
  }
}

