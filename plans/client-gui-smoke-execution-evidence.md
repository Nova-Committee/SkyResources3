# Client GUI Smoke Execution Evidence

> Date: 2026-06-14
> Task: `.trellis/tasks/06-14-client-gui-smoke-execution`

## Summary

The desktop smoke attempt proved that the dev client can reach the main menu and an existing local singleplayer world
with SkyResources3, Jade, and JEI loaded. It also exposed a real usability defect: the guide key default was `G`, which
conflicted with Minecraft 1.21.11 `key.quickActions`. The default guide key is now `Y`.

The first GUI attempt did not complete the full click-through because native desktop input automation became unreliable
after window focus changed between Codex, Windows Terminal, and the GLFW Minecraft window.

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

## Release Smoke Validation

A later focused validation run under
`.trellis/tasks/06-14-client-gui-release-smoke-validation/evidence/` completed the remaining primary GUI paths in a
local creative singleplayer world with SkyResources3, Jade, and JEI enabled.

Observed screenshots/log evidence:

- `release-runclient-1-guide-open-post-y.png`: `Y` opened the `Sky Resources Guide`.
- `release-runclient-1-guide-search-fusion.png`: guide search accepted `fusion` and visibly narrowed the result set.
- `release-runclient-1-guide-link-sand-island.png`: clicking a guide page action jumped to `Sand Island` and cleared the
  search field.
- `release-runclient-1-island-team-chat-history.png`: `/island create grass`, `/island home`, `/island spawn`,
  `/island info`, `/island trusted`, `/skyresources3 team create`, `/skyresources3 team info`, and
  `/skyresources3 team home` produced readable chat feedback and teleported without disconnecting.
- `release-runclient-1-fusion-table-target.png`: the local player targeted `skyresources3:fusion_table`.
- `release-runclient-1-fusion-table-dump-tooltip-precise.png`: the Fusion Table menu opened and exposed its
  screen-specific `Dump Stored Catalyst` button.
- The initial JEI guide recipe click only showed the guide tooltip and did not switch screens, revealing a silent
  integration-result problem.
- `release-runclient-2-jei-after-guide-recipe-fixed.png`: after the integration result check was tightened, clicking the
  Life Infusion recipe action opened JEI's Recipes GUI for the Alchemical Infusion Stone item recipe instead of staying
  silently on the guide.

The JEI-present action is therefore visually safe and opens JEI, but the observed fallback view was the item recipe view,
not direct proof that the targeted `process/infusion` category deep-link displayed a populated category. Treat targeted
process-category deep-linking as an enhancement unless a stricter release requirement is added.

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
in Git. Release validation evidence lives under
`.trellis/tasks/06-14-client-gui-release-smoke-validation/evidence/` and is also intentionally not tracked in Git.

## Result

- Client launch: passed.
- Existing world entry: passed in the `interactive-runclient-2` attempt.
- Guide default key conflict: found and fixed by changing the default from `G` to `Y`.
- Hidden-launcher focused retry: partial; client loaded but visible captures stayed white.
- Guide screen open/search/action click-through: passed in the release validation run.
- JEI recipe action click-through: passed with JEI opening to the item recipe fallback view after the integration result
  fix.
- Representative machine screen click-through: passed for Fusion Table, including the screen-specific dump button.
- Island/team command manual sanity: passed for the local singleplayer command sequence listed above.

## Follow-Up

Only run the no-JEI fallback profile if preparing a distribution profile without JEI. A stricter JEI process-category
deep-link test can be added later if release criteria require the guide action to open the exact process category rather
than a visible JEI recipe fallback.
