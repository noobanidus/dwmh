package com.noobanidus.dwmh.commands;

import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.items.ItemOcarina;
import com.noobanidus.dwmh.util.EntityTracking;
import com.noobanidus.dwmh.world.DataHelper;
import com.noobanidus.dwmh.world.EntityData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.UUID;

public class CommandOcarina extends CommandBase {
  @Override
  public String getName() {
    return "ocarina";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/ocarina clear | /ocarina sync";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if (sender instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) sender;
      if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
        EntityData data = DataHelper.getTrackingData();
        List<UUID> owned = data.ownerToEntities.get(player.getUniqueID());
        if (owned == null || owned.isEmpty()) {
          player.sendMessage(new TextComponentString("No entities to clear."));
        } else {
          for (UUID entity : owned) {
            if (data.savedEntities.containsKey(entity)) {
              String name = data.entityToName.get(entity);
              if (name == null || name.isEmpty()) {
                name = "Unknown name";
              }
              player.sendMessage(new TextComponentString("One or more entities (" + name + ") are saved from unloaded chunks or death! Summon them before continuing."));
              return;
            }
          }

          for (UUID entity : owned) {
            data.entityToOwner.remove(entity);
            data.savedEntities.remove(entity);
            data.restoredEntities.remove(entity);
            data.trackedEntities.remove(entity);
            data.entityToResourceLocation.remove(entity);
            data.entityToName.remove(entity);
          }

          data.ownerToEntities.remove(player.getUniqueID());

          EntityTracking.save();

          trySyncHand(player);
          player.sendMessage(new TextComponentString("Entities cleared!"));
        }
      } else if (args.length == 1 && args[0].equalsIgnoreCase("sync")) {
        trySyncHand(player);
        player.sendMessage(new TextComponentString("Ocarinas in hand and inventory synced."));
      }
    }
  }

  public void trySyncHand (EntityPlayerMP player) {
    IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
    if (inventory == null) return;
    for (int i = 0; i < inventory.getSlots(); i++) {
      if (inventory.getStackInSlot(i).getItem() == ItemRegistry.OCARINA) {
        ItemStack ocarina = inventory.extractItem(i, 1, false);
        ItemOcarina.updateOcarinaNBT(ocarina, player);
        inventory.insertItem(i, ocarina, false);
      }
    }
    ItemStack offHand = player.getHeldItemOffhand();
    if (offHand.getItem() == ItemRegistry.OCARINA) {
      ItemOcarina.updateOcarinaNBT(offHand, player);
    }
    player.sendAllContents(player.openContainer, player.openContainer.getInventory());
  }
}
