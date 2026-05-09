package cn.starlight.electricpvp.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cn.starlight.electricpvp.ElectricPVP;
import cn.starlight.electricpvp.core.DglabClient;
import cn.starlight.electricpvp.core.util.NetworkAdapter;
import cn.starlight.electricpvp.hud.StrengthHud;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainConfig extends Config {
    public static MainConfig INSTANCE = null;

    @Header(text = "Websocket Server Settings")
    public static boolean useless1;

    @Text(name = "Address", description = "The address of websocket server.", placeholder = "192.168.31.51", size = 2)
    public static String address = "192.168.31.51";

    @Number(name = "Port", description = "The port of websocket server.", min = 0, max = 65535, size = 2)
    public static int port = 9999;

    @Switch(name = "Auto Start", description = "Automatically start the websocket server when game launches.", size = 2)
    public static boolean autoStart = false;

    @Button(name = "Toggle Network Adapter", text = "Toggle", description = "Change address according to network adapter.")
    Runnable runnable = this::toggleNetworkAdapter;

    @Button(name = "Show QR Code", text = "Show", description = "Generate and open the QR code to connect.")
    Runnable runnable2 = DglabClient::createQR;

    @Header(text = "Strength Settings")
    public static boolean useless2;

    @Slider(name = "A Strength", description = "Regular A strength.", min = 0f, max = 100f, step = 1)
    public static int aStrength = 0;

    @Slider(name = "A Increase", description = "The increase of A strength when being damaged.", min = 1f, max = 20f, step = 1)
    public static int aIncrease = 2;

    @Slider(name = "A Decrease", description = "The decrease of A strength when not being damaged.", min = 1f, max = 20f, step = 1)
    public static int aDecrease = 2;

    @Slider(name = "A Decrease Delay", description = "[N] tick(s) should pass to start decreasing A strength when not being damaged.", min = 1f, max = 200f, step = 1)
    public static int aDecreaseDelay = 40;

    @Slider(name = "A Decrease Frequency", description = "Every [N] tick(s) should pass to decrease A strength once.", min = 1f, max = 200f, step = 1)
    public static int aDecreaseFrequency = 20;

    @Slider(name = "B Strength", description = "Regular B strength.", min = 0f, max = 100f, step = 1)
    public static int bStrength = 0;

    @Slider(name = "B Increase", description = "The increase of B strength when being damaged.", min = 1f, max = 20f, step = 1)
    public static int bIncrease = 2;

    @Slider(name = "B Decrease", description = "The decrease of B strength when not being damaged.", min = 1f, max = 20f, step = 1)
    public static int bDecrease = 2;

    @Slider(name = "B Decrease Delay", description = "[N] tick(s) should pass to start decreasing B strength when not being damaged.", min = 1, max = 200, step = 1)
    public static int bDecreaseDelay = 40;

    @Slider(name = "B Decrease Frequency", description = "Every [N] tick(s) should pass to decrease B strength once.", min = 1f, max = 200f, step = 1)
    public static int bDecreaseFrequency = 20;

    @Header(text = "Waveform Settings")
    public static boolean useless3;

    @Text(name = "A Damage Waveform", description = "The A damage waveform.", placeholder = "\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"", size = 2)
    public static String aDamageWaveform = "\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"";

    @Text(name = "A Healing Waveform", description = "The A healing waveform.", placeholder = "\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"", size = 2)
    public static String aHealingWaveform = "\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"";

    @Text(name = "B Damage Waveform", description = "The B damage waveform.", placeholder = "\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"", size = 2)
    public static String bDamageWaveform = "\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"";

    @Text(name = "B Healing Waveform", description = "The B healing waveform.", placeholder = "\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"", size = 2)
    public static String bHealingWaveform = "\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"";

    @HUD(name = "Strength HUD")
    public StrengthHud hud = new StrengthHud();

    public MainConfig() {
        super(new Mod(ElectricPVP.NAME, ModType.UTIL_QOL, "/assets/electricpvp/dglab.png"), "ElectricPVP.json");
        initialize();

        NetworkAdapter networkInterface = new NetworkAdapter();
        if (address.isEmpty() || (!network.isEmpty() && networkInterface.getNetworkMap().size() == 1)) autoGetNetworkAddress();
        else {
            String address = networkInterface.NICGetaddress(MainConfig.address);
            if (address != null) setAddress(address);
        }
    }

    public static String network = "";

    public void autoGetNetworkAddress(){
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            if(localhost != null) {
                address = localhost.getHostAddress();
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);
                if(networkInterface != null) {
                    network = networkInterface.getDisplayName();
                }
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    public void setAddress(String address) {
        MainConfig.address = address;
        try {
            InetAddress inetAddress = InetAddress.getByName(address);
            network = "";
            if(inetAddress != null) {
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
                if(networkInterface != null) {
                    network = networkInterface.getDisplayName();
                }
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    private final NetworkAdapter adapter = new NetworkAdapter();
    private final LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(adapter.getNetworkMap());

    private void toggleNetworkAdapter(){
        boolean isKeyFound = false;
        for (Map.Entry<String, String> entry : linkedHashMap.entrySet()){
            if(entry.getKey().equals(network)) isKeyFound = true;
            else if (isKeyFound) {
                address = entry.getValue();
                network = entry.getKey();

                ElectricPVP.showMsg("Current IP address: " + address);
                ElectricPVP.showMsg("Current adapter name: " + network);
                DglabClient.stop();
                DglabClient.initialize();
                return;
            }
        }
        Map.Entry<String, String> firstEntry = linkedHashMap.entrySet().iterator().next();
        address = firstEntry.getValue();
        network = firstEntry.getKey();

        ElectricPVP.showMsg("Current IP address: " + address);
        ElectricPVP.showMsg("Current adapter name: " + network);
        DglabClient.stop();
        DglabClient.initialize();
    }
}

