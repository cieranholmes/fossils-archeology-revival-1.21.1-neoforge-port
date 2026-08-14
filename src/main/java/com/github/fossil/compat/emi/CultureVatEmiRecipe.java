package com.github.fossil.compat.emi;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.inventory.CultureVatMenu;
import com.github.fossil.recipe.CultureVatRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

import static com.github.fossil.client.gui.CultureVatScreen.*;

public class CultureVatEmiRecipe extends WithFuelEmiRecipe<CultureVatRecipe> {
    private static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/gui/culture_vat.png");
    private static final EmiTexture EMPTY_FUEL = new EmiTexture(TEXTURE, 81, 36, FUEL_WIDTH, FUEL_HEIGHT + 2);
    private static final EmiTexture FULL_FUEL = new EmiTexture(TEXTURE, 176, 0, FUEL_WIDTH, FUEL_HEIGHT);
    private static final EmiTexture EMPTY_PROGRESS = new EmiTexture(TEXTURE, 78, 22, PROGRESS_WIDTH, PROGRESS_HEIGHT);
    private static final EmiTexture FULL_PROGRESS = new EmiTexture(TEXTURE, 176, FUEL_HEIGHT, PROGRESS_WIDTH, PROGRESS_HEIGHT);

    protected CultureVatEmiRecipe(RecipeHolder<CultureVatRecipe> holder) {
        super(holder);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return FossilEmiPlugin.CULTURE_VAT_CATEGORY;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        widgets.addTexture(EMPTY_FUEL, 41, 20);
        widgets.addAnimatedTexture(FULL_FUEL, 42, 21, recipe.getFuelDuration(), false, true, true).tooltip((mx, my) ->
                List.of(ClientTooltipComponent.create(Component.translatable("emi.cooking.time", recipe.getFuelDuration() / 20f).getVisualOrderText())));

        widgets.addTexture(EMPTY_PROGRESS, 38, 6);
        widgets.addAnimatedTexture(FULL_PROGRESS, 38, 6, CultureVatMenu.CULTIVATION_DURATION, true, false, false).tooltip((mx, my) ->
                List.of(ClientTooltipComponent.create(Component.translatable("emi.cooking.time", CultureVatMenu.CULTIVATION_DURATION / 20f).getVisualOrderText())));
    }
}
