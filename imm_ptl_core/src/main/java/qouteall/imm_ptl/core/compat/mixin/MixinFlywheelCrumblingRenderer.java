package qouteall.imm_ptl.core.compat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ClientWorldLoader;

@Pseudo
@Mixin(targets = "com.jozufozu.flywheel.core.crumbling.CrumblingRenderer", remap = false)
public class MixinFlywheelCrumblingRenderer {

//DISABLED_COMPILE    @Inject(
//DISABLED_COMPILE        method = "onReloadRenderers", at = @At("HEAD"),
//DISABLED_COMPILE        cancellable = true
//DISABLED_COMPILE    )
    private static void onInvalidateAll(@Coerce Object obj, CallbackInfo ci) {
        if (ClientWorldLoader.getIsCreatingClientWorld()) {
            ci.cancel();
        }
    }
}
