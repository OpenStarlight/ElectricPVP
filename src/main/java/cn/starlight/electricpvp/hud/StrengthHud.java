package cn.starlight.electricpvp.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import cn.starlight.electricpvp.config.MainConfig;
import cn.starlight.electricpvp.core.DglabClient;
import cn.starlight.electricpvp.core.DglabServer;

import java.util.List;

public class StrengthHud extends TextHud {
    public StrengthHud() {
        super(true);
    }

    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (example) {
            lines.add("A: 114/514");
            lines.add("B: 191/9810");
        } else {
            DglabServer server = DglabClient.webSocketServer;
            if (server != null && server.getConnected()) {
                lines.add("A: " + server.getStrength().getAStrength() + "/" + server.getStrength().getAMaxStrength());
                lines.add("B: " + server.getStrength().getBStrength() + "/" + server.getStrength().getBMaxStrength());
            } else {
                lines.add("Not connected");
            }
        }
    }
}
