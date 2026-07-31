package cc.cassian.paintings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static cc.cassian.paintings.PermanentPaintings.addRecipes;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	@Shadow
	@Final
	private HolderLookup.Provider registries;

	@Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Ljava/lang/Iterable;)Lnet/minecraft/world/item/crafting/RecipeMap;"))
	private void pick(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir, @Local(name = "recipeHolders") List<RecipeHolder<?>> recipeHolders) {
		addRecipes(manager, recipeHolders, registries);
	}


}