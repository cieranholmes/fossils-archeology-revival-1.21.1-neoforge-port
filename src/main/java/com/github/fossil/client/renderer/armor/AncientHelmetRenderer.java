package com.github.fossil.client.renderer.armor;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.item.AncientHelmetItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AncientHelmetRenderer extends GeoArmorRenderer<AncientHelmetItem> {
    public AncientHelmetRenderer() {
        super(new DefaultedItemGeoModel<>(FossilsAndArcheologyRevival.location("armor/ancient_helmet")));
    }
}
