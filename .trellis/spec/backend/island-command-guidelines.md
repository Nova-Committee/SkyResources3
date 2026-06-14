# Island Command Guidelines

> Executable contracts for the built-in VoidIslandControl island command surface.

---

## Scenario: Island Relationship Commands

### 1. Scope / Trigger

- Trigger: adding or changing island commands, island saved data, island protection, or island spawn guidance.
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
The `/skyresources3 team ...` signatures above are compatibility aliases only; they must not create or persist an
independent team model.

### 3. Contracts

- Island ownership, membership, invitations, and trusted visitors are stored only in `IslandSavedData.IslandRecord`.
- Do not introduce or write a separate `TeamSavedData` model for island relationships.
- Island saved data stores a starter template `type`; old island records without `type`, `members`, or `invites` default
  to `grass` and empty maps.
- Supported island starter template ids are `grass`, `sand`, `snow`, `wood`, `gog`, and `magma`.
- The `magma` starter template preserves the legacy VIC shape, including Crystal Fluid at the old `pos.west().south()`
  relative position next to the Magmafied Stone progression setup.
- Creating an island creates only an island record. The creator becomes the island owner and the first island player.
- Accepting an island invitation adds the accepting player to that island's member map and teleports them to island home.
- A player may own at most one island or be a member of one island. A member must leave before creating their own island.
- Members who leave an island are removed from that island but remain eligible for future invitations. Leaving must not
  create a permanent deny list.
- When a trusted visitor accepts an island invitation, their trusted-visitor entry must be removed because membership
  becomes the source of access. Leaving that island must also remove any stale trusted-visitor access.
- `/skyresources3 team create` is retained only as a rejected compatibility command. Players must use `/island create`
  or `/island invite` instead.
- `/skyresources3 team invite`, `accept`, `leave`, `disband`, `home`, `info`, `trust`, `untrust`, and `trusted` are
  compatibility aliases for the island commands and must read/write only `IslandSavedData`.
- Island disband by the island owner through `/island disband` or `/skyresources3 team disband` deletes the island
  record and teleports the owner and online members back to the initial spawn platform. It does not clear placed blocks
  from the world.
- Player identity lookup is stored in `PlayerIdentitySavedData` as a local `UUID -> last known name` cache for players
  this mod has already observed. Do not use it as an external profile service or full rename-history index.
- An island member without a personal island resolves `/island home` and `/island info` through their joined island.
- `/island visit <player>` resolves the target player's accessible island first. Online targets resolve by UUID;
  offline targets may resolve by saved island owner/member name in `IslandSavedData`, then by `PlayerIdentitySavedData`
  if a cached UUID exists.
- `/island reset` is only a confirmation prompt; `/island reset confirm` performs the reset with the stored type.
- `/island reset <type>` prompts for a type switch; `/island reset <type> confirm` clears the island reset area, rebuilds
  the selected starter template, and persists the new type.
- Island reset is restricted to island owners. Reset clearing uses the configured island protection radius and caps the
  clear radius by island spacing so one reset cannot wipe a neighboring island.
- New islands are created in `skyresources3:void_island` when the data-pack dimension is available, with overworld
  fallback only for missing-dimension recovery.
- `/island spawn` teleports to a generated spawn platform in `skyresources3:void_island` when available, with the old
  overworld origin-heightmap behavior as fallback.
- `/island spawn` generates that shared spawn platform from `voidIslandSpawnPlatformRadius` and
  `voidIslandSpawnPlatformBlock`; defaults must remain radius `2` and `minecraft:bedrock`.
- When the `skyresources3:void_island` world preset creates an empty flat overworld, server startup must generate the
  shared bedrock spawn platform, set the world spawn to it, and no-island players logging in there must be placed on
  that platform. This initial overworld spawn platform must use `minecraft:bedrock` even if an older local config file
  still contains a different `voidIslandSpawnPlatformBlock` value.
- `/island` without subcommands must show localized guidance for creating a new island or joining another player's
  island invitation.
- Island protection uses the configured horizontal radius around each island center, derived from
  `IslandRecord.home().below()`.
- Island owners, island members, and trusted visitors may break, place, and right-click blocks inside that protected
  island range.
- Untrusted visitors and unrelated players must not be allowed to break, place, or right-click blocks inside another
  island's protected range.
- Island protection handlers must run before custom world-mutating interaction handlers such as processing tools or
  cauldron cleaning.
- Trusted visitors are stored on `IslandSavedData.IslandRecord` so solo island owners can grant access without creating
  another relationship model.
- Trust creation resolves online targets first, then cached local identities from `PlayerIdentitySavedData`; unresolved
  offline names still fail. Untrust may remove a stored visitor by name.
- Island invitations resolve online targets first, then cached local identities from `PlayerIdentitySavedData`;
  unresolved offline names still fail. Cached offline invitations are persisted by UUID and can be accepted when the
  player later joins with the same UUID.
