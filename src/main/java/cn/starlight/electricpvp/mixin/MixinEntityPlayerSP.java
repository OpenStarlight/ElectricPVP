package cn.starlight.electricpvp.mixin;

import cn.starlight.electricpvp.config.MainConfig;
import cn.starlight.electricpvp.core.DglabClient;
import cn.starlight.electricpvp.core.DglabServer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Unique
    private static float dg_labHealth = 0f;

    @Unique
    private static void electricPVP$updateDglabHealth(float health) {
        DglabServer server = DglabClient.webSocketServer;
        if (server != null && server.getConnected() && MainConfig.INSTANCE.enabled) {
            float damage = dg_labHealth - health;

            if (damage > 0.0F) {
                server.setDelayTime(MainConfig.aDecreaseDelay, MainConfig.bDecreaseDelay);
                if (MainConfig.aIncrease > 0) server.sendStrengthToClient(Math.max(1, ((int) (damage * MainConfig.aIncrease))), 1, 1);
                if (MainConfig.bIncrease > 0) server.sendStrengthToClient(Math.max(1, ((int) (damage * MainConfig.bIncrease))), 1, 2);
            }
            dg_labHealth = health;
        }
    }

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setHealth(F)V", shift = At.Shift.AFTER))
    private void onDamage(DamageSource damageSrc, float damageAmount, CallbackInfo ci) {
        float currentHealth = ((EntityPlayerSP) (Object) this).getHealth();
        electricPVP$updateDglabHealth(currentHealth - damageAmount);
    }

    @Inject(method = "setPlayerSPHealth", at = @At(value = "TAIL"))
    private void onSetPlayerSPHealth(float health, CallbackInfo ci) {
        electricPVP$updateDglabHealth(health);
    }
}
