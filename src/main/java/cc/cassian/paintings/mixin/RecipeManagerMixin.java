package cc.cassian.paintings.mixin;

import cc.cassian.paintings.PermanentPaintings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import static cc.cassian.paintings.PermanentPaintings.addRecipes;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

	@Inject(method = "<init>", at = @At(value = "HEAD", target = "Lnet/minecraft/core/HolderLookup;listElements()Ljava/util/stream/Stream;"))
	private static void pick(HolderLookup.Provider registries, CallbackInfo ci) {
		PermanentPaintings.registries = registries;
	}


}