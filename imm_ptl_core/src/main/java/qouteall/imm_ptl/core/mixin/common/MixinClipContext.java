package qouteall.imm_ptl.core.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.ducks.IERayTraceContext;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;

@Mixin(ClipContext.class)
public abstract class MixinClipContext implements IERayTraceContext {
    @Shadow
    @Final
    @Mutable
    private Vec3 from;
    
    @Shadow
    @Final
    @Mutable
    private Vec3 to;
    
    @Shadow
    @Final
    private ClipContext.Block block;
    
    @Shadow
    @Final
    private CollisionContext collisionContext;
    
    @Override
    public IERayTraceContext setStart(Vec3 newStart) {
        from = newStart;
        return this;
    }
    
    @Override
    public IERayTraceContext setEnd(Vec3 newEnd) {
        to = newEnd;
        return this;
    }
    
    // portal placeholder does not have outline if colliding with portal
    // placeholder blocks entity view
    @Inject(
        at = @At("HEAD"),
        method = "getBlockShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
        cancellable = true
    )
    private void onGetBlockShape(
        BlockState blockState,
        BlockGetter blockView,
        BlockPos blockPos,
        CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (blockState.getBlock() == IPRegistry.NETHER_PORTAL_BLOCK.get()) {
            if (block == ClipContext.Block.OUTLINE) {
                if (blockView instanceof Level) {
                    boolean isIntersectingWithPortal = McHelper.getEntitiesRegardingLargeEntities(
                        (Level) blockView, new AABB(blockPos),
                        10, Portal.class, e -> true
                    ).isEmpty();
                    if (!isIntersectingWithPortal) {
                        cir.setReturnValue(Shapes.empty());
                    }
                }
            }
            else if (block == ClipContext.Block.COLLIDER) {
                cir.setReturnValue(IPRegistry.NETHER_PORTAL_BLOCK.get().getShape(
                    blockState, blockView, blockPos, collisionContext
                ));
            }
        }
    }
}
