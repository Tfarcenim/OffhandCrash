package tfar.offhandcrash.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class ForceServerMixin {
    @Inject(method = "isMultiplayerEnabled",at = @At("HEAD"),cancellable = true)
    private void fix(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
