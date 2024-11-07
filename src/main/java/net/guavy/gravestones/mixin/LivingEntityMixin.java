package net.guavy.gravestones.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.compat.TrinketsCompat;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(at = @At(value = "HEAD"), method = "drop", cancellable = true)
    private void onDrop(DamageSource damageSource, CallbackInfo ci) {
        if (GravestonesConfig.getConfig().mainSettings.enableGraves && ((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            Gravestones.placeGrave(this.getWorld(), this.getPos(), player);

            if (FabricLoader.getInstance().isModLoaded("trinkets"))
                TrinketsCompat.dropAll(player);

            ci.cancel();
        }
    }
}
