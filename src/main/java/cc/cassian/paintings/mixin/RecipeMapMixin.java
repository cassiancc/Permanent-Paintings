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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import static cc.cassian.paintings.PermanentPaintings.addRecipes;

@Mixin(RecipeMap.class)
public abstract class RecipeMapMixin {

	@Inject(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderLookup;listElements()Ljava/util/stream/Stream;"))
	private static void pick(HolderLookup<Recipe<?>> recipes, CallbackInfoReturnable<RecipeMap> cir, @Local(name = "byType") ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType, @Local(name = "byKey") ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey) {
		ArrayList<RecipeHolder<?>> recipeHolders = new ArrayList<>();
		addRecipes(PermanentPaintings.getResourceManager(), recipeHolders, PermanentPaintings.registryAccess());
		recipeHolders.forEach(legacyRecipeHolder -> {
			byType.put((legacyRecipeHolder.value()).getType(), legacyRecipeHolder);
			byKey.put(legacyRecipeHolder.id(), legacyRecipeHolder);
		});
	}


}