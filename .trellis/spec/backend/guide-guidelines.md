# Guide Guidelines

> Contracts for the in-game Sky Resources guide data and client rendering boundary.

---

## Guide Action Contract

### 1. Scope / Trigger

Use this contract when adding or extending interactive guide page entries such as page links, recipe hints, or structure previews. The guide data lives in common Java code, while rendering and click behavior stay client-side.

### 2. Signatures

- Page metadata:
  ```java
  public record GuidePage(
          String id,
          String categoryKey,
          String titleKey,
          String textKey,
          Supplier<ItemStack> iconSupplier,
          List<GuideAction> actions
  )
  ```
- Action metadata:
  ```java
  public record GuideAction(
          GuideAction.Type type,
          String target,
          String labelKey,
          Supplier<ItemStack> iconSupplier
  )
  ```
- Structure metadata:
  ```java
  public record GuideStructure(
          String id,
          String titleKey,
          List<GuideStructure.BlockEntry> blocks
  )
  ```

### 3. Contracts

- `GuideAction.Type.LINK` targets a `GuidePage.id`.
- `GuideAction.Type.IMAGE` targets a `GuideStructure.id`.
- `GuideAction.Type.RECIPE` uses its icon stack as the recipe target until a recipe viewer integration exists.
- `GuidePage.actions()` must be immutable to callers; use `List.copyOf` in record construction.
- Guide body text may contain `{action:n}` inline markers, where `n` is the 1-based index of `GuidePage.actions()`.
- `{action:n}` markers are a client rendering hint only; invalid or out-of-range markers should degrade to readable text instead of throwing.
- Structure detail rendering should use `GuideStructure.BlockEntry` coordinates for both the scrollable block list and a client-only layout preview; keep any 3D or 2D rendering code out of common guide data.
- Client screens may render and dispatch actions, but common guide data must not import client-only classes.
- The default guide key must avoid current vanilla key mappings. Minecraft `1.21.11` uses `G` for `key.quickActions`,
  so SkyResources3 uses `Y` for `key.skyresources3.guide` by default.
- User-visible action labels, tooltips, feedback messages, and structure titles must use translation keys except item display names coming from `ItemStack#getHoverName()`.
- Server-side guide integrity tests should validate the common data contract without loading client-only screen classes.

### 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Page has no actions | Render the existing page text normally |
| Link action targets a missing page | Show localized feedback instead of throwing |
| Image action targets a missing structure | Show localized feedback instead of throwing |
| Recipe viewer integration is absent | Show a localized "pending integration" message |
| Search text hides a linked page | Clear search before jumping to the linked page |
| Structure has more blocks than visible rows | Allow client-side scrolling in the structure preview |
| Inline marker references a missing action | Render the marker as readable text; do not fail the page |
| Page body contains inline markers | Strip markers from search matching so users search visible prose |
| Common guide data contains a broken link, missing structure, bad recipe target, or missing translation key | `GuideMenuGameTests.guideDataIntegrity` should fail during `./gradlew.bat runGameTestServer` |

### 5. Good/Base/Bad Cases

- Good: `GuidePages` declares a page action with `GuideAction.link("crucible", stack(() -> ModItems.CRUCIBLE.get()))`, and `GuideScreen` handles the click by selecting the target page.
- Good: Guide text uses `{action:1}` to place that first action inline near the relevant prose.
- Good: A server-side GameTest reads `assets/skyresources3/lang/en_us.json` from the classpath and checks guide links, image structures, recipe targets, and inline markers without importing `net.minecraft.client.*`.
- Base: A plain text-only page uses the five-argument `GuidePage` constructor and has `List.of()` actions.
- Bad: A common guide class imports `net.minecraft.client.*` to open a screen or render a tooltip.
- Bad: A headless GameTest claims to verify screen layout or click behavior; those remain client `runClient` checks.

### 6. Tests Required

- `./gradlew.bat compileJava` must pass for guide data/model changes.
- `./gradlew.bat build` must pass for resources and packaging.
- `./gradlew.bat runData` should pass when guide text or generated resources changed.
- `./gradlew.bat runGameTestServer` must pass after guide page/action/structure changes and should include assertions that:
  - every guide category, page title, page text, image-action label, and referenced structure title has an `en_us.json` entry;
  - every `LINK` action target resolves through `GuidePages.find`;
  - every non-empty `RECIPE` action target belongs to `GuideRecipeTargets`;
  - every `IMAGE` action target resolves through `GuideStructures.find`;
  - every `{action:n}` marker in translated page text references an existing 1-based action index.
- Manual `runClient` verification is recommended for visual layout changes that cannot be covered by current tests.

### 7. Wrong vs Correct

Wrong:
```java
// Common data must not know how to open a client screen.
public void onClick() {
    Minecraft.getInstance().setScreen(new GuideScreen());
}
```

Correct:
```java
// Common data describes intent; the client screen dispatches it.
GuideAction.image("infuser", "guide.skyresources3.structure.infuser", stack(() -> ModItems.LIFE_INFUSER.get()));
```
