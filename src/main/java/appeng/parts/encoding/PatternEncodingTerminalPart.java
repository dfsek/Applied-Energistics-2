/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
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

package appeng.parts.encoding;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.helpers.IPatternTerminalLogicHost;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.items.parts.PartModels;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;

public class PatternEncodingTerminalPart extends AbstractTerminalPart
        implements IPatternTerminalLogicHost, IPatternTerminalMenuHost {

    @PartModels
    public static final ResourceLocation MODEL_OFF = new ResourceLocation(AppEng.MOD_ID,
            "part/pattern_encoding_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = new ResourceLocation(AppEng.MOD_ID,
            "part/pattern_encoding_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private final PatternEncodingLogic logic = new PatternEncodingLogic(this);

    public PatternEncodingTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        for (var is : this.logic.getBlankPatternInv()) {
            drops.add(is);
        }
        for (var is : this.logic.getEncodedPatternInv()) {
            drops.add(is);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.logic.getBlankPatternInv().clear();
        this.logic.getEncodedPatternInv().clear();
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);

        logic.readFromNBT(data);
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        logic.writeToNBT(data);
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return PatternEncodingTermMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public PatternEncodingLogic getLogic() {
        return logic;
    }

    @Override
    public void markForSave() {
        getHost().markForSave();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == Capabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> logic.getBlankPatternInv().toItemHandler()).cast();
        }
        return super.getCapability(cap);
    }
}
