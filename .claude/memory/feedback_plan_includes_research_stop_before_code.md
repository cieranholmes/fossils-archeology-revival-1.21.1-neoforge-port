---
name: feedback-plan-includes-research-stop-before-code
description: "When asked to 'plan' a task, finish all research (incl. API-shape investigation) before presenting it, but do not write/edit any code until the user approves the plan"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: acd0d06b-e862-4382-a22c-03dd81865e4b
  modified: 2026-08-14T07:38:24.661Z
---

When the user asks for a plan (or says "stop before implementing"), the planning phase includes finishing any research needed to make the plan concrete — e.g. looking up exact current API signatures via decompiled sources/javap before committing to an approach — not just a task list. Only actual file writes/edits count as "implementing" and should wait for explicit go-ahead.

**Why:** during [[fossils-migration-plan]] work, an in-flight research+implementation agent (porting the recipe framework for Phase 7) was killed mid-run after the user said "stop before implementing." The user then clarified that research should still be finished as part of the plan — only the code-writing part needed to wait.

**How to apply:** for any non-trivial task in this project (especially migration-phase work with real API-drift risk), split work into (1) research/investigation — safe to run to completion and report on, even via background agents, and (2) implementation — pause after presenting the research-backed plan and wait for explicit approval before writing/editing files. This complements [[feedback-stop-after-each-task]] (checkpoint after each task) — this memory is about the boundary *before* the first task's code starts, not just between tasks.
