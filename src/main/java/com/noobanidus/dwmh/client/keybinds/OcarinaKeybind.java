package com.noobanidus.dwmh.client.keybinds;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.Registrar;
import com.noobanidus.dwmh.items.ItemOcarina;
import com.noobanidus.dwmh.network.PacketHandler;
import com.noobanidus.dwmh.network.PacketOcarina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DWMH.MODID)
@SuppressWarnings("unused")
public class OcarinaKeybind {
    public static final String DWMH_GROUP = "dwmh.gui.keygroup";
    public static final String DWMH_BINDS = "dwmh.gui.keybind";

    public static KeyBinding ocarinaKey = null;

    public static void initKeybinds() {
        KeyBinding kb = new KeyBinding(DWMH_BINDS + ".ocarina", 0, DWMH_GROUP);
        ClientRegistry.registerKeyBinding(kb);
        ocarinaKey = kb;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyOcarina(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        // let's make assumptions about which hand it is
        ItemStack stackHand = mc.player.getHeldItemMainhand();
        ItemStack stackOffhand = mc.player.getHeldItemOffhand();

        ItemOcarina Ocarina = Registrar.ocarina;

        EnumHand hand = null;
        if (!stackHand.isEmpty() && stackHand.getItem() == Ocarina) {
            hand = EnumHand.MAIN_HAND;
        } else if (!stackOffhand.isEmpty() && stackOffhand.getItem() == Ocarina) {
            hand = EnumHand.OFF_HAND;
        }

        if (ocarinaKey.isKeyDown() && hand != null) {
            Ocarina.cycleMode(mc.player, mc.player.isSneaking());

            PacketOcarina.Mode packet = new PacketOcarina.Mode(Ocarina.getPlayerMode(mc.player));
            PacketHandler.sendToServer(packet);
        }
    }
}
