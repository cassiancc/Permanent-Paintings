package cc.cassian.paintings;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

public class PermanentPaintingsTags {
	public static TagKey<PaintingVariant> HIDDEN_FROM_STONECUTTER = TagKey.create(Registries.PAINTING_VARIANT, PermanentPaintings.id("hidden_from_stonecutter"));
}
