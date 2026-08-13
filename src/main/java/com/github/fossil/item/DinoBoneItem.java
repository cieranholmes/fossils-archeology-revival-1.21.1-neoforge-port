package com.github.fossil.item;

import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;

import java.util.function.Consumer;

public class DinoBoneItem extends PrehistoricEntityItem {

    public DinoBoneItem(PrehistoricEntityInfo info, String boneType) {
        super(info, "bone_unique".equals(boneType) ? "bone_unique_" + info.resourceName : boneType);
    }

    //NeoForge's DeferredRegister has no equivalent to Architectury's DeferredRegister#listen callback - the
    //listener is invoked directly inside the item factory as a side effect, at the exact point the concrete
    //item is constructed (during the ITEM RegisterEvent), which achieves the same "populate this field once
    //the real item exists" effect.
    public static void registerItem(String boneType, PrehistoricEntityInfo info, Consumer<DinoBoneItem> listener) {
        var holder = ModItems.ITEMS.register(boneType + "_" + info.resourceName, () -> {
            DinoBoneItem item = new DinoBoneItem(info, boneType);
            listener.accept(item);
            return item;
        });
        ModTabs.addToMobItemTab(holder);
    }
}
