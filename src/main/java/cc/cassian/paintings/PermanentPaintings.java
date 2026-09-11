package cc.cassian.paintings;

import cc.cassian.mru.Platform;
import cc.cassian.mru.events.CommonRegisterEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PermanentPaintings implements CommonRegisterEvent {

	public static final String MOD_ID = "permanent_paintings";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModConfig CONFIG = ModConfig.createToml(Platform.INSTANCE.configPath(), "", MOD_ID, ModConfig.class);
	public static HolderLookup.Provider registries;
	public static ResourceManager resourceManager;

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void addRecipes(ResourceManager manager, List<RecipeHolder<?>> recipeHolders, HolderLookup.Provider registries) {
		if (!CONFIG.stonecutterRecipes) return;
		manager.listResources("painting_variant", (p)->p.getPath().endsWith(".json")).forEach((identifier, resource) -> {
			var painting = identifier.withPath(p->p.replace(".json", "").replace("painting_variant/", ""));
			Holder.Reference<PaintingVariant> variantReference = registries.getOrThrow(ResourceKey.create(Registries.PAINTING_VARIANT, painting));
			if (variantReference.is(PaintingVariantTags.PLACEABLE) && !variantReference.is(PermanentPaintingsTags.HIDDEN_FROM_STONECUTTER))
				recipeHolders.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, PermanentPaintings.id(painting.getNamespace() + "/"+painting.getPath())), new StonecutterRecipe(new Recipe.CommonInfo(false), Ingredient.of(Items.PAINTING), new ItemStackTemplate(Items.PAINTING, DataComponentPatch.builder().set(DataComponents.PAINTING_VARIANT, variantReference).build()))));
		});
	}

	public static HolderLookup.Provider registryAccess() {
		return registries;
	}

	public static ResourceManager getResourceManager() {
		return resourceManager;
	}

	@Override
	public void onInitialize() {
	}
}
