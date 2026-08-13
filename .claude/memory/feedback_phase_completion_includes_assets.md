---
name: feedback-phase-completion-includes-assets
description: "When porting FossilsAndArcheologyRevival, a phase isn't done just because the Java compiles and the server boots — check for matching resource assets AND for stubbed-out functionality (e.g. methods returning null) before declaring it complete"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: c77171aa-03d9-4a9f-b6de-b9700032311e
  modified: 2026-08-09T16:11:01.803Z
---

Don't declare a migration phase "done" based on `compileJava` + `runServer` booting clean alone. Two specific things a clean boot does NOT prove, both caught by the user in the same session:

1. **Resource assets exist.** Check whether the source project has matching textures/blockstates/models/lang/loot tables/tags for what was just ported, and bring the relevant ones over. A dedicated server never loads blockstates/models/textures at all, so a clean `runServer` boot says nothing about client-side rendering.
2. **Ported code isn't secretly a stub.** Grep for `return null;`/no-op placeholder bodies in what was just ported (or what it depends on) before calling it done — a method can compile fine, never throw, and still do nothing.

**Why:** After declaring [[fossils-phase1-blocks-progress]] (Phase 1, Blocks) complete, the user caught two separate gaps in a row: "you forgot all the textures/assets" (asset gap — source's `common/src/main/resources/assets/fossil/` already had 340 textures/86 models/20 blockstates sitting there, just needed a `fossil`→`fossils` namespace rewrite and a copy, which had been skipped), and then "you didn't implement the guis for some of the blocks? like the analyzer?" (stub gap — all 5 machine block entities' `createMenu()` methods were literally `return null;`, inherited from a prior interrupted session, meaning right-clicking any machine block did nothing; this compiled fine and never surfaced in a server boot test because opening a GUI is a client-only interaction the server test never exercised).

**How to apply:** For any future phase in this migration (items, entities, worldgen, etc.):
- After the Java side compiles and boots, explicitly check the source repo's `common/src/main/resources/` (and `common/src/generated/resources/` for datagen output like loot tables/tags) for assets tied to what was just ported.
- Grep the newly-touched files (and their immediate call chains, e.g. `abstract` methods a block entity must override) for `return null`, empty method bodies, or other placeholder implementations, and check whether the CALLER (e.g. `useWithoutItem`'s `instanceof MenuProvider` check, or a `createMenu` override) actually depends on that method doing real work.
- If only partial coverage exists and the rest depends on infrastructure from a later phase (datagen not stood up, `ModRecipes`/`FoodMappings` not ported yet, a custom model loader not implemented), surface that gap explicitly and let the user decide the scope (defer to the later phase / do it now / simplify-but-keep-functional) rather than silently leaving it stubbed or silently doing large unplanned extra work.
- A "done" declaration should describe what was actually exercised by the verification (e.g. "server boots clean" only proves registration, not GUI interactivity or client rendering) rather than implying full functional coverage.
