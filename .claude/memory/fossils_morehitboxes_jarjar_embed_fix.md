---
name: fossils-morehitboxes-jarjar-embed-fix
description: "The more-hitboxes mixin-registration fix (adding [[mixins]] to its patched jar's TOML) only ever reached the Gradle dev classpath (runClient/runServer), never the built/shipped mod jar - fixed by embedding the patched jar via NeoForge's Jar-in-Jar mechanism. CONFIRMED 2026-08-15 live: the jarJar embed itself works correctly, but a separately-installed morehitboxes jar in mods/ silently overrides it - must be removed for combat to work."
metadata:
  type: project
---

Found 2026-08-15 when the user reported: "entities not damaging others" is fixed under `runClient` but still present when building the jar and dropping it into a real Minecraft instance. Corrects/extends [[fossils-morehitboxes-mixin-registration-fix]].

## Root cause

[[fossils-morehitboxes-mixin-registration-fix]] patches more-hitboxes' jar (adds a missing `[[mixins]]` TOML entry) and consumes the patched jar via a plain `implementation files(patchedMoreHitboxesJar)` Gradle dependency. That only puts the patched jar on the **dev-time classpath** (`runClient`/`runServer` auto-discover every `implementation` jar as a mod candidate). It does **not** put it inside the actual built mod jar - `implementation` deps are never bundled into `jar`/`build`'s output.

Compounding this: `neoforge.mods.toml` (`src/main/templates/META-INF/neoforge.mods.toml`) declares `morehitboxes` as a separate `required` mod dependency, exactly like GeckoLib/TerraBlender - meaning the mod expects the *user* to separately install a `morehitboxes` jar in their instance's `mods/` folder. Whatever they get from Modrinth/CurseForge is the original, unpatched, upstream jar (the patched copy only ever existed transiently under this project's own `build/patchedLibs/`, never published anywhere). So on a real instance, the same missing-mixins bug is right back.

## The fix (part 1: get the patched jar into the shipped mod jar)

`build.gradle`: added a `patchMoreHitboxesManifest` task (parallel to the existing `patchMoreHitboxesToml`) that inserts `Automatic-Module-Name: morehitboxes` into the extracted jar's `META-INF/MANIFEST.MF` **before any trailing blank line** (a manifest's main-attributes section ends at its first blank line; appending after it starts an invalid second "per-entry" section and throws `invalid manifest format`). Then added `jarJar files(patchedMoreHitboxesJar)` alongside the existing `implementation files(patchedMoreHitboxesJar)`.

NeoForge's `net.neoforged.moddev` plugin auto-applies a `JarJarPlugin` per source set, which creates a `jarJar` Configuration already wired into the `jar` task's `from(...)`. Its `JarJar` task has an explicit fallback path for plain file-collection dependencies (not just resolvable Maven coordinates): it requires an `Automatic-Module-Name` (or `module-info.class`) to key the embedded jar, then embeds it under `META-INF/jarjar/<file>.jar` plus a generated `META-INF/jarjar/metadata.json`, using an MD5 hash of the file as its synthetic version. Verified via `unzip -l build/libs/fossil-*.jar` and unzipping the embedded jar itself - both the `[[mixins]]` TOML fix and the `Automatic-Module-Name` manifest fix are present inside it. This part works correctly and was NOT the remaining bug.

## The fix (part 2: the actually-live-broken part) - CONFIRMED 2026-08-15

Even with the jarJar embed correctly in place (freshly rebuilt jar verified to contain the patched nested jar), the user's real instance still failed - and even after they thought they'd fixed it, removing what they believed was the issue didn't immediately resolve it, until the standalone `morehitboxes` jar was fully removed from `mods/`.

**Root cause, confirmed by reading NeoForge's actual `JarJarSelector` library source** (`net.neoforged:JarJarSelector:0.4.1`, class `JarSelector.detectAndSelect()`):
```java
//Strip out jars which are already included by source. We can't do any resolution on this anyway
// so we force the use of those by not returning them.
```
Any embedded/jarJar-selected jar whose modid matches a jar **already present as a directly-installed "source" mod** gets discarded entirely, deferring unconditionally to the source (mods/-folder) jar. This is deliberate NeoForge behavior (lets a user's real, separately-installed copy override an embedded dependency bundled by another mod) - not a bug, and not something fixable from this project's side. It means our patched jarJar copy can **never** win against an external unpatched copy of the same mod sitting in `mods/`.

**Practical implication:** `neoforge.mods.toml` still declares `morehitboxes` as a separate `required` dependency (same as GeckoLib/TerraBlender) - which is exactly what leads installers/users toward downloading a standalone copy from Modrinth/CurseForge, silently reintroducing this exact bug. **Anyone installing this mod must be told explicitly: do NOT install more-hitboxes separately, it's bundled via jarJar already; if one is already present in `mods/`, delete it.** Confirmed live: with only `fossil.jar` + GeckoLib + TerraBlender in `mods/` (no standalone morehitboxes jar), combat/hitbox behavior worked correctly.

**Reusable pattern:** any time a Gradle-only patch/workaround for a vendored dependency is consumed via a plain `implementation files(...)`/`runtimeOnly files(...)` dependency, ask whether that fix needs to reach the *shipped* jar, not just the dev run configs. And even once jarJar embedding is correctly wired, remember NeoForge's jar-in-jar dedup always prefers a directly-installed mod over an embedded one with the same modid - if the embedded copy carries a required patch (not just a convenience bundle), a real, separately-installed copy of that same mod defeats the patch. Worth checking the actual `JarJarSelector` library source directly when NeoForge's dedup/selection behavior needs verifying - it's a small, readable class.
