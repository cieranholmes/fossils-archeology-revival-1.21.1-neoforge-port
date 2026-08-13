package com.github.fossil.world.feature.structures;

import net.minecraft.world.level.levelgen.structure.StructureType;

/**
 * {@code StructureType} is a "simple" code registry ({@code BuiltInRegistries.STRUCTURE_TYPE}), not a
 * data-driven one - NeoForge only allows registering into it during {@code RegisterEvent}'s dispatch
 * window for that specific registry (it's frozen everywhere else, including at mod-construction time).
 * So these are just plain instances here; the actual {@code Registry.register} calls happen in
 * {@code FossilsAndArcheologyRevival}'s {@code RegisterEvent} listener.
 */
public class ModStructureType {
    public static final StructureType<AnuCastleStructure> ANU_CASTLE = () -> AnuCastleStructure.CODEC;
    public static final StructureType<HellBoatStructure> HELL_BOAT = () -> HellBoatStructure.CODEC;
    public static final StructureType<TreasureRoomStructure> TREASURE_ROOM = () -> TreasureRoomStructure.CODEC;
}
