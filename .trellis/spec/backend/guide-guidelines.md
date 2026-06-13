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
- Client screens may render and dispatch actions, but common guide data must not import client-only classes.
- User-visible action labels, tooltips, feedback messages, and structure titles must use translation keys except item display names coming from `ItemStack#getHoverName()`.

### 4. Validation & Error Matrix

| Condition | Expected behavior |
|---|---|
| Page has no actions | Render the existing page text normally |
| Link action targets a missing page | Show localized feedback instead of throwing |
| Image action targets a missing structure | Show localized feedback instead of throwing |
| Recipe viewer integration is absent | Show a localized "pending integration" message |
| Search text hides a linked page | Clear search before jumping to the linked page |
| Structure has more blocks than visible rows | Show the first visible rows; defer scrolling to a focused follow-up |

### 5. Good/Base/Bad Cases

- Good: `GuidePages` declares a page action with `GuideAction.link("crucible", stack(() -> ModItems.CRUCIBLE.get()))`, and `GuideScreen` handles the click by selecting the target page.
- Base: A plain text-only page uses the five-argument `GuidePage` constructor and has `List.of()` actions.
- Bad: A common guide class imports `net.minecraft.client.*` to open a screen or render a tooltip.

### 6. Tests Required

- `./gradlew.bat compileJava` must pass for guide data/model changes.
- `./gradlew.bat build` must pass for resources and packaging.
- `./gradlew.bat runData` should pass when guide text or generated resources changed.
- `./gradlew.bat runGameTestServer` should pass to catch common/server load regressions after adding common guide data.
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
