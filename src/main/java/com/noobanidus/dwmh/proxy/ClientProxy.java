package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.commands.ClientEntityCommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        super.loadComplete(event);
        ClientCommandHandler.instance.registerCommand(new ClientEntityCommand());
    }
}