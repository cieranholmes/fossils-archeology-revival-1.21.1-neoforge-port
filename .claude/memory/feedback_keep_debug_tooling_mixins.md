---
name: feedback-keep-debug-tooling-mixins
description: "Don't drop the dev-only debug-tooling mixins (DebugLevelRendererMixin, DebugMinecraftMixin, DebugMouseHandlerMixin, ChunkStatusMixin) during the Phase 8 mixin cleanup pass - keep them for now"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 5a1274e9-916b-41f4-89dd-97a9cc4087b3
  modified: 2026-08-14T12:48:28.022Z
---

[[fossils-migration-plan]]'s original Phase 8 scoping recommended dropping the 4 dev-only debug-tooling mixins (`DebugLevelRendererMixin`, `DebugMinecraftMixin`, `DebugMouseHandlerMixin`, `ChunkStatusMixin`) as part of the mixin cleanup pass, on the reasoning that they're dev-only and not gameplay-relevant. User overrode this on 2026-08-14: "drop the 'drop outright' from the plan. we should keep those for now."

**Why:** not stated explicitly by the user beyond the direct instruction - treat as a standing preference to keep dev tooling available rather than prune it preemptively, possibly because the debug/pathing GUI subsystem was deliberately ported in full in [[fossils-phase5-debug-gui-progress]] (a deviation from this same plan's original "drop it" recommendation for that subsystem too) - there's a pattern of the user preferring to keep debug/dev tooling around rather than cut it for scope reduction.

**How to apply:** when Phase 8 (mixin cleanup) actually starts, do not remove these 4 mixins. Audit/port them like any other mixin (check for 1.21.1 API drift, Java 21 mixin compatibility) but don't treat "it's just dev tooling" as sufficient reason to drop them. If a mixin in this category turns out to be broken/non-functional after porting, that's a separate conversation (fix or drop due to being broken) - distinct from dropping it preemptively for being unnecessary.
