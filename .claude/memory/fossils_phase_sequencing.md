---
name: fossils-phase-sequencing
description: "Phase 2 (Items) was reordered after Phase 4 (Entities) once item-system entity coupling was discovered — read this before assuming the plan's Phase 1->2->3... order is still literal"
metadata: 
  node_type: memory
  type: project
  originSessionId: 5e9ab82f-8e0e-4d97-bf5f-7ef8d9856b39
  modified: 2026-08-12T18:49:16.910Z
---

[[fossils-migration-plan]]'s phase numbering is no longer the literal execution order. On 2026-08-09, Phase 2 (Items) was started and immediately paused after discovering the item system is far more entity-coupled than the plan assumed:

- Dynamically-generated items (DNA items, dino bone items, egg items, meat items, spawn eggs) aren't statically listed in source's `ModItems.java` at all — they're generated per-species by `PrehistoricEntityInfo.register()`/`VanillaEntityInfo.register()`, iterating over entity definitions that don't exist yet in the port.
- ~20 item classes directly reference entity classes: `JavelinItem`/`LaserPointerItem`/`StoneTabletItem` spawn projectile/display entities, `WhipItem`/`AncientSwordItem` interact with `Prehistoric`/`FriendlyPiglin`, `ToyBallItem`/`ToyScratchingPostItem`/`ToyTetheredLogItem` spawn toy entities, `FossilItem` spawns a `PrehistoricSkeleton` display entity, `CustomSpawnEggItem` wraps an `EntityType`.
- Even `ModToolTiers.TOOTH_DAGGER`'s repair ingredient references `PrehistoricEntityInfo.TYRANNOSAURUS.uniqueBoneItem`.

**Why:** user was given 3 options (stub entity-coupled items now and backfill after Phase 4 / do Phase 4 first then finish items / permanently simplify away the entity hooks) and explicitly chose **"do Phase 4 first"** (2026-08-09) — reasoning: avoids writing item code twice (once as stubs, once for real).

**How to apply:** treat Phase 4 (Entities & AI, ~199 source files, the largest single chunk in [[fossils-migration-inventory]]) as the next active phase, NOT Phase 2. Phase 2's items work (this session touched only exploration, no files were written/committed for items yet — `ModItems.java` in the target repo is still just the `TAR_BUCKET` stub from Phase 0/1) resumes only after Phase 4 lands real entity classes to hook into. Phase 2's entity-independent pieces (materials/armor/tools minus the tooth-dagger repair ingredient, food/loot/villager/tags/advancements, block items, decorative items, records) were NOT started under this decision — they were left for after Phase 4 too, since the user chose the full reorder option rather than the "do the independent parts now" option. Don't assume any Phase 2 subsystem is in-progress until you check the target repo's actual file state.

**Update 2026-08-12: Phase 4 is now COMPLETE** (see [[fossils-phase4-entities-progress]]'s final session log) — every entity class (species, monster, Anu boss, toys, projectiles) is real, registered, rendered, and verified live in-game. The condition this memory was tracking has been satisfied: **Phase 2 (Items) is now the recommended next phase.** `ModItems.java` has grown a handful of small placeholder fields added opportunistically during Phase 4 (`MAGIC_CONCH`, `ELASMOTHERIUM_FUR`, `MAMMOTH_FUR`, `ANCIENT_SWORD`/`ANCIENT_HELMET`/`ANCIENT_KEY` — the sword/helmet are plain vanilla-backed placeholders, not the real bespoke item classes) but is still fundamentally unstarted as a phase — check the target repo's actual `ModItems.java` state before assuming any real Items-phase subsystem exists.