- Player-facing command messages use `Component.translatable` with keys in `assets/skyresources3/lang/en_us.json`.
- Translation arguments must be Minecraft-supported message argument types: `Component`, `Number`, `Boolean`, or
  `String`. Convert identifiers, resource keys, block positions, and other domain objects to strings or components
  before passing them to `Component.translatable`.

### 4. Validation & Error Matrix

- Void island config disabled -> reject every island command and compatibility alias.
- Player already owns an island -> reject island invitation acceptance.
- Player is already an island member -> reject personal island creation.
- Player runs `/skyresources3 team create` -> reject; island relationships are created by `/island create` and
  `/island invite`.
- Inviter is an island member but not the island owner -> reject invite.
- Invite target is offline but has no cached local identity -> reject invite.
- Invite target already owns an island -> reject invite.
- Invite target is already an island member -> reject invite.
- Invite target already has a pending island invitation -> reject invite to keep accept semantics unambiguous.
- Invite target is offline but has a cached local identity -> persist a pending invitation against the cached UUID.
- Island owner tries `/island leave` or `/skyresources3 team leave` -> reject and require disband.
- Island member leaves through `/island leave` -> remove the member and keep that player eligible for a later invitation.
- Island member leaves through `/skyresources3 team leave` -> same behavior as `/island leave`.
- Island member leaves after previously being trusted -> remove the trusted access too.
- Player leaves through `/island leave`, gets invited again by the same owner, and accepts -> rejoin succeeds.
- Player leaves through `/skyresources3 team leave`, gets invited again by the same owner, and accepts -> rejoin
  succeeds.
- Island owner disbands -> delete the island record and clear all membership by removing that record.
- Visit target is offline but matches a saved island owner name -> teleport to that island.
- Visit target is offline but matches a saved island member name -> teleport to that member's island.
- Visit target is offline and has no saved island/member name match -> reject visit.
- Visit target has no accessible island -> reject visit.
- Visitor previously left the target island -> follow normal visit rules; do not reject solely because of leave history.
- Island member runs `/island reset confirm` without owning the island -> reject reset.
- Island owner runs `/island reset confirm` with placed blocks inside the protected reset area -> clear those blocks
  before rebuilding the starter template.
- Island owner runs `/island reset confirm` with placed blocks outside the protected reset area -> leave those blocks
  untouched.
- Non-member modifies another island's protected range -> cancel the event and show localized denial feedback.
- Owner or island member modifies the protected range -> allow the event to continue.
- Trusted visitor modifies the protected range -> allow the event to continue.
- Non-owner island member manages trusted visitors -> reject.
- Island owner trusts an online non-member visitor -> persist the visitor UUID and last known name.
- Island owner trusts an offline cached non-member visitor -> persist the cached visitor UUID and last known name.
- Island owner trusts an offline name with no cached local identity -> reject.
- Island owner untrusts a stored visitor name -> remove the visitor from the island record.
- Position is outside every known protected range -> leave the event untouched.

### 5. Good/Base/Bad Cases

- Good: owner creates island, invites an online player, player accepts, player uses `/island home`.
- Base: solo player creates island and uses `/island home`; no separate team data is required.
- Bad: storing island membership in `TeamSavedData` while protection checks `IslandSavedData`, because access rules can
  drift.
- Bad: registering top-level `/team` merges with or shadows vanilla scoreboard commands.

### 6. Tests Required

- `./gradlew.bat compileJava` proves command classes and codecs compile.
- `./gradlew.bat runData` proves resources still load during data generation.
- `./gradlew.bat build` proves packaged resources and classes assemble.
- `./gradlew.bat runGameTestServer` proves dedicated-server startup accepts command registration and saved data codecs.

Command GameTests should execute commands through the Brigadier dispatcher and assert saved-data side effects, not just
positive command return values.
Command GameTests for island relationships should cover create/invite/accept/home/leave/rejoin/disband and the
compatibility `/skyresources3 team ...` aliases.
Command GameTests for cached offline identities should assert that invite/trust writes the cached UUID, and that a later
player with the same UUID can accept the stored invitation.
Command GameTests for `/island spawn` should assert command execution and teleport behavior. If the GameTest server
exposes `skyresources3:void_island`, assert generated platform block state through the command path; otherwise assert
fallback command behavior and cover platform config through `VoidIslandWorld.ensureSpawnPlatform` directly.

### 7. Wrong vs Correct

#### Wrong

```java
event.getDispatcher().register(Commands.literal("team").then(teamNode()));
```

#### Correct

```java
event.getDispatcher().register(Commands.literal(Skyresources3.MODID).then(teamNode()));
event.getDispatcher().register(islandNode());
```

#### Wrong

```java
source.sendSuccess(() -> Component.translatable("message.skyresources3.island.info", island.dimension().identifier()), false);
```

#### Correct

```java
source.sendSuccess(() -> Component.translatable("message.skyresources3.island.info", island.dimension().identifier().toString()), false);
```
