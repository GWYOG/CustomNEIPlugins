package pers.gwyog.customneiplugins.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import pers.gwyog.customneiplugins.CustomNEIPlugins;

public class CNPNetwork {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CustomNEIPlugins.MODID);
    public static int id = 0;
    
    public CNPNetwork() {
        registerPacketClient(PacketClientCommand.class, PacketClientCommand.class);
    }
    
    private void registerPacketClient(Class handlerClazz, Class packetClazz) {
        INSTANCE.registerMessage(handlerClazz, packetClazz, id++, Side.CLIENT);
    }
    
}
