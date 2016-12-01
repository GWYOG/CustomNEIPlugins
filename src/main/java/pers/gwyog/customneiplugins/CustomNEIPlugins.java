package pers.gwyog.customneiplugins;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import pers.gwyog.customneiplugins.config.ConfigLoader;
import pers.gwyog.customneiplugins.plugin.NEIPluginConfig;

@Mod(modid = CustomNEIPlugins.MODID, name = CustomNEIPlugins.MODNAME, version = CustomNEIPlugins.VERSION, dependencies = "required-after:NotEnoughItems")
public class CustomNEIPlugins {
    public static final String MODID = "customneiplugins";
    public static final String MODNAME = "CustomNEIPlugins";
    public static final String VERSION = "1.0.0";
    
    public static boolean isCustomStuff2Loaded;
    
    @Instance
    public static CustomNEIPlugins instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ConfigLoader.init(event);
            isCustomStuff2Loaded = Loader.isModLoaded("CustomStuff2");
        }
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ConfigLoader.loadPlugins();
            NEIPluginConfig.loadConfigPostInit();
        }
    } 
    
}
