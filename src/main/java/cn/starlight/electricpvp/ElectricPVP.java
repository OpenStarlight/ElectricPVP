package cn.starlight.electricpvp;

import cn.starlight.electricpvp.config.MainConfig;
import cn.starlight.electricpvp.core.DglabClient;
import cn.starlight.electricpvp.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ElectricPVP.MODID, name = ElectricPVP.NAME, version = ElectricPVP.VERSION)
public class ElectricPVP {
    // Set in Gradle config
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final Logger logger = LogManager.getLogger("ElectricPVP");
    public static boolean serverLoaded = false;
    @Mod.Instance(MODID)
    public static ElectricPVP INSTANCE;

    @Mod.EventHandler
    public void onInitialize(FMLInitializationEvent event) {
        MainConfig.INSTANCE = new MainConfig();
        EventHandler.INSTANCE = new EventHandler();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        DglabClient.initialize();
    }

    @Mod.EventHandler
    public void onServerStop(FMLServerStoppingEvent event) {
        try {
            DglabClient.stop();
        } catch (Exception ignored) {}
    }

    public static void showMsg(Object msg) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§b§l" + NAME + "§r§7] §f" + msg));
        }
    }
}
