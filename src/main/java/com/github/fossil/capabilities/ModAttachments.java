package com.github.fossil.capabilities;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.EntityInfo;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FossilsAndArcheologyRevival.MODID);

    private static final StreamCodec<ByteBuf, MammalData> MAMMAL_DATA_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, data -> data.getEmbryo() == null ? "" : data.getEmbryo().name(),
            ByteBufCodecs.VAR_INT, MammalData::getEmbryoProgress,
            (embryoName, progress) -> new MammalData(embryoName.isEmpty() ? null : EntityInfo.fromNbt(embryoName), progress)
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MammalData>> MAMMAL_DATA = ATTACHMENT_TYPES.register("mammal_data",
            () -> AttachmentType.serializable(MammalData::new).sync(MAMMAL_DATA_STREAM_CODEC).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HAS_HATCHED_DINOSAUR = ATTACHMENT_TYPES.register("has_hatched_dinosaur",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().sync(ByteBufCodecs.BOOL).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
