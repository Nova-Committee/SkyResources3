# Client Manual Smoke Runbook

> Purpose: complete the final visual/click-through validation that headless Gradle gates and GameTests cannot prove.
> Current startup evidence: `run/logs/latest.log` from 2026-06-14 04:56.

## Preconditions

- Start the client with `./gradlew.bat runClient --no-daemon`.
- Use the default dev user/world or a throwaway local creative world.
- Keep JEI and Jade enabled for the primary run; only run a no-JEI profile if preparing a package that omits JEI.
- Treat this as a manual release-prep check. Automated GameTests already cover guide data integrity, menu registry ids, and server command behavior, but not visual clicks.

## Startup Evidence Already Refreshed

- `runClient` reached the client render thread.
- `SkyResources3 1.0.0 (skyresources3)`, Jade, JEI, Minecraft, and NeoForge were present in the mod list.
- ResourceManager reloaded `mod/skyresources3`, `mod/jade`, `mod/neoforge`, and `mod/jei`.
- `SkyResources3 common setup complete` was logged during resource loading.
- Jade loaded `committee.nova.mods.skyresources3.integration.jade.SkyResourcesJadePlugin`.
- OpenAL initialized and the sound engine started.
- Minecraft and JEI GUI texture atlases were created.
- No crash marker was found in the captured `latest.log`.

## Guide Smoke

1. Join a local world and press the configured `key.skyresources3.guide` key with no other screen open. The default is
   `Y` to avoid Minecraft 1.21.11's built-in `G` quick-actions key.
2. Confirm the `Sky Resources Guide` screen opens.
3. Type `fusion` into the search box.
4. Confirm search narrows results and selecting the Fusion Table page clears/jumps predictably.
5. Click at least one inline page link from Stage 1, such as an island route link.
6. Click at least one recipe action, for example Fusion Table or Life Infusion.
7. With JEI present, confirm the recipe action opens/filters JEI instead of showing an unavailable message.
8. Click at least one structure/setup action, such as Combustion Heater Setup or Life Infuser Setup.
9. Confirm the structure overlay/list is readable, scrollable if needed, and closes with Done.

## Representative Menu Smoke

Use creative inventory or `/give @s <id>` to place and open these representative blocks:

- `skyresources3:fusion_table`: validates the large custom Fusion Table screen and dump button presence.
- `skyresources3:wooden_machine_casing`: validates Machine Casing mode display; install a Heat Provider, Combustion Heater, or Condenser item if available.
- `skyresources3:mini_freezer`: validates the Freezer screen family.
- `skyresources3:aqueous_concentrator`: validates shared Aqueous machine layout.
- `skyresources3:rock_crusher`: validates FE/progress machine layout.
- `skyresources3:life_infuser`: validates Life Infuser slots and status text.
- `skyresources3:combustion_controller`: validates five filter slots.
- `skyresources3:combustion_collector`: validates five output slots.

Pass if each screen opens without a client crash, slots line up visually with the background, player inventory is visible, titles/progress indicators do not overlap, and closing the screen returns to the world.

## Island and Team Smoke

Run these commands in a throwaway local world. Use a creative/op dev profile.

1. `/island create grass`
2. `/island home`
3. `/island spawn`
4. `/island info`
5. `/island trusted`
6. `/skyresources3 team create`
7. `/skyresources3 team info`
8. `/skyresources3 team home`

Pass if commands execute without client disconnects, success/failure text is localized/readable, teleports land on a safe platform/home position, and team commands do not conflict with vanilla `/team`.

## JEI-Absent Fallback

Only run this if preparing a distribution profile without JEI:

1. Start a no-JEI client profile.
2. Open the guide with `G`.
3. Click the same recipe actions used in the JEI-present run.
4. Pass if the guide shows the localized fallback/unavailable message and the client does not crash.

## Recording Result

When a human completes the click-through, update:

- `plans/client-smoke-resource-license-closeout-checklist.md`
- `plans/final-validation-compatibility-closeout-checklist.md`
- `plans/final-migration-gap-audit.md`
- `plans/client-gui-smoke-execution-evidence.md`

Record the date, profile used, and any observed issue. Do not mark the manual item complete from startup logs alone.
