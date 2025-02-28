/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.recipes.handlers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import appeng.core.AppEng;
import appeng.init.InitRecipeTypes;

public class InscriberRecipe implements Recipe<Container> {

    private static final Codec<InscriberProcessType> MODE_CODEC = ExtraCodecs.stringResolverCodec(
            mode -> switch (mode) {
            case INSCRIBE -> "inscribe";
            case PRESS -> "press";
            },
            mode -> switch (mode) {
            default -> InscriberProcessType.INSCRIBE;
            case "press" -> InscriberProcessType.PRESS;
            });

    public static final Codec<InscriberRecipe> CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            Ingredient.CODEC_NONEMPTY.fieldOf("middle")
                                    .fieldOf("ingredients").forGetter(ir -> ir.middleInput),
                            CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(ir -> ir.output),
                            ExtraCodecs.strictOptionalField(Ingredient.CODEC, "top", Ingredient.EMPTY)
                                    .fieldOf("ingredients").forGetter(ir -> ir.topOptional),
                            ExtraCodecs.strictOptionalField(Ingredient.CODEC, "bottom", Ingredient.EMPTY)
                                    .fieldOf("ingredients").forGetter(ir -> ir.bottomOptional),
                            MODE_CODEC.fieldOf("mode").forGetter(ir -> ir.processType))
                    .apply(builder, InscriberRecipe::new));

    public static final ResourceLocation TYPE_ID = AppEng.makeId("inscriber");

    public static final RecipeType<InscriberRecipe> TYPE = InitRecipeTypes.register(TYPE_ID.toString());

    private final Ingredient middleInput;
    private final Ingredient topOptional;
    private final Ingredient bottomOptional;
    private final ItemStack output;
    private final InscriberProcessType processType;

    public InscriberRecipe(Ingredient middleInput, ItemStack output,
            Ingredient topOptional, Ingredient bottomOptional, InscriberProcessType processType) {
        this.middleInput = middleInput;
        this.output = output;
        this.topOptional = topOptional;
        this.bottomOptional = bottomOptional;
        this.processType = processType;
    }

    @Override
    public boolean matches(Container inv, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        return getResultItem(registryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return InscriberRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.topOptional);
        ingredients.add(this.middleInput);
        ingredients.add(this.bottomOptional);
        return ingredients;
    }

    public Ingredient getMiddleInput() {
        return middleInput;
    }

    public Ingredient getTopOptional() {
        return topOptional;
    }

    public Ingredient getBottomOptional() {
        return bottomOptional;
    }

    public InscriberProcessType getProcessType() {
        return processType;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
