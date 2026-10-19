# Speed of Sound — handoff checklist

**21 / 25 complete.** Reviewed October 19, 2026.

Generated from `checklist.json` by `render_checklist.py`; edit the JSON and regenerate both views.

Target: `Hieraphant/SpeedofSound:feat/moonshine-v2-asr`. Validated application baseline: `b6a87a069d887b580a465ffb8e347b40ddb5e8b4`.

This handoff is committed above that baseline without changing application code. The current branch tip is obtained from Git, not inferred from this baseline field.

## Explicit outstanding dependency

> Your repo still vendors sherpa-onnx v1.13.2; the latest stable release page currently identifies v1.13.8. The Gradle correction is a separate build-toolchain change, not a native recognizer update.

This dated statement does NOT mark the native dependency as updated. Recheck the release and verify PR #3976 inclusion before changing the complete library set. The dependency gate below remains open.

## Prepare the branch

- [x] **01. Fresh clone, remotes and LFS** (`setup`)

  **Verified.** The working checkout is Speedofsound, targeting Hieraphant/SpeedofSound:feat/moonshine-v2-asr. The separate sos-upstream-review checkout targets the true fork Hieraphant/speedofsoundmain. On October 19, the working repo and experiment were clean, their committed trees matched, and git lfs fsck passed. Do not confuse the feature branch with main.

- [x] **02. Preserve the original branch tip** (`backup`)

  **Preserved.** Keep backup/moonshine-before-upstream at de3f32e in the upstream-review Git repository; keep backup/moonshine-before-model-reload at de3f32e and backup/moonshine-before-flatpak-align at f4dec80 in the original working repository. Local backups and evidence are not disposable merely because the delivered code is pushed.

- [x] **03. Correct authorship and remove the session trailer** (`author`)

  **Complete in upstream fork.** Authorship-only rewrite: ad8f145 → 33ee0fb; de3f32e → fd18dd5. Both rewritten commits map to Hieraphant; the session trailer was removed and existing co-author credit retained. The original standalone history was deliberately NOT rewritten. Never rebase it merely to repeat this completed step.

- [x] **04. Verify that the rewrite did not change the code** (`trees`)

  **Verified.** The original and rewritten Moonshine commit trees were identical. The corrected GUI-tested integration f22ced9 and standalone f4dec80 also share a tree. Experiment f981bb8 and delivered b6a87a0 share tree 307dc66d1b68b11daee8168db901cfd3c9b31378. Subsequent handoff-only commits must not change application code, tests, libraries, or packaging inputs.

## Test on the desktop

- [x] **05. Build and run automated checks** (`build`)

  **Passed.** At the delivered application baseline b6a87a0: make build and make check passed; 190 uncached standard tests passed (186 core, 4 app; CLI has no tests), with no failures/errors/skips. The 21 reload lifecycle/concurrency tests and 6 catalog tests are included in 190, not extra. Separate native acceptance is recorded in HANDOFF.md.

- [x] **06. Launch the actual development application** (`gui-start`)

  **Desktop verified.** The user successfully ran the development app. An earlier run merely activated an installed Flatpak sharing the same application ID. Fully quit all installed/development copies before a targeted GUI test; do not uninstall or replace the installed application.

- [x] **07. Verify short dictation with the loaded Base model** (`dictation-log`)

  **Desktop verified.** Earlier startup explicitly loaded English Base and completed short dictation; the corrected integration GUI test later reloaded Base twice and produced useful text. Source: user-provided October 18–19 logs and user confirmation, not an accuracy benchmark.

- [x] **08. Verify short dictation with the loaded Tiny model** (`tiny-runtime`)

  **Desktop verified.** Tiny was explicitly created at startup and after a fresh GUI download. The corrected integration then reloaded Tiny during same-session switches and produced useful text. The result is short English functional recognition, not long-audio validation.

