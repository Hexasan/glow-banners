package io.github.ultrusbot.glowingbanners.mixin;

import io.github.ultrusbot.glowingbanners.GlowingBannerInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BannerBlockEntity.class)
public abstract class GlowingBannerMixin extends BlockEntity implements GlowingBannerInterface {

    private boolean isGlowing = false;

    public GlowingBannerMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("TAIL"))
    public void addGlowingInit(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.isGlowing = false;
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeGlowingNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putBoolean("isGlowing", isGlowing);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readIsGlowingNbt(NbtCompound nbt, CallbackInfo ci) {
        isGlowing = nbt.getBoolean("isGlowing");
    }
    @Override
    public void setGlowing(boolean glowing) {
        isGlowing = glowing;
        this.markDirty();
        this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);

    }

    @Inject(method = "getPickStack", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT    )
    public void addGlowingToPickStack(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if (isGlowing) {
            itemStack.getOrCreateSubNbt("BlockEntityTag").putBoolean("isGlowing", isGlowing);
        }
    }

    @Override
    public boolean isGlowing() {
        return isGlowing;
    }

}