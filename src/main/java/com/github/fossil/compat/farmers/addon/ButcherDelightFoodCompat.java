package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import net.mcreator.butchersdelight.init.ButchersdelightModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Ported from source's forge {@code ButcherDelightFoodCompat} (Butcher's Delight Foods addon,
 * source modid {@code butchersdelightfoods}). Drops the {@code FoodMappingsManager} parameter -
 * calls {@link FoodMappings#addMeat} directly. Behavior preserved exactly: every item in the
 * addon's registry is added as meat (safe because literally all Butcher's Delight Foods items are
 * raw/cooked meat, per source's own comment).
 *
 * <p><b>Uncertainty flagged, now partly resolved:</b> the official "Butcher's Delight Foods" mod has
 * NOT published a NeoForge 1.21.1 build (still Forge-only, capped at MC 1.20.1). Ported instead
 * against the third-party <b>unofficial</b> NeoForge 1.21.1 port on CurseForge
 * ({@code butchers-delight-neoforge-port-unofficial}, file {@code butchersdelight-1.21.1-1.0.1.jar}).
 * Verified directly against the resolved jar via javap: the registry class moved from
 * {@code net.mcreator.butchersdelightfoods.init.ButchersdelightfoodsModItems} to
 * {@code net.mcreator.butchersdelight.init.ButchersdelightModItems} (dropped the "foods" suffix
 * throughout), but its {@code REGISTRY} field is still a plain
 * {@code net.neoforged.neoforge.registries.DeferredRegister<Item>} exposing {@code getEntries()},
 * so the loop-over-every-registered-item logic below is unchanged and did not need adjusting for
 * the {@code DeferredHolder} type.
 */
public class ButcherDelightFoodCompat {
    public static void registerFoodMappings() {
        for (DeferredHolder<Item, ? extends Item> meat : ButchersdelightModItems.REGISTRY.getEntries()) {
            FoodMappings.addMeat(meat.get());
        }
        //This is safe to do because literally all Butcher Delight Foods items are raw and cooked meat
    }
}
