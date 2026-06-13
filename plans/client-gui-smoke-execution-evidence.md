# Client GUI Smoke Execution Evidence

> Date: 2026-06-14
> Task: `.trellis/tasks/06-14-client-gui-smoke-execution`

## Summary

The desktop smoke attempt proved that the dev client can reach the main menu and an existing local singleplayer world
with SkyResources3, Jade, and JEI loaded. It also exposed a real usability defect: the guide key default was `G`, which
conflicted with Minecraft 1.21.11 `key.quickActions`. The default guide key is now `Y`.

The full manual GUI click-through is still not complete. Native desktop input automation became unreliable after window
focus changed between Codex, Windows Terminal, and the GLFW Minecraft window, so guide search/actions, JEI recipe action
clicks, representative machine screens, and island/team chat command flows remain manual release-prep items.

## Focused Retry

A follow-up attempt under `.trellis/tasks/06-14-client-gui-focused-smoke-closeout/evidence/` launched
`./gradlew.bat runClient --no-daemon` through a hidden PowerShell wrapper so the launcher terminal would not steal focus.
The client process created a `Minecraft NeoForge* 1.21.11` window, loaded SkyResources3, Jade, and JEI, initialized OpenAL,
and created the Minecraft and JEI GUI atlases. After both the initial and long-wait captures, the visible GLFW window
remained a white client area, so the retry did not produce direct visual evidence for main-menu, guide, JEI, machine, or
island/team click-through.

This narrows the remaining blocker: terminal focus pollution is no longer the main explanation for the missing GUI
evidence. In this desktop session, the client window/render capture itself did not become a usable visual target, so the
manual runbook still needs a human-observed focused client session before release.

## Observed Evidence

- `interactive-runclient-2-main-menu.png`: client reached the Minecraft main menu.
- `interactive-runclient-2-select-world.png`: `Singleplayer` opened the local world list.
- `interactive-runclient-2-after-enter-selected-world.png`: existing local world opened successfully; the screenshot shows
  an in-world singleplayer view.
- `run/logs/latest.log` from the same attempt included SkyResources3, Jade, JEI, Minecraft, and NeoForge in the mod list
  and logged spawn preparation.
- `interactive-runclient-3.out.txt`: after the key fix, `runClient` recompiled `SkyResources3Client.java` and reached
  client resource loading again.

Evidence files live under `.trellis/tasks/06-14-client-gui-smoke-execution/evidence/` and are intentionally not tracked
in Git.

## Result

- Client launch: passed.
- Existing world entry: passed in the `interactive-runclient-2` attempt.
- Guide default key conflict: found and fixed by changing the default from `G` to `Y`.
- Hidden-launcher focused retry: partial; client loaded but visible captures stayed white.
- Guide screen open/search/action click-through: not completed.
- JEI recipe action click-through: not completed.
- Representative machine screen click-through: not completed.
- Island/team command manual sanity: not completed.

## Follow-Up

Run `plans/client-manual-smoke-runbook.md` manually from a focused desktop session and record screenshots for the remaining
guide, JEI, machine GUI, and island/team checks before a release tag.
