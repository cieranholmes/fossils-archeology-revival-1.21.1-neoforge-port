package com.github.fossil.client.model;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricFish;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PrehistoricFishGeoModel<T extends PrehistoricFish> extends DefaultedEntityGeoModel<T> implements AdditiveAnimationModel {

    public PrehistoricFishGeoModel(String assetName) {
        super(FossilsAndArcheologyRevival.location(assetName), false);
        withAltTexture(FossilsAndArcheologyRevival.location(assetName + "/texturemap"));
    }
}
