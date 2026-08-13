package com.github.fossils.client.model;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.entity.prehistoric.base.PrehistoricFish;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PrehistoricFishGeoModel<T extends PrehistoricFish> extends DefaultedEntityGeoModel<T> implements AdditiveAnimationModel {

    public PrehistoricFishGeoModel(String assetName) {
        super(FossilsAndArcheologyRevival.location(assetName), false);
        withAltTexture(FossilsAndArcheologyRevival.location(assetName + "/texturemap"));
    }
}
