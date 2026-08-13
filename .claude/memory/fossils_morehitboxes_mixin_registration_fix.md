---
name: fossils-morehitboxes-mixin-registration-fix
description: "Corrects the earlier 'likely systemic Mixin JAVA_21 gap' diagnosis - more-hitboxes' mixins were never loading because its own jar's neoforge.mods.toml never declared a [[mixins]] block at all (a packaging bug, not a bytecode-compatibility one); fixed via a build.gradle task that patches the vendored jar"
metadata:
  node_type: memory
  type: project
  originSessionId: 78d0a1c6-3588-48a6-800c-16a36b318da9
  modified: 2026-08-13T07:54:59.561Z
---

Found while chasing "can't hit the Tyrannosaurus on the head" (and a regression where a partial fix made hitting the body worse too). Corrects [[fossils-combat-and-scaling-bugs-fixed]]'s Bug #3, which attributed `more-hitboxes`' broken mixins to a "likely systemic Mixin JAVA_21 compatibility gap" shared with GeckoLib/TerraBlender. That gap **is real** for GeckoLib/TerraBlender/NeoForge's own mixins (confirmed via `run/logs/debug.log`: `"Class version 65 required is higher than the class version supported by the current version of Mixin (JAVA_17 supports class version 61)"` for specific classes in those mods) - but it is **not** why `more-hitboxes`' mixins were inert.

## The actual root cause

Unzipped the resolved `morehitboxes-neoforge-1.21.1-1.9.4-alpha.jar` directly and compared its `META-INF/neoforge.mods.toml` against GeckoLib's and TerraBlender's. Both of those correctly declare:
```
[[mixins]]
    config = "geckolib.mixins.json"
```
`more-hitboxes`' own `neoforge.mods.toml` has **no `[[mixins]]` block at all** - just `[[mods]]` and `[[dependencies.morehitboxes]]` entries. `morehitboxes.mixins.json` sits at the jar root, fully valid (`compatibilityLevel: JAVA_21`, 9 common + 7 client mixins), but NeoForge's mod loader has no way to discover it. Confirmed directly: `grep -n "morehitboxes.mixins.json" run/logs/debug.log` returned **nothing** before the fix (not even a "Compatibility level too high" downgrade message, unlike `terrablender.mixins.json`/`geckolib.mixins.json`/`fossils.mixins.json`, which all appear). This is a genuine upstream packaging bug in this `-alpha` release - plausibly the author wired mixin registration correctly for Fabric's `fabric.mod.json` (`"mixins"` array) but forgot the equivalent NeoForge TOML declaration.

**Why this was missed before:** an earlier session saw the generic "Class version 65...JAVA_17 supports class version 61" messages affecting GeckoLib/TerraBlender/NeoForge's own mixins in the same log and reasonably (but incorrectly) generalized that more-hitboxes' mixins were failing for the identical reason, without directly confirming `morehitboxes.mixins.json` was even being loaded at all.

## The fix

`build.gradle` no longer depends on `com.github.darkpred.morehitboxes:morehitboxes-neoforge-...` directly. Instead:
- `moreHitboxesOriginal` - a `detachedConfiguration` resolving the original coordinate (not on any compile/runtime classpath).
- `extractMoreHitboxes` (`Copy` from `zipTree(...)`) → `patchMoreHitboxesToml` (appends `[[mixins]]\nconfig="morehitboxes.mixins.json"\n` to the extracted TOML if not already present) → `patchedMoreHitboxesJar` (`Zip` task repacking the patched directory).
- `implementation files(patchedMoreHitboxesJar)` replaces the old coordinate-based `implementation`.

Verified via a full `runServer` + RCON boot: `run/logs/debug.log` now shows `"Mixing EntityMixin/PlayerMixin/LevelMixin/NeoForgeLevelMixin/MobMixin/NeoForgeMobMixin/EntityHitResultMixin from morehitboxes.mixins.json into ..."` with **zero** class-version-65 rejections for any of them (unlike GeckoLib/TerraBlender, whose *some* specific mixin classes do still hit that separate, real compatibility issue) - and a temporary debug print confirmed `EntityMixin#updateBounds` (attack-bounds refresh) and `MobMixin#tickCustomParts` (per-tick part positioning) now fire automatically with zero manual workaround code. Both manual `aiStep()` workarounds from [[fossils-combat-and-scaling-bugs-fixed]] Bug #3 (the `hitboxData.makeAttackBounds()` call, and a since-reverted `updatePosition()` loop) were removed from `Prehistoric.java` as a result - they're now redundant.

**Reusable pattern:** when a vendored third-party mod jar's mixins/other JAR-embedded config seem inert, don't assume a generic "Mixin/Java-version compatibility" explanation just because a *different* mod in the same log hits that exact error - unzip the specific jar and check whether its own `neoforge.mods.toml` actually declares the config at all. `grep`-ing the debug log for the config filename itself (not just the symptom) is the fastest way to tell "never discovered" apart from "discovered but rejected."

## A second, independent gotcha found along the way (informational, not something to fix)

Before finding the real root cause, a manual reimplementation of `MobMixin#tickCustomParts` (calling `part.updatePosition()` every tick without the matching `TransientEntitySectionManagerMixin` that keeps parts in the real spatial index) made melee hit-detection **worse**, not better - once part entities actually track the parent's position, they become extra overlapping hit-candidates for vanilla's `ProjectileUtil.getEntityHitResult`, which has a known ambiguous, iteration-order-dependent tie-break when the player's eye position falls inside more than one candidate's (inflated) bounding box - very likely at melee range against something dinosaur-sized. `more-hitboxes` avoids this entirely by inserting parts into the level's real `EntitySectionStorage` via `TransientEntitySectionManagerMixin`, which a flat `Level.getPartEntities()`-only fallback does not replicate. **Worth remembering for any future manual mixin-behavior reimplementation**: partially replicating a multi-mixin subsystem (fixing positioning without fixing spatial registration) can be worse than fixing nothing - prefer getting the *real* mixin loading (as done here) over hand-rolling pieces of it.
