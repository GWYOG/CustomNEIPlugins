package pers.gwyog.customneiplugins.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import pers.gwyog.customneiplugins.config.ConfigLoader;

public class PacketClientCommand implements IMessage, IMessageHandler<PacketClientCommand, IMessage>{
    public int commandType;

    public PacketClientCommand() {}
    
    public PacketClientCommand(int commandType) {
        this.commandType = commandType;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.commandType = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.commandType);
    }

    @Override
    public IMessage onMessage(PacketClientCommand message, MessageContext ctx) {
        switch (message.commandType) {
        case 0: // Command /cnp reload
            ConfigLoader.reloadPlugins();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("customneiplugins.command.reload.info", ConfigLoader.loadedPlugins.size()));
            break;
        case 1: // Command /cnp status
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("customneiplugins.command.status.info", ConfigLoader.loadedPlugins.size(), ConfigLoader.disabledPlugins.size(), ConfigLoader.erroredPlugins.size()));
            for (String loadedPluginName: ConfigLoader.loadedPlugins)
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("customneiplugins.command.status_loaded.info", loadedPluginName));
            for (String disabledPluginName: ConfigLoader.disabledPlugins)
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("customneiplugins.command.status_disabled.info", disabledPluginName));
            for (String erroredPluginName: ConfigLoader.erroredPlugins)
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("customneiplugins.command.status_errored.info", erroredPluginName));
        }
        return null;
    }

}
