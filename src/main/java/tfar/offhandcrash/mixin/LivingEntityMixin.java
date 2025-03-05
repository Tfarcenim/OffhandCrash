package tfar.offhandcrash.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.offhandcrash.OffhandCrash;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "setHeldItem", at = @At("HEAD"), cancellable = true)
	private void onEquipStack(Hand hand, ItemStack newStack, CallbackInfo info) {
		if ((Object) this != Minecraft.getInstance().player) return;

		if (OffhandCrash.Mod.isAntiCrash()) {
			info.cancel();
		}
	}
}
