# Island Command Guidelines

> Executable contracts for the built-in VoidIslandControl and team command surface.

---

## Scenario: Island Team Commands

### 1. Scope / Trigger

- Trigger: adding or changing island/team commands, island saved data, or team saved data.
- Scope: common/server-side Java code under `committee.nova.mods.skyresources3.island`.

### 2. Signatures

- `/island create`
- `/island create <type>`
- `/island`
- `/island home`
- `/island spawn`
- `/island visit <player>`
- `/island trust <player>`
- `/island untrust <player>`
- `/island trusted`
- `/island reset`
- `/island reset <type>`
- `/island reset confirm`
- `/island reset <type> confirm`
- `/island info`
- `/island invite <player>`
- `/island accept`
- `/island leave`
- `/island disband`
- `/skyresources3 island ...`
- `/skyresources3 team create`
- `/skyresources3 team invite <player>`
- `/skyresources3 team trust <player>`
- `/skyresources3 team untrust <player>`
- `/skyresources3 team trusted`
- `/skyresources3 team accept`
- `/skyresources3 team leave`
- `/skyresources3 team disband`
- `/skyresources3 team home`
- `/skyresources3 team info`

Do not register a top-level `/team` command because vanilla Minecraft already owns that namespace for scoreboard teams.

### 3. Contracts

- Island ownership is stored in `IslandSavedData`, keyed by owner UUID.
- Island saved data stores a starter template `type`; old records without `type` default to `grass`.
- Supported island starter template ids are `grass`, `sand`, `snow`, `wood`, `gog`, and `magma`.
- The `magma` starter template preserves the legacy VIC shape, including Crystal Fluid at the old `pos.west().south()`
  relative position next to the Magmafied Stone progression setup.
- Team ownership is stored in `TeamSavedData`, keyed by team owner UUID.
- Teams are island-scoped only. Creating an island automatically creates that island's team; accepting an island
  invitation automatically joins the team and teleports the player to the island home.
- `/skyresources3 team create` is retained only as a rejected compatibility command. Players must not be able to
  create an independent team before creating or joining an island.
- `/skyresources3 team trust`, `/skyresources3 team untrust`, and `/skyresources3 team trusted` are compatibility
  aliases for the island trusted-visitor commands and must obey the same owner-only rules.
- Members who leave an island are removed from that team but remain eligible for future invitations. Leaving an island
  or using `/skyresources3 team leave` must not create a permanent deny list.
- Old saved `departed_members` data may still be decoded for save compatibility, but it must not block invitation,
  acceptance, or `/island visit` behavior.
- When a trusted visitor accepts an island invitation, their trusted-visitor entry must be removed because team
  membership becomes the source of access. Leaving that island must also remove any stale trusted-visitor access.
- Team disband by the island owner through `/island disband` or `/skyresources3 team disband` removes the team,
  removes online members from that team, and abandons the island saved record. It does not clear placed blocks from the
  world.
- Player identity lookup is stored in `PlayerIdentitySavedData` as a local `UUID -> last known name` cache for players
  this mod has already observed. Do not use it as an external profile service or full rename-history index.
- A team member without a personal island resolves `/island home` and `/island info` through the team owner's island.
- `/island visit <player>` resolves the target player's own island first, then the target's team owner island. Online
  targets resolve by UUID; offline targets may resolve by the last saved island owner or team member name stored in
  `IslandSavedData` / `TeamSavedData`, then by `PlayerIdentitySavedData` if a cached UUID exists.
- `/island reset` is only a confirmation prompt; `/island reset confirm` performs the reset with the stored type.
- `/island reset <type>` prompts for a type switch; `/island reset <type> confirm` clears the island reset area, rebuilds the selected starter template, and persists the new type.
- Island reset is restricted to personal island owners. Reset clearing uses the configured island protection radius and caps the clear radius by island spacing so one reset cannot wipe a neighboring island.
- New islands are created in `skyresources3:void_island` when the data-pack dimension is available, with overworld fallback only for missing-dimension recovery.
- `/island spawn` teleports to a generated spawn platform in `skyresources3:void_island` when available, with the old overworld origin-heightmap behavior as fallback.
- `/island spawn` generates that shared spawn platform from `voidIslandSpawnPlatformRadius` and
  `voidIslandSpawnPlatformBlock`; defaults must remain radius `2` and `minecraft:bedrock`.
- When the `skyresources3:void_island` world preset creates an empty flat overworld, server startup must generate the
  shared bedrock spawn platform, set the world spawn to it, and no-island/no-team players logging in there must be
  placed on that platform. This initial overworld spawn platform must use `minecraft:bedrock` even if an older local
  config file still contains a different `voidIslandSpawnPlatformBlock` value.
- `/island` without subcommands must show localized guidance for creating a new island or joining another player's
  island invitation.
- Island protection uses the configured horizontal radius around each island center, derived from `IslandRecord.home().below()`.
- Trusted visitors are stored on `IslandSavedData.IslandRecord` so solo island owners can grant access without creating a team.
- Trust creation resolves online targets first, then cached local identities from `PlayerIdentitySavedData`; unresolved
  offline names still fail. Untrust may remove a stored visitor by name.
- Team invitations resolve online targets first, then cached local identities from `PlayerIdentitySavedData`; unresolved
  offline names still fail. Cached offline invitations are persisted by UUID and can be accepted when the player later
  joins with the same UUID.
