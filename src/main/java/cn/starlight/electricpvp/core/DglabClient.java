package cn.starlight.electricpvp.core;

import cn.starlight.electricpvp.ElectricPVP;
import cn.starlight.electricpvp.config.MainConfig;
import cn.starlight.electricpvp.core.tool.DGWaveformTool;
import cn.starlight.electricpvp.core.tool.QRTool;
import cn.starlight.electricpvp.core.util.DGStrength;
import cn.starlight.electricpvp.core.util.Waveform;
import net.minecraft.client.Minecraft;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class DglabClient {

    public static DglabServer webSocketServer = null;
    public static Map<String, Waveform> waveformMap = new HashMap<>();

    public static void initialize() {
        ElectricPVP.logger.info("Initializing ElectricPVP server!");

        waveformMap.clear();
        waveformMap.put("ADamage", new Waveform(MainConfig.aDamageWaveform).DataToGraph());
        waveformMap.put("BDamage", new Waveform(MainConfig.bDamageWaveform).DataToGraph());
        waveformMap.put("AHealing", new Waveform(MainConfig.aHealingWaveform).DataToGraph());
        waveformMap.put("BHealing", new Waveform(MainConfig.bHealingWaveform).DataToGraph());

        if (webSocketServer != null) {
            stop();
        }
        webSocketServer = new DglabServer(new InetSocketAddress(MainConfig.port));
        DGWaveformTool.updateDuration();

        if (MainConfig.autoStart) {
            webSocketServer.start();
            ElectricPVP.serverLoaded = true;
        }
    }

    public static void stop() {
        ElectricPVP.logger.info("Stopping ElectricPVP server!");
        if (ElectricPVP.serverLoaded) {
            try {
                webSocketServer.stop();
            } catch (Exception ignored) {}
            ElectricPVP.serverLoaded = false;
        }
    }

    // 二维码
    public static void createQR() {
        QRTool.createQR();
    }

    public static void setStrength(int a, int b) {
        DGStrength DGStrength = webSocketServer.getStrength();
        DGStrength.setAStrength(a);
        DGStrength.setBStrength(b);
        webSocketServer.setStrength(DGStrength);
        webSocketServer.sendStrength();
    }
}