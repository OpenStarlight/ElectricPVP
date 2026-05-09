package cn.starlight.electricpvp.core.util;


import net.minecraft.util.DamageSource;

public class Damage {
    private float value;
    private DamageSource damageSource;

    public Damage() {
    }

    public Damage(int value, DamageSource damageSource) {
        this.value = value;
        this.damageSource = damageSource;
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }
}