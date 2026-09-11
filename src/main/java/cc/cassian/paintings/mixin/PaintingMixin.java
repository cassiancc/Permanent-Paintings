package cc.cassian.paintings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static cc.cassian.paintings.PermanentPaintings.CONFIG;

@Mixin(Painting.class)
public abstract class PaintingMixin extends HangingEntity {
	@Shadow
	public abstract Holder<PaintingVariant> getVariant();

	protected PaintingMixin(EntityType<? extends HangingEntity> type, Level level) {
		super(type, level);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/painting/Painting;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"), method = "dropItem")
	private ItemEntity init(Painting instance, ServerLevel serverLevel, ItemStack stack, Operation<ItemEntity> original) {
		if (!CONFIG.paintingsDropWithVariant) return original.call(instance, serverLevel, stack);
		var variant = instance.getVariant();
		stack.set(DataComponents.PAINTING_VARIANT, variant);
		return spawnAtLocation(serverLevel, stack);
	}

	@ModifyReturnValue(method = "getPickResult", at = @At(value = "RETURN"))
	private ItemStack pick(ItemStack original) {
		if (CONFIG.paintingsDropWithVariant)
			original.set(DataComponents.PAINTING_VARIANT, this.getVariant());
		return original;
	}
}