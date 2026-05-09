package cn.starlight.electricpvp.event;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cn.starlight.electricpvp.ElectricPVP;
import cn.starlight.electricpvp.config.MainConfig;
import cn.starlight.electricpvp.core.DglabClient;
import cn.starlight.electricpvp.core.util.DGStrength;
import cn.starlight.electricpvp.core.util.TimeUtil;
import net.minecraft.client.Minecraft;

import java.net.InetAddress;

public class EventHandler {
    public static EventHandler INSTANCE = null;
    private final TimeUtil timeUtil = new TimeUtil();
    private boolean lastEnabled = false;
    private boolean lastConnected = false;

    private int tickCounter = 0;
    private int lastRunTickA = 0;
    private int lastRunTickB = 0;
    private boolean hasDetectedADelay = false;
    private boolean hasDetectedBDelay = false;
    private boolean ClearA = false;
    private boolean ClearB = false;
    private boolean hasDetectedADelayZeroAndStrength = false;
    private boolean hasDetectedBDelayZeroAndStrength = false;

    public EventHandler() {
        EventManager.INSTANCE.register(this);
    }

    @Subscribe
    private void onTick(TickEvent event) {
        // Connection handler loop
        try {
            if (!MainConfig.INSTANCE.enabled) {
                if (lastEnabled) {
                    DglabClient.stop();
                    lastEnabled = false;
                    lastConnected = false;
                }
                return;
            }
            if (!lastEnabled) {
                DglabClient.initialize();
            }
            lastEnabled = true;

            if (!ElectricPVP.serverLoaded) {
                ElectricPVP.showMsg("Starting websocket server!");
                ElectricPVP.serverLoaded = true;
                DglabClient.webSocketServer.start();
            } else {
                if (timeUtil.hasReached(5000)) {
                    if (!DglabClient.webSocketServer.getConnected()) {
                        if (lastConnected) {
                            ElectricPVP.showMsg("§cServer is no longer connected!");
                            lastConnected = false;
                            return;
                        }
                        ElectricPVP.showMsg("§eLocal IP: " + MainConfig.address);
                        ElectricPVP.showMsg("§eServer is ready! Scan the QR code now.");
                        ElectricPVP.showMsg("§ePlease ensure all clients are connected to the same LAN first.");
                        ElectricPVP.showMsg("§eIf you can't connect to the server, check if the address is from the correct network adapter. (You can change it by clicking \"Toggle Network Adapter\")");
                        lastConnected = false;
                    } else {
                        if (!lastConnected) {
                            ElectricPVP.showMsg("§aConnected to the server successfully!");
                            lastConnected = true;
                        }
                    }
                    timeUtil.reset();
                }
            }
        } catch (Exception ignored) {
        }

        if (!lastEnabled || !lastConnected) return;
        // Strength handler loop
        Minecraft mc = Minecraft.getMinecraft();
        DGStrength dgStrength = DglabClient.webSocketServer.getStrength(); // 获取DGStrength对象
        int ADelayTime = dgStrength.getADelayTime(), BDelayTime = dgStrength.getBDelayTime(); // 获取A和B的等待时间

        // 更新等待时间
        ADelayTime = (ADelayTime > 0) ? ADelayTime - 1 : 0; // 如果ADelayTime大于0，减少1；否则设置为0
        BDelayTime = (BDelayTime > 0) ? BDelayTime - 1 : 0; // 如果BDelayTime大于0，减少1；否则设置为0
        DglabClient.webSocketServer.setDelayTime(ADelayTime, BDelayTime); // 设置更新后的等待时间

        int AStrength = dgStrength.getAStrength(), BStrength = dgStrength.getBStrength(); // 获取A和B的强度
        int AMin = 0, BMin = 0;
        if(mc.thePlayer != null){
            AMin = MainConfig.aStrength;
            BMin = MainConfig.bStrength;
        }

        if (AStrength < AMin) {
            DglabClient.webSocketServer.sendStrengthToClient(AMin, 2, 1);
        }
        if (BStrength < BMin) {
            DglabClient.webSocketServer.sendStrengthToClient(BMin, 2, 2);
        }
        if (tickCounter % MainConfig.aDecreaseFrequency == 0 && ADelayTime <= 0 && AStrength > AMin){
            // 如果计数器是ADownTime的倍数，且ADelayTime小于等于0且AStrength大于0，则发送A的强度值
            if (DglabClient.webSocketServer.getStrength().getAStrength() - MainConfig.aDecrease < AMin) {
                DglabClient.webSocketServer.sendStrengthToClient(AMin, 2, 1);
            }
            else
                DglabClient.webSocketServer.sendStrengthToClient(MainConfig.aDecrease, 0, 1);
        }
        if (tickCounter % MainConfig.bDecreaseFrequency == 0 && BDelayTime <= 0 && BStrength > BMin) {
            // 如果计数器是BDownTime的倍数，且BDelayTime小于等于0且BStrength大于0，则发送B的强度值
            if (DglabClient.webSocketServer.getStrength().getBStrength() - MainConfig.bDecrease < BMin)
                DglabClient.webSocketServer.sendStrengthToClient(BMin, 2, 2);
            else
                DglabClient.webSocketServer.sendStrengthToClient(MainConfig.bDecrease, 0, 2);
        }

        // 检查A的延迟时间和强度
        if (ADelayTime > 0) {
            hasDetectedADelayZeroAndStrength = false;
            ClearA = false;
            if (!hasDetectedADelay) {
                DglabClient.webSocketServer.sendDgWaveform(2, true, 1);
                hasDetectedADelay = true;
            } else if (tickCounter - lastRunTickA >= DglabClient.waveformMap.get("ADamage").getDuration() * 2) {
                DglabClient.webSocketServer.sendDgWaveform(2, false, 1);
                lastRunTickA = tickCounter;
            }
        } else {
            hasDetectedADelay = false;
            if (AStrength > 0) {
                if (!hasDetectedADelayZeroAndStrength) {
                    DglabClient.webSocketServer.sendDgWaveform(3, true, 1);
                    hasDetectedADelayZeroAndStrength = true;
                } else if (tickCounter - lastRunTickA >= DglabClient.waveformMap.get("AHealing").getDuration() * 2) {
                    DglabClient.webSocketServer.sendDgWaveform(3, false, 1);
                    lastRunTickA = tickCounter;
                }

            }
            else if(!ClearA){
                DglabClient.webSocketServer.cleanFrequency(1);
                ClearA = true;
            }
        }

        if (BDelayTime > 0) {
            ClearB = false;
            hasDetectedBDelayZeroAndStrength = false;
            if (!hasDetectedBDelay) {
                DglabClient.webSocketServer.sendDgWaveform(2, true, 2);
                hasDetectedBDelay = true;
            } else if (tickCounter - lastRunTickB >= DglabClient.waveformMap.get("BDamage").getDuration() * 2) {
                DglabClient.webSocketServer.sendDgWaveform(2, false, 2);
                lastRunTickB = tickCounter;
            }
        } else {
            hasDetectedBDelay = false;
            if (BStrength > 0) {
                if (!hasDetectedBDelayZeroAndStrength) {
                    DglabClient.webSocketServer.sendDgWaveform(3, true, 2);
                    hasDetectedBDelayZeroAndStrength = true;
                } else if (tickCounter - lastRunTickB >= DglabClient.waveformMap.get("BHealing").getDuration() * 2) {
                    DglabClient.webSocketServer.sendDgWaveform(3, false, 2);
                    lastRunTickB = tickCounter;
                }

            }
            else if(!ClearB){
                DglabClient.webSocketServer.cleanFrequency(2);
                ClearB = true;
            }
        }

        tickCounter++; // 增加计数器
        if (tickCounter == 2147483625) tickCounter = 0; // 如果计数器达到2147483625，则重置为0
    }
}
