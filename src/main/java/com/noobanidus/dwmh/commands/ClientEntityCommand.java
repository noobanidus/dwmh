package com.noobanidus.dwmh.commands;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEntityCommand extends CommandBase implements IClientCommand {
    @Override
    @Nonnull
    public String getName() {
        return "dwmh";
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "dwmh.strings.command.usage";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Lists.newArrayList("dwmh");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equals("entity")) {
            if (sender instanceof EntityPlayerSP) {
                RayTraceResult res = Minecraft.getMinecraft().objectMouseOver;

                ITextComponent temp;

                if (res != null && res.entityHit != null) {
                    temp = new TextComponentTranslation("dwmh.strings.command.entity_found", res.entityHit.getClass().getName());
                    temp.getStyle().setColor(TextFormatting.GOLD);
                    sender.sendMessage(temp);
                    StringSelection select = new StringSelection(res.entityHit.getClass().getName());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(select, select);
                    return;
                } else {
                    temp = new TextComponentTranslation("dwmh.strings.command.no_entity");
                    temp.getStyle().setColor(TextFormatting.DARK_RED);
                    sender.sendMessage(temp);
                    return;
                }
            }
        }

        throw new WrongUsageException(getUsage(sender));
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "entity") : Collections.emptyList();
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

}
