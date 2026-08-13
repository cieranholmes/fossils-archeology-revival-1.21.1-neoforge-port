package com.github.fossils.client;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

import java.util.Map;

public abstract class ResourceLoader<T> extends SimplePreparableReloadListener<T> {
    private final PackType packType;
    private final String modId;
    protected final String directory;
    protected final String suffix;

    protected ResourceLoader(PackType packType, String directory, String suffix) {
        this.packType = packType;
        this.modId = FossilsAndArcheologyRevival.MODID;
        this.directory = directory;
        this.suffix = suffix;
    }

    /**
     * Returns a collection of paths limited to the directory in the namespace of the mod
     */
    protected Map<ResourceLocation, Resource> listResources(ResourceManager resourceManager) {
        return listResources(resourceManager, suffix);
    }

    protected Map<ResourceLocation, Resource> listResources(ResourceManager resourceManager, String suffix) {
        return resourceManager.listResources(directory, fileName -> fileName.getPath().endsWith(suffix));
    }
}
