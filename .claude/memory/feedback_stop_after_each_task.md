---
name: feedback-stop-after-each-task
description: "During multi-task work (like phased migration work), write a memory checkpoint after each task/subtask completes, then wait for the user's explicit go-ahead before starting the next one"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 6fd81c74-5ff9-40d1-bb1b-4c2acfc62afc
  modified: 2026-08-14T11:17:39.615Z
---

When working through a tracked task list (e.g. TaskList-based phase work on this project), stop after each task's status flips to completed: write a memory checkpoint summarizing what was just done, then wait for the user's next command rather than auto-continuing to the next task.

**Why:** User explicitly asked for this mid-session during Phase 6 (worldgen) work, after several tasks had already completed in a row without a pause. They want visibility/checkpointing between discrete units of work rather than a long autonomous run.

**How to apply:** In sessions with a live TaskList tracking multi-step phase/migration work, treat each task completion as a natural stopping point — update relevant project memory (e.g. the phase's progress-tracking memory file), then stop talking/acting and let the user decide whether to continue. Don't apply this to trivial single-step requests; it's specifically for long autonomous multi-task runs. If the user says "keep going" after a pause, that's authorization to continue — but revert to pausing after the *next* task completes, not to auto-running the rest of the list.

**Granularity correction (2026-08-14):** when a task is itself broken into numbered subtasks (e.g. Phase 7 task 9's 9.1-9.10), the stopping point is after the *whole numbered task* (all of 9.x), not after each individual subtask. User's exact words: "only stop for major tasks not subtasks i.e finish all 9.x before stopping" — given while task 9.1 of 10 had just finished and I'd stopped there. Still checkpoint memory after each subtask (cheap, preserves resumability), just don't stop talking/acting and waiting for go-ahead until the parent task's last subtask is done.
