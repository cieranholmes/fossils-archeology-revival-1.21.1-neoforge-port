---
name: fossils-modid-rename-to-fossil
description: "2026-08-13: the mod id and Java package were renamed from 'fossils'/com.github.fossils to 'fossil'/com.github.fossil across the whole repo - every earlier memory file that says 'fossils' or 'com.github.fossils' is describing a state that no longer matches the current repo"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6fd81c74-5ff9-40d1-bb1b-4c2acfc62afc
  modified: 2026-08-13T20:04:12.242Z
---

**The mod id is now `fossil` (singular), package `com.github.fossil`.** This supersedes every earlier memory's references to mod id `fossils`/package `com.github.fossils` (see [[project-neoforge-port]] and [[fossils-migration-plan]], both of which recorded `fossils`/`com.github.fossils` as a deliberate "firm decision" — that decision was explicitly reversed by the user on 2026-08-13).

**Why:** user hit a real crash — `IllegalArgumentException: The specified resource 'fossil.mixins.json' was invalid or could not be read` — caused by a pre-existing, previously-uncommitted mismatch: `gradle.properties`'s `mod_id` property was already `fossil` (singular, likely from early Phase 0 setup, sitting as an uncommitted local edit this whole time), while every actual line of code, every resource folder, and the mixins config filename used `fossils` (plural). `src/main/templates/META-INF/neoforge.mods.toml` templates `config="${mod_id}.mixins.json"` off that property, so it was looking for `fossil.mixins.json` while the real file on disk was `fossils.mixins.json`. Asked the user whether to fix this by aligning everything to `fossils` (matching the code) or `fossil` (matching gradle.properties) — **user chose to rename everything to `fossil`**, including explicitly opting into also renaming the Java package (not just the mod id/resource namespace) despite being warned that's a much larger, higher-risk change.

**What changed** (verified compile + boot clean on both `runServer`/`runClient` afterward, RCON-confirmed `fossil:` namespace resolves — see e.g. `/locate structure fossil:aztec_temple`):
- Java package: `src/main/java/com/github/fossils/` → `com/github/fossil/` (514 files moved, every `package`/`import` statement rewritten).
- `FossilsAndArcheologyRevival.MODID` constant: `"fossils"` → `"fossil"`.
- Resource folders: `src/main/resources/data/fossils/` → `data/fossil/`, `assets/fossils/` → `assets/fossil/`.
- Every `"fossils:..."` resource-location string across ~240 JSON/mcmeta files → `"fossil:..."`.
- All 65 structure NBT templates: block-palette strings `fossils:...` → `fossil:...` (via the same `nbt_rename.py` round-trip tool established in [[fossils-phase6-worldgen-progress]]).
- `fossils.mixins.json` → `fossil.mixins.json` (filename + its internal `"package": "com.github.fossils.mixin"` → `"com.github.fossil.mixin"` field — this was the actual root cause).
- `CLAUDE.md`'s "mod id `fossils`" line corrected to `fossil`.
- Wiped `run/world`/`run/logs`/`run/crash-reports`/`run/config` (gitignored, stale saves reference the old namespace and would be incompatible with the renamed mod anyway — same pattern as every world-save wipe in [[fossils-phase6-worldgen-progress]]).

**What deliberately did NOT change** (verified via a boundary-safe regex — `(?<![a-zA-Z0-9_])fossils(?![a-zA-Z0-9_])`, i.e. only touches the standalone lowercase token, immune to camelCase/`snake_case` adjacency — before trusting any future blanket "fossils"→"fossil" find-and-replace, rederive this same boundary logic rather than assuming a naive replace is safe):
- The main class name `FossilsAndArcheologyRevival` and `mod_name`/project name — the user asked to rename the **id**, not the project's display name/class name. `generateFossils`/`GENERATE_FOSSILS` (a config key about whether *fossil ore blocks* generate — a real English word, camelCase-joined so no boundary exists, totally unrelated to the mod id) — a naive replace would have silently corrupted this into `generateFossil`, breaking the persisted config key name.
- ~1094 `.txt` Dinopedia lore files under `assets/fossil/dinopedia/en_us/` — these use "fossils" as ordinary English prose (plural of the word "fossil", describing real paleontology), not the mod id. Confirmed by direct content inspection before excluding them from the rewrite — a plain string-boundary check wasn't enough here since prose can still have "fossils" bounded by punctuation (e.g. "Triceratops fossils that have been found" has "fossils" bounded by spaces, which DOES match the boundary regex) — the `.txt` extension itself was the safe exclusion (only `.json`/`.mcmeta`/`.toml` were ever targeted for the rewrite, never `.txt`/`.png`/`.ogg`/`.nbt` via the text-regex path).
- Internal `TreeGrower` name strings like `"fossils_calamites"` (underscore-joined, so no boundary — the regex naturally left these alone). Cosmetic either way, not worth a special case.
- `.claude/memory/*.md` files — deliberately NOT rewritten; they're a historical record of what was true *at the time*, and rewriting them would be revisionist. This file's existence is the mechanism for flagging the change instead.
- A stray, unrelated pre-existing repo-hygiene issue was noticed but NOT touched (out of scope for this task): `javac.20260812_200845.args` at the repo root is an accidentally-committed, stale javac argfile from an earlier session (references a since-deleted scratchpad path) — harmless dead weight, flag to the user if repo cleanliness ever comes up, but don't fix unprompted.

**How to apply:** for ANY future work, use `fossil`/`com.github.fossil` — not `fossils`/`com.github.fossils`. If a prior memory file (most of them, since this predates 2026-08-13) shows Java code, package paths, or resource locations using `fossils`, treat that as stale notation for the same underlying file/concept, not a discrepancy to "fix back" — the file still exists, just one path segment shorter now.
