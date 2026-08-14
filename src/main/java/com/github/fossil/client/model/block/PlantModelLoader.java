package com.github.fossil.client.model.block;

import com.github.fossil.client.model.block.PlantBlockModel.PlantBlockElement;
import com.github.fossil.client.model.block.PlantBlockModel.PlantBlockElementFace;
import com.github.fossil.client.model.block.PlantBlockModel.PlantBlockFaceUV;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class PlantModelLoader implements IGeometryLoader<PlantModelGeometry> {
    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PlantBlockModel.class, new PlantBlockModel.Deserializer())
            .registerTypeAdapter(PlantBlockElement.class, new PlantBlockElement.Deserializer())
            .registerTypeAdapter(PlantBlockElementFace.class, new PlantBlockElementFace.Deserializer())
            .registerTypeAdapter(PlantBlockFaceUV.class, new PlantBlockFaceUV.Deserializer()).create();

    @Override
    public PlantModelGeometry read(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new PlantModelGeometry(GSON.getAdapter(PlantBlockModel.class).fromJsonTree(jsonObject));
    }
}