- [x] **09. Confirm that dictated text is usable in the target application** (`text-output`)

  **User confirmed.** The user confirmed useful voice texting and supplied repeated Testing phrases after model switches. Logs independently show completed pipelines and non-empty text-output requests. Do not claim a formal word-error-rate measurement or tests of every portal/output setting.

- [x] **10. Disclose the inherited live model-switching bug** (`model-switch`)

  **Disclosed and repaired downstream.** Originally Base/Tiny selection changed the label without rebuilding the recognizer. The defect and later independent correction were disclosed in the existing comment on upstream PR #125. Actual repair and downstream delivery are separately recorded in model-switch-resolution; do not file a duplicate report.

- [x] **11. Identify the reused model files and shared storage** (`shared-models`)

  **Verified.** Both normal development checkouts share ~/.local/share/speedofsound/models and the properties file under ~/.local/share/speedofsound. Reusing old models does not prove downloading. Isolate XDG_DATA_HOME and XDG_CACHE_HOME for fresh-install tests; never delete the normal model collection to simulate first launch.

- [x] **12. Verify fresh Moonshine Tiny and Base downloads** (`gui`)

  **Fresh download passed.** In an initially empty isolated profile, the user downloaded English Tiny and Base through Model Library. Both archives passed SHA-256 verification and extraction; a newly created Tiny recognizer then dictated successfully. Whisper Tiny English was also downloaded. Raw desktop logs contain session/restore values and must not be uploaded publicly.

- [x] **13. Smoke-test existing Whisper dictation** (`whisper`)

  **Desktop verified.** The app disabled Moonshine, created sherpa-onnx-whisper-tiny/en, and completed five short dictations with output counts 72, 13, 17, 17, 39. This is the completed existing-model regression. Runtime corrections may require new targeted tests, not a repeat of the whole original acceptance sequence.

## Prepare and publish the draft

- [x] **14. Track the Flatpak failure separately** (`flatpak`)

  **Reported; downstream fix passed CI.** Upstream issue #194 was filed. A subsequent CI run on f4dec80 reproduced SDK Gradle 9.7.1 requesting Kotlin DSL 6.7.3 while old offline sources supplied 6.5.2. Delivered b6a87a0 aligns the wrapper/checksum and all three generated manifests. Full Flatpak, Build, JPackage and Snap CI passed at b6a87a0. The report is still open upstream and has not yet been updated with that downstream success.

- [x] **15. Check for overlapping upstream work** (`upstream-overlap`)

  **Reviewed.** Existing upstream draft PR #125 is the maintainer’s Moonshine v2 attempt and is associated with feature request #35. It predates the current shared infrastructure and has merge conflicts. The saved older Claude report is historical; the live Claude session could not be read during handoff, so its current work/blockers are unknown.

- [x] **16. Recheck the dependency and prepare an honest draft body** (`release`)

  **Review complete; dependency fix NOT delivered.** Your repo still vendors sherpa-onnx v1.13.2; the latest stable release page currently identifies v1.13.8. The Gradle correction is a separate build-toolchain change, not a native recognizer update. Reverified October 19, 2026. PR k2-fsa/sherpa-onnx#3976 merged September 22; release v1.13.8 was published September 10 and does not contain the fix. Reviewing the blocker is complete; implementing and validating its resolution remains unchecked under merge.

- [x] **17. Push only the feature branch to the real fork** (`push`)

  **Published.** The true upstream fork has feat/moonshine-v2-asr at fd18dd5 and the independent fix/sherpa-model-reload at dd3ac71. The original Hieraphant/SpeedofSound feature branch additionally contains f4dec80 (reload integration) and b6a87a0 (Flatpak alignment). These are deliberately separate histories; do not force-synchronize them.

- [x] **18. Coordinate with the maintainer on existing PR #125** (`coordinate`)

  **Published and updated.** Existing PR #125 comment 6258263909 links the fork feature and reload branches and records completed tests. It was last updated October 19 at 05:07:32 UTC in the checked snapshot. It predates the later original-repo integrations/green Flatpak CI. Preserve this distinction and use the existing thread rather than opening a competing PR cold.