- Island owners and members of the owner's `TeamSavedData` team may break, place, and right-click blocks inside that protected island range.
- Trusted visitors may break, place, and right-click blocks inside the protected island range.
- Untrusted visitors and unrelated players must not be allowed to break, place, or right-click blocks inside another team's protected island range.
- Island protection handlers must run before custom world-mutating interaction handlers such as processing tools or cauldron cleaning.
- Player-facing command messages use `Component.translatable` with keys in `assets/skyresources3/lang/en_us.json`.
- Translation arguments must be Minecraft-supported message argument types: `Component`, `Number`, `Boolean`, or `String`.
  Convert identifiers, resource keys, block positions, and other domain objects to strings or components before passing
  them to `Component.translatable`.

### 4. Validation & Error Matrix

- Void island config disabled -> reject every island/team command.
- Player already owns an island -> reject team invitation acceptance.
- Player is already a team member -> reject personal island creation.
- Player is any team owner or member without a personal island -> reject personal island creation.
- Player runs `/skyresources3 team create` -> reject; teams are created by `/island create`.
- Inviter is a non-owner team member -> reject invite.
- Invite target is offline -> reject invite.
- Team owner has no island -> reject team creation/invite/home.
- Team owner tries `/island leave` or `/skyresources3 team leave` -> reject and require disband.
- Team member leaves an island -> remove the member and keep that player eligible for a later invitation.
- Team member leaves an island after previously being trusted -> remove the trusted access too.
- Player leaves through `/island leave`, gets invited again by the same owner, and accepts -> rejoin succeeds.
- Player leaves through `/skyresources3 team leave`, gets invited again by the same owner, and accepts -> rejoin succeeds.
- Island owner disbands team -> remove the owned team, online member team links, and island record.
- Invite target already has a pending team invitation -> reject invite to keep accept semantics unambiguous.
- Invite target is offline but has a cached local identity -> persist a pending invitation against the cached UUID.
- Invite target is offline and has no cached local identity -> reject invite.
- Visit target is offline but matches a saved island owner name -> teleport to that island.
- Visit target is offline but matches a saved team owner/member name -> teleport to that team's owner island.
- Visit target is offline and has no saved island/team name match -> reject visit.
- Visit target has no own or team island -> reject visit.
- Visitor previously left the target island -> follow normal visit rules; do not reject solely because of leave history.
- Team member runs `/island reset confirm` without owning a personal island -> reject reset.
- Island owner runs `/island reset confirm` with placed blocks inside the protected reset area -> clear those blocks before rebuilding the starter template.
- Island owner runs `/island reset confirm` with placed blocks outside the protected reset area -> leave those blocks untouched.
- Non-member modifies another island's protected range -> cancel the event and show localized denial feedback.
- Owner or team member modifies the protected range -> allow the event to continue.
- Trusted visitor modifies the protected range -> allow the event to continue.
- Non-owner team member manages trusted visitors -> reject.
- Island owner trusts an online non-member visitor -> persist the visitor UUID and last known name.
- Island owner trusts an offline cached non-member visitor -> persist the cached visitor UUID and last known name.
- Island owner trusts an offline name with no cached local identity -> reject.
- Island owner untrusts a stored visitor name -> remove the visitor from the island record.
- Position is outside every known protected range -> leave the event untouched.

### 5. Good/Base/Bad Cases

- Good: owner creates island, creates team, invites an online player, player accepts, player uses `/island home`.
- Base: solo player creates island and uses `/island home`; no team data is required.
- Bad: registering top-level `/team` merges with or shadows vanilla scoreboard commands.

### 6. Tests Required

- `./gradlew.bat compileJava` proves command classes and codecs compile.
- `./gradlew.bat runData` proves resources still load during data generation.
- `./gradlew.bat build` proves packaged resources and classes assemble.
- `./gradlew.bat runGameTestServer` proves dedicated-server startup accepts command registration and saved data codecs.

When command execution GameTests are added, cover create/invite/accept/home/reset/visit and rejection paths for existing islands, existing teams, members resetting team islands, and offline visit targets.
Command GameTests should execute commands through the Brigadier dispatcher and assert saved-data side effects, not just positive command return values.
Command GameTests for cached offline identities should assert that invite/trust writes the cached UUID, and that a later
player with the same UUID can accept the stored invitation.
Command GameTests for `/island spawn` should assert command execution and teleport behavior. If the
GameTest server exposes `skyresources3:void_island`, assert generated platform block state through the
command path; otherwise assert fallback command behavior and cover platform config through
`VoidIslandWorld.ensureSpawnPlatform` directly.

### 7. Wrong vs Correct

#### Wrong

```java
event.getDispatcher().register(Commands.literal("team").then(teamNode()));
```

#### Correct

```java
event.getDispatcher().register(Commands.literal(Skyresources3.MODID).then(teamNode()));
event.getDispatcher().register(Commands.literal("island").then(inviteAlias()));
```

#### Wrong

```java
source.sendSuccess(() -> Component.translatable("message.skyresources3.island.info", island.dimension().identifier()), false);
```

#### Correct

```java
source.sendSuccess(() -> Component.translatable("message.skyresources3.island.info", island.dimension().identifier().toString()), false);
```
