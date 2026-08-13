# Phase 5 debug GUI screens — COMPLETE (2026-08-13)

Ported source's `common/.../client/gui/debug/` top-level (12 files) into
`src/main/java/com/github/fossils/client/gui/debug/`, plus the keybind wiring and the network/renderer
follow-through it unblocked. This finishes the work started in an earlier paused session (see git history for
that checkpoint's step-by-step notes, steps A-K, all now done) plus one additional gap found and closed
below (step L) that wasn't in the original 12-file list.

## All 12 target files, done

`DebugSlider.java`, `DebugTab.java`, `DebugSelectionList.java`, `AbstractAnimationList.java`,
`SkeletonEditTab.java`, `EggTab.java`, `EmbryoTab.java`, `InfoTab.java`, `DebugScreen.java`, `PathingScreen.java`
(+ field-ownership move from `PathingRenderer`/`PathingDebug`, confirmed already done), `AnimationTab.java`,
`InstructionTab.java` (real 226-line port, replacing the earlier stub).

## What this session actually did (picking up from the checkpoint)

The checkpoint memory (steps A-K) turned out to already be mostly done by whatever session last touched the
repo - `DebugScreen`/`PathingScreen`/`AnimationTab`/real `InstructionTab`/both new network packets
(`InstructionMessage`/`C2SStructureMessage`) all existed on disk already, matching source line-counts closely.
**5 things were still genuinely incomplete**, found by re-verifying every checklist item against real file
content rather than trusting the checkpoint doc:

1. **`S2CMarkMessage.handle()`** was still the deliberate no-op stub - replaced with a real call to
   `DebugScreen.showPath(context.player(), payload.targetPositions(), payload.blockStates(), payload.below())`,
   now that `DebugScreen` exists.
2. **`SyncDebugInfoMessage.handle()`** was a disjoint "return immediately on client / do everything on server"
   split - restructured to match source's real single-shared-path shape: on the client, return early unless
   `Minecraft.getInstance().screen instanceof DebugScreen` and that screen's `entity` field matches this
   payload's `entityId`; then run gender/mating-cooldown/playing-cooldown/climbing-cooldown/
   refreshTexturePath/refreshDimensions/updateAbilities on **both** sides, but age/hunger/mood only when
   `!clientSide`.
3. **`InstructionRenderer`** was missing the ruler-tool and position-mode crosshair-guide tail section (and the
   `rulerStartPos`/`rulerEndPos` fields) - both restored verbatim from source, now that `DebugScreen.rulerMode`
   and `PathingDebug.getAirHitResult`/`getBlockHitResult`/`getHitResult` (all pre-existing) are usable.
4. **Keybind wiring on `ClientModEvents`** didn't exist at all, despite `PathingRenderer.renderOverlay` already
   referencing `ClientModEvents.pathingScreenKey`/`debugRepathKey`/`debugAdvanceKey`/`debugReverseKey` - **the
   project did not compile before this fix**. Added: 6 `public static KeyMapping` fields, a
   `@SubscribeEvent registerKeyMappings(RegisterKeyMappingsEvent)` (gated on `Version.debugEnabled()`), and a
   `@SubscribeEvent onClientTick(ClientTickEvent.Post)` mirroring source's `ClientInit.later()` key-polling
   block (`debugHelpKey`→toggle `PathingDebug.showHelpMenu`, `pathingScreenKey`→open `PathingScreen`,
   `debugRepathKey`/`debugAdvanceKey`/`debugReverseKey`→`PathingDebug.rePath()`/`PathingRenderer.advanceIndex()`/
   `reverseIndex()`, `PathingDebug.tick()`, `debugScreenKey`→open `DebugScreen` via `DebugScreen.getHitResult`).
   7 lang entries added to `en_us.json` (`category.fossils.debug` + 6 `key.fossils.*`).
5. **Step L (new, not in the original 12-file/A-K list): `PathingDebug.pathNavigation1`/`pathNavigation3`/
   `pathNavigation4`/`pathNavigation5` were never initialized anywhere** - `PathingScreen.init()` builds
   `List.of(pathNavigation1, pathNavigation3, pathNavigation4, pathNavigation5)`, which NPEs if any are still
   null (`List.of` rejects nulls), so the screen would have crashed the instant a player opened it. Source
   initialized these in `ClientInit.later()`'s `ClientPlayerEvent.CLIENT_PLAYER_JOIN` registration - target has
   no equivalent `ClientInit` class, so this was added as a new `@SubscribeEvent
   onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn)` on `ClientModEvents`, gated on
   `Version.debugEnabled()`, constructing `PlayerPathNavigation`/`DebugCenteredPathNavigation`/
   `SweepPathNavigation`/`WaterPathNavigation` against `event.getPlayer()`/`Minecraft.getInstance().level`
   exactly like source did. **Look for this kind of gap on any future debug-tooling work** - the 12-file
   checklist covered the GUI classes themselves but not all the client-lifecycle plumbing those classes assume
   already exists.

## Deliberately still NOT done (real, documented gaps - not oversights)

- **The actual per-frame render-hook wiring for `PathingRenderer.render(...)`/`InstructionRenderer.render(...)`**
  (world-space box/line/arrow rendering) and **`DebugMouseHandlerMixin`/`DebugMinecraftMixin`** (which set
  `rulerStartPos`/`rulerEndPos`/`DebugScreen.rulerMode` from mouse clicks) are genuinely unported. Source wired
  these via a **mixin** (`DebugLevelRendererMixin`) into `LevelRenderer` - confirmed by grepping source, no
  Architectury-event equivalent exists for a `MultiBufferSource`-based world-render hook. This project
  deliberately has **no mixin framework** (see [[fossils-combat-and-scaling-bugs-fixed]] and
  [[fossils-morehitboxes-mixin-registration-fix]] - the only mixins in the whole project are the vendored
  more-hitboxes jar's own, patched in via a build.gradle task, not authored here). Adding a first-party mixin
  framework just for dev-only debug-tool rendering would be significant scope creep beyond "port the GUI
  screens" - flagged here for whoever wants full debug-tool parity later. Note this does **not** block anything
  gameplay-relevant: `PathingRenderer.renderOverlay` (the on-screen key-hint text, HUD-only, no mixin needed)
  still isn't wired to a HUD-render event either, same reasoning - low priority since it's just help text.
- Everything already flagged out-of-scope in the original checkpoint remains out of scope: mammal capability
  editing tabs' server call sites beyond what SyncDebugInfoMessage's restructure now supports, etc.

## Verified this session

- `./gradlew compileJava` - clean, first try, zero errors (only pre-existing unchecked/deprecated-API notes).
- `./gradlew runServer` - booted to `Done (1.568s)! For help, type "help"` with no exceptions, then stopped
  cleanly. (Debug packets/classes are shared client+server code paths - even though the GUI itself is
  client-only, `DebugPackets`/`ModPackets` registration and `SyncDebugInfoMessage`/`S2CMarkMessage` handlers
  load on the server too, so this is a real compile+load check for that shared code, not just the client half.)
- `./gradlew runClient` - booted fully into a singleplayer world (mixins loaded, LWJGL/OpenAL initialized,
  world generated, player joined), then closed itself after ~20s with no crash. Log confirms via NeoForge's own
  `AutomaticEventSubscriber` debug output that `ClientModEvents`'s new methods split correctly across buses
  ("Found mix of game bus and mod bus listeners... registering them separately"): `registerKeyMappings`→mod
  bus, `onClientTick`/`onClientPlayerLoggingIn`→game bus - confirms the project's established "no explicit
  `bus=` needed, NeoForge auto-detects per event type" pattern extends to these new events too. No missing-
  translation warnings for the new `key.fossils.debug*`/`category.fossils.debug` lang entries. Zero exceptions
  referencing any of `ClientModEvents`/`DebugScreen`/`PathingScreen`/`PathingDebug`/`PathingRenderer`/
  `InstructionRenderer`/`InstructionTab`/`AnimationTab`/`SkeletonEditTab`/`EggTab`/`EmbryoTab`/`InfoTab`/
  `DebugTab`/`DebugSlider`/`DebugSelectionList`/`AbstractAnimationList`/`S2CMarkMessage`/
  `SyncDebugInfoMessage`/`InstructionMessage`/`C2SStructureMessage`. The ~5000 "Exception" log hits and several
  `ERROR`-level model-load failures present in this run are all **pre-existing, unrelated** gaps already
  flagged elsewhere (GeckoLib `animation.postosuchus.run` parse failure, and `fossils:plant_loader` not found
  for ~23 plant block models - see [[fossils-phase1-blocks-progress]]'s "23 models need a Phase 5 plant_loader"
  note, which is a different, still-open Phase 5 item from this GUI work and not addressed by this session).

## Follow-up for [[fossils-phase3-capabilities-networking-progress]]

That memory's "4 debug packets deferred" note is now **stale** - all 11 debug packets
(`C2SDisableAIMessage`/`C2SDiscardMessage`/`C2SForceAnimationMessage`/`C2SRotationMessage`/`C2SSlowMessage`/
`C2SStructureMessage`/`C2STameMessage`/`InstructionMessage`/`S2CCancelAnimationMessage`/`S2CMarkMessage`/
`SyncDebugInfoMessage`) are ported, registered, and now have real (non-no-op) handlers. 0 deferred.
