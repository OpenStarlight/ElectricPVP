package cn.starlight.electricpvp.core.tool;


class WaveformPairs {
    private int time, strength;

    public int getTime() { return time; }

    public int getStrength() { return strength; }

    public void setTime(int time) {
        this.time = Math.max(time, 0);
    }

    public void setStrength(int strength) {
        strength = Math.min(100, Math.max(strength, 0));
    }
}