---
name: fossils-builtin-entity-item-renderer-missing
description: "2026-08-15, user-confirmed fixed: anu_statue/anubite_statue/ancient_chest/sarcophagus held-item textures were completely invisible (not missing-texture) because their client-side BlockEntityWithoutLevelRenderer was never ported from the 1.20.1 source mod"
metadata:
  type: project
---

Found via live user testing, same session as [[fossils-dinopedia-blur-and-sleep-scale-bugs-fixed]].

## Symptom

Four items - `anu_statue`, `anubite_statue`, `ancient_chest`, `sarcophagus` - rendered as literally nothing when held/in GUI slots. Not the purple/black missing-texture checkerboard - just empty space.

## Cause

All four item models (`assets/fossil/models/item/*.json`) declare `"parent": "builtin/entity"` - the same mechanism vanilla uses for chests, beds, shulker boxes, banners, decorated pots, etc. `builtin/entity` bakes **zero quads of its own**; the item's entire visual appearance is delegated to a client-registered `BlockEntityWithoutLevelRenderer` (BEWLR) via `IClientItemExtensions.getCustomRenderer()`. If nothing is registered for that item, nothing renders - there is no missing-texture fallback for this model type, unlike a broken/missing normal item texture.

The 1.20.1 source mod has exactly this: `client/renderer/CustomItemRenderer.java` (a `BlockEntityWithoutLevelRenderer` singleton holding dummy off-world `BlockEntity` instances for the 4 blocks, dispatching to `BlockEntityRenderDispatcher.renderItem(...)`), registered per-item via `Item.initializeClient(...)` in each loader's `*BlockItemImpl` class (Forge-specific in the source's multi-loader split). **This renderer class and its registration were never ported** - `ModBlocks.java`'s `blockWithCustomBlockItem(...)` helper for these 4 blocks only ever used `CustomBlockItem` (which just overrides `getName()`, unrelated to rendering), so nothing in the NeoForge target ever registered a client item renderer for them at all.

## Fix

- Ported `com.github.fossil.client.renderer.CustomItemRenderer` verbatim (same dummy-BlockEntity-dispatch approach).
- Registered it in `ClientModEvents.registerClientExtensions` (the `RegisterClientExtensionsEvent` handler that already existed for the tar fluid type) via `event.registerItem(IClientItemExtensions, Item...)`, targeting `ModBlocks.{ANU_STATUE,ANUBITE_STATUE,ANCIENT_CHEST,SARCOPHAGUS}.get().asItem()`. NeoForge's modern API is a single `RegisterClientExtensionsEvent` call rather than source's per-loader `Item.initializeClient()` override - cleaner, no `BlockItem` subclass needed for this purpose.

## Reusable insight

Any custom item with a `"builtin/entity"` model parent needs an explicit `IClientItemExtensions.getCustomRenderer()` registration or it silently renders as nothing (no missing-texture placeholder, unlike ordinary broken item textures) - grep item model JSONs for `"builtin/entity"` and cross-check against `RegisterClientExtensionsEvent` registrations if a similar "item is just invisible" report comes up for another block-item.
