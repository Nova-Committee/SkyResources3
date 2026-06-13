# Stage 65 - Guide Structure Preview Renderer

> Scope: restore the useful part of old `ModGuidePages.imageDesigns` by rendering current guide structures as an inline, client-only layout preview.

## Checklist

- [x] Create Trellis task and PRD.
- [x] Read guide specs and old `GuideImage` / `ModGuidePages.imageDesigns` behavior.
- [x] Keep common `GuideStructure` data client-free.
- [x] Add a compact structure preview area to `GuideScreen`.
- [x] Render `GuideStructure.BlockEntry` coordinates as a 2D isometric item layout.
- [x] Center and fit the preview inside the structure detail panel.
- [x] Show block name and `(x, y, z)` coordinate tooltip on preview hover.
- [x] Keep the scrollable structure list and close behavior intact.
- [x] Update migration plan, guide spec, and deferred checklist notes.
- [x] Run compileJava, runData, build, runGameTestServer.
- [ ] Chinese commit and archive Trellis task.