- [ ] **19. Agree the upstream route before opening another draft** (`draft`)

  **Awaiting maintainer decision.** No maintainer response accepting selected commits versus a linked replacement was found on October 19. Incorporation of the offered reload and packaging corrections is not an upstream merge. Read PR #125 and its comments/reviews again before acting; do not open a duplicate or change upstream branches on an assumption.

- [ ] **20. Check the new upstream PR workflows** (`ci`)

  **Upstream validation still open.** All four substantive workflows passed on the original repository’s PR #1 at b6a87a0. This does not complete the eventual upstream contribution’s own CI/LFS checkout. Review the actual selected upstream PR SHA and its workflows when a route exists; the docs-only Build (skip) job alone is not a substantive build.

## Before marking ready for review

- [ ] **21. Resolve the older filtering/sorting requirement** (`library-gate`)

  **Scope unresolved.** The older pre-merge checklist requested language/provider filtering and selectable name/size sorting. The current library only orders by size. Shared registry/base-class reuse and documentation already address the other requests. No filtering controls were implemented or waived. Ask the maintainer about scope; do not silently add unrelated models or interface features.

- [x] **22. Fix model reloading and deliver the tested correction to your repo** (`model-switch-resolution`)

  **Implemented, tested and pushed.** Independent correction dd3ac71 → integrated f4dec80. It tracks loaded model/language/debug, reloads before inference, serializes decode/cleanup and handles failed replacements without stale engines. 21 standard lifecycle/concurrency tests passed; real native before/after and production-plugin probes passed after reproducing the old bug. The user’s GUI run at 00:52–00:54 on October 19 loaded Tiny → Base → Tiny → Base without restart and completed all four dictations. Delivered in the working repo; upstream acceptance belongs to draft.

- [ ] **23. Update the complete fixed dependency and retest longer audio** (`merge`)

  **OPEN — native dependency and longer audio.** Your repo still vendors sherpa-onnx v1.13.2; the latest stable release page currently identifies v1.13.8. The Gradle correction is a separate build-toolchain change, not a native recognizer update. This statement is explicitly part of the checklist. Recheck the release at the time of work; verify inclusion of merge commit 3a82dc5caee92380c73c976aec0213e2a775315d (PR #3976), not merely a larger version number. Update Java, Linux x64 and Linux aarch64 JARs together, the three expected checksums in core/libs/Makefile, and the three core/build.gradle.kts paths; update dependent docs/build metadata as needed. Test short clips and clips around/beyond the prior ~9.2-second boundary, plus existing-model/reload regressions. Record aarch64 coverage honestly. No temporary cap, VAD/chunking, or unreviewed master/nightly binary is silently substituted for this acceptance criterion.

## Personal confirmations

- [x] **24. Confirm the author email is linked to your GitHub account** (`email`)

  **Verified by GitHub mapping.** The documented author email on de3f32e maps to Hieraphant in GitHub’s commit API; rewritten feature commits and new fixes also mapped to that account. Retain the repository-local approved author identity. Do not invent another identity or rewrite already-published history.

- [x] **25. Confirm the previously exposed token was revoked** (`token`)

  **User confirmed revoked.** The user explicitly confirmed that the exposed token was revoked. This item is complete; do not ask again, retrieve old token notes, or publish credentials. The receiving agent must verify its present authorized GitHub connection independently; the user’s desktop authorization is not proof that the sandbox can push.

## Counting and delivery boundaries

Model-reload engineering and delivery are complete. Upstream incorporation belongs to the separate contribution-route gate. All four substantive downstream CI workflows passed on the application baseline; they are not the eventual upstream PR's checks. Do not add or remove checkmarks merely to represent preparation of this handoff.

Browser checkbox edits are local and are not automatically synchronized to this JSON or GitHub. HANDOFF.md carries the workflow, source/evidence map, preserved limitations and cleanup rules.
