package com.github.fossil.compat.jade;

import com.github.fossil.block.ModBlocks;
import com.github.fossil.block.custom_blocks.FeederBlock;
import com.github.fossil.block.entity.FeederBlockEntity;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class FossilJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(PrehistoricBreedingProvider.INSTANCE, Prehistoric.class);
        registration.registerEntityDataProvider(PrehistoricGrowthProvider.INSTANCE, Prehistoric.class);
        registration.registerBlockDataProvider(FeederStatusProvider.INSTANCE, FeederBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(FeederStatusProvider.INSTANCE, FeederBlock.class);
        registration.hideTarget(ModBlocks.ANU_BARRIER_FACE.get());
        registration.hideTarget(ModBlocks.ANU_BARRIER_ORIGIN.get());
    }
}
