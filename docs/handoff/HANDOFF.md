# Speed of Sound — consolidated project handoff

**Prepared October 19, 2026. Application baseline: `b6a87a0`. Checklist: 21/25.**

This document replaces the scattered planning, review, test and delivery write-ups as the continuation brief. It does not replace source code or the checklist. Read `CHECKLIST.md` first, use `checklist.json` as the editable checklist record, and run `python3 docs/handoff/render_checklist.py` after an evidence-backed status change. `checklist.html` is a portable interactive view, not an automatic GitHub monitor.

## Continuing ownership: Claude Code and the user only

The receiving Claude Code agent takes over implementation, sandbox experiments, automated/native testing, delivery commits, authorized normal pushes, CI inspection and the versioned checklist. The user performs desktop/microphone tests and sends results directly to Claude. GitHub carries the code and durable project state between them.

This temporary ChatGPT conversation has no continuing role, approval responsibility or required tool access. Do not ask the user to reopen it, reconnect its Browser/Host tools, or return here for testing, promotion or publication. The user's desktop directories below describe existing local state, not paths or tools presumed available inside Claude's sandbox. Claude supplies exact candidate identifiers and local instructions to the user when a desktop action is needed.

## 1. The authoritative repository and what is already delivered

**Working target: `Hieraphant/SpeedofSound`, branch `feat/moonshine-v2-asr`.** Do not clone or reset to `main` and conclude that the feature or fixes are missing. `main` has deliberately not been changed. Original draft PR #1 targets this repository's own main; it is not an upstream PR.

The complete validated application baseline is:

- Commit: `b6a87a069d887b580a465ffb8e347b40ddb5e8b4`.
- File tree: `307dc66d1b68b11daee8168db901cfd3c9b31378`.
- The handoff-only descendant containing these files adds documentation and preserved opt-in test harnesses. It does not change the validated application or package-building inputs. Determine its exact HEAD from Git and the delivery receipt rather than treating the baseline hash as the latest documentation commit forever.

| Working-repository commit | Content | Status |
|---|---|---|
| `ad8f145` | Moonshine v2 provider, ten-model catalog, app/CLI/settings wiring and six catalog tests | Delivered |
| `de3f32e` | Moonshine documentation and actual sherpa-onnx version/limitation | Delivered |
| `f4dec80` | Shared recognizer model/language/debug reload correction and 21 lifecycle/concurrency tests | Delivered; desktop-tested source |
| `b6a87a0` | Gradle 9.7.1 wrapper/checksum, three regenerated Flatpak offline-source manifests and alignment documentation | Delivered; all four substantive CI workflows passed |

The integration experiment `f981bb8a2226275ac19e436e625b6f71ce7f7b01` and the working repository baseline share the exact file tree above. All five files of the independent reload correction are present byte-for-byte in the working repo. The handoff audit found no untracked or ignored source files in the application/source-tooling paths that needed delivery. All three vendored JARs are tracked and their SHA-256 values match `core/libs/Makefile`; LFS verification passed.

### The true fork is a different delivery surface

`Hieraphant/speedofsoundmain` is the true fork of `zugaldia/speedofsound`. Its deliberately independent review branches are:

| Fork branch | Published tip | Meaning |
|---|---|---|
| `feat/moonshine-v2-asr` | `fd18dd5c875301469e344964a69f5f9d41049838` | Authorship-cleaned original Moonshine feature/docs, before later repairs |
| `fix/sherpa-model-reload` | `dd3ac712a6a64708218dfb379b0387c81f924500` | Reload correction alone, directly based on upstream `c233281` |
| `main` | `c233281cdcc4407b5f648de41fceef90dca849cc` | Preserved upstream base |

Do not force-push one history over the other. `33ee0fb`/`fd18dd5` are authorship-only counterparts of `ad8f145`/`de3f32e`, with identical original file trees. The standalone repo intentionally retained its original history. New fixes were attributed to Hieraphant; the upstream fork's feature rewrite removed its session trailer and preserved co-author credit. This completed authorship work must not be repeated on published standalone history.

## 2. Explicit unresolved native-library limitation

> Your repo still vendors sherpa-onnx v1.13.2; the latest stable release page currently identifies v1.13.8. The Gradle correction is a separate build-toolchain change, not a native recognizer update.

**This statement is also written directly into the checklist, including its open dependency item.** Reverified on October 19, 2026: GitHub's latest stable release was v1.13.8 (published September 10). The relevant fix, [k2-fsa/sherpa-onnx#3976](https://github.com/k2-fsa/sherpa-onnx/pull/3976), merged September 22 as `3a82dc5caee92380c73c976aec0213e2a775315d`. Comparing that fix to v1.13.8 reports the release 16 commits behind, none ahead. Recheck the actual release and inclusion when continuing; do not assume the next version number or a currently cached page is sufficient.

The historical agent report used the repository's 16 kHz mono `jfk.wav` fixture: 9.0-second Base/Tiny inputs transcribed, while 9.5-second inputs returned empty. Base's 10/11/22.8/34.2-second probes and Tiny's 11-second probe were also reported empty. These are **historical agent-provided boundary results**, not tests rerun in the documentation handoff. The native fix concerns decoder attention-mask length; a successful return code or “Transcription completed” message was not proof of non-empty output.

To close the dependency gate: select a verified release containing the fix; replace **all three** JARs (Java, Linux x64 and Linux aarch64) together; update the three expected hashes and version in `core/libs/Makefile`, the three paths in `core/build.gradle.kts`, and any affected docs/build metadata. Fetch into a temporary directory and verify before replacing the known-good set: the existing `download-libs` Make target removes existing matching JARs first. Run standard, native and desktop checks and explicit short/boundary/long-audio probes. Record which architectures actually ran; including an aarch64 JAR does not prove an aarch64 runtime test.

No VAD/chunking, nine-second cap, streaming rewrite or locally built master/nightly library has been substituted for this gate. PR #186 is an optional, separate recording-timeout proposal; the version inspected caps Whisper at 30 seconds, not Moonshine at nine seconds. Do not add that cap or ask for it as though the user or maintainer already requested it.

## 3. Tests and evidence: preserve what actually passed

| Evidence | Observed result | Scope |
|---|---|---|
| Standard build/check | `make build` and `make check` passed; 190 uncached tests: 186 core, 4 app, no failures/errors/skips | Delivered application baseline; CLI has no tests |
| Reload tests | 21 lifecycle/concurrency tests | Included in the 190, not additional |
| Catalog tests | Six Moonshine invariants | Included in the 190; not all-model runtime coverage |
| Original native probe | Two tests failed on original code, exposing stale loaded models | Historical before-fix result |
| Corrected native probe | Both passed: Moonshine Base/Tiny/Base and Whisper Tiny/Tiny-English/Tiny | Real native libraries, short fixture audio |
| Actual production plugins | Two further acceptance tests passed for registry-based same/cross-provider changes and invalid-selection recovery | Rechecked after the Gradle correction |
| Corrected desktop run | Tiny → Base → Tiny → Base without restarting; recognizer creation at 00:52:35.563, 00:53:02.224, 00:53:47.339, 00:54:21.391; output counts 24/23/24/23 | User's October 19 log and confirmation of usable text |
| Fresh download test | English Tiny and Base downloaded via Model Library in an initially empty profile; SHA-256 and extraction passed; newly installed Tiny dictated | User's isolated GUI run; not a manual file-copy test |
| Existing Whisper | Explicit Whisper recognizer creation followed by five successful short dictations; output counts 72/13/17/17/39 | User desktop regression |
| Remote/LFS | Independent remote checkout downloaded LFS objects without sharing the local object store; `git lfs fsck` passed | Original repository; current code's LFS files are unchanged |
| Offline Gradle package build | `installShadowDist` passed offline with an initially empty Gradle cache; 936 artifacts passed SHA-512 validation | Followed by a full successful Flatpak CI build |

No formal recognition-accuracy benchmark, complete non-English runtime matrix, aarch64 runtime validation, all portal settings matrix, or packaged desktop launch was performed. The installed Flatpak was not replaced. Do not claim those tests happened. These missing optional coverage areas are not automatically new items in the established 25-checkpoint acceptance list.

### Current substantive CI, pinned to `b6a87a0`

All were re-read during the October 19 handoff audit and reported `completed / success`:

| Workflow | Run |
|---|---|
| Build | [39209383639](https://github.com/Hieraphant/SpeedofSound/actions/runs/39209383639) |
| JPackage Build | [39209383636](https://github.com/Hieraphant/SpeedofSound/actions/runs/39209383636) |
| Snap Build | [39209383664](https://github.com/Hieraphant/SpeedofSound/actions/runs/39209383664) |
| Flatpak Build | [39209383742](https://github.com/Hieraphant/SpeedofSound/actions/runs/39209383742) |

These runs belong to the original repo's PR #1. They are neither merely historical pre-fix results nor the eventual upstream PR's checks. A separate `Build (skip)` success is not counted as a substantive build. A documentation-only descendant can legitimately have different workflow triggers; record exact SHAs rather than claiming every check reran on it.

### Preserved opt-in native harnesses

`native-tests/SherpaModelReloadNativeTest.kt` and `native-tests/SherpaProductionPluginNativeTest.kt` are the exact earlier local acceptance harnesses, now versioned so they are not stranded in a desktop evidence folder. Their SHA-256 hashes are in `verification.json`. They are outside the normal Gradle source tree on purpose: normal CI must not acquire undeclared large model fixtures.

Use only in a clean **experiment** checkout. Stage a copy of the chosen harness in `core/src/test/kotlin/com/zugaldia/speedofsound/core/plugins/asr/` only after verifying that the destination does not already exist. Set isolated `XDG_DATA_HOME` and `XDG_CACHE_HOME`, unset `SNAP_USER_COMMON`, and provide four models under `$XDG_DATA_HOME/speedofsound/models`: Moonshine Base English, Moonshine Tiny English, bundled Whisper Tiny and Whisper Tiny English. Use verified copies or catalog downloads in the sandbox, never the user's live model directory as a deletion target. The tests cut the tracked fixture to five seconds and do not access a microphone.

Run the selected class explicitly, for example:

```bash
./gradlew --no-configuration-cache --no-build-cache :core:test \
  --tests 'com.zugaldia.speedofsound.core.plugins.asr.SherpaProductionPluginNativeTest' \
  --rerun-tasks
```

Save the XML/log evidence outside source control after removing sensitive values. Remove only the identical temporary harness copy you created; keep the canonical copy under `docs/handoff/native-tests`. Confirm the experiment's status before committing. These probes use configuration observation/reflection and may need deliberate adaptation if internals change; do not weaken the model-identity assertions to make them green.

## 4. Architecture and repair boundaries

Moonshine support already exists: three provider/options/catalog files, ten catalog entries, app registration and options mapping, settings/enum branches and the CLI's `--provider sherpa` route. The CLI's model-family branch is important because its fallback otherwise routes an unknown ID to Whisper. Reuse `SUPPORTED_LOCAL_ASR_MODELS` and `SherpaOfflineAsr`; do not duplicate the old upstream draft implementation.

`Language` is a data class with companion constants, not an enum. The Moonshine exports in this catalog are monolingual; the existing plugin does not pass a selectable language into the native Moonshine configuration. Components are encoder, merged decoder, tokens, in that order, because the shared factory obtains tokens from the last component. Model sizes describe copied component storage, not compressed downloads. Preserve the measured catalog checksums and existing license notes; non-English catalog presence is not proof of runtime or licensing equivalence to English.

The reload correction keeps loaded model/language/debug configuration, reuses an unchanged engine and defers a reload until `enable` or the next transcription. Settings changes, creation hooks, inference and cleanup share a lock; failed replacement clears handles rather than silently reusing a stale/freed engine. Do not release while decoding, introduce inverse plugin-registry/recognizer lock order, or regress the failure-recovery tests. “Already active … skipping” can still be normal at registry selection if the subsequent inference performs the correct reload.

The packaging repair matches the SDK's Gradle 9.7.1 / Kotlin DSL 6.7.3. Its generated sources contain the matching Kotlin/build-tool dependencies; the project version catalog's Kotlin plugin pin is **2.3.20**, not the stale 2.3.10 text formerly in `CLAUDE.md`. Do not confuse that pin with Gradle's embedded Kotlin/build-script dependencies. Wrapper distribution SHA-256: `acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a`. Preserve normal Windows wrapper CRLF; the earlier whitespace verification used CRLF-aware checking, not disabled whitespace rules.

## 5. Upstream coordination: what is and is not agreed

Existing work: [PR #125](https://github.com/zugaldia/speedofsound/pull/125), [feature request #35](https://github.com/zugaldia/speedofsound/issues/35), [our coordination comment](https://github.com/zugaldia/speedofsound/pull/125#issuecomment-6258263909), and [Flatpak report #194](https://github.com/zugaldia/speedofsound/issues/194).

The audit found PR #125 still open/draft with conflicts, no new maintainer reply selecting a contribution route, and no new comments on #194. The older maintainer checklist requested shared registry, shared lifecycle, documentation and model-library filtering/sorting. The first two abstractions already landed separately in main and are reused here; the docs were updated. Filtering by language/provider and user-selectable sorting remain unimplemented and unwaived; fixed size ordering is not all of that requirement.

The existing #125 comment links the separately published fork feature and reload fix. It was last updated at 2026-10-19T05:07:32Z in the checked snapshot, before the subsequent original-repo integration/Flatpak success. Issue #194 was filed before a candidate fix was validated and has no downstream-success follow-up yet. These public posts must be read afresh before editing; do not overwrite concurrent feedback. A concise downstream success update is a possible next coordination action, not an already-performed action in this handoff. Do not claim upstream main reproduced or accepted a fix merely because downstream CI passed.

The four open gates remain: agreed upstream route/incorporation; that contribution's own CI/LFS; filtering/sorting scope; fixed native dependency plus longer-audio tests. Repair engineering and downstream delivery are complete. No new competing upstream PR, merge to main, automatic release, or installation over the user's desktop app is authorized by the routine repair workflow.

## 6. Two-environment working method

### User machine: locations and preserved state

| Role | Directory / branch |
|---|---|
| Experiment | `/home/hieraphant/Downloads/speedofsound/sos-model-reload-integration`, `test/moonshine-model-reload` |
| Validated delivery | `/home/hieraphant/Downloads/speedofsound/Speedofsound`, `feat/moonshine-v2-asr`, origin `Hieraphant/SpeedofSound` |
| Upstream feature review | `/home/hieraphant/Downloads/speedofsound/sos-upstream-review` |
| Independent upstream reload correction | `/home/hieraphant/Downloads/speedofsound/sos-sherpa-model-reload` |

The experiment and upstream review worktrees share a Git common directory; the standalone delivery repo is separate. Their different hashes do not imply missing source. Preserve local backups and existing evidence. Sandbox paths must be newly discovered or chosen inside that sandbox; these desktop paths are not available there by assumption.

The recorded host environment was Kubuntu/Linux x86-64, Java 25.0.4.1, GTK4/libadwaita and GStreamer. The desktop app log reported Adwaita 1.9.1 and GStreamer 1.26.11. Some CLI development utilities were absent even though required runtime libraries were present; build success alone does not certify microphone/UI availability. Mirror the project toolchain and dependency versions in the sandbox, and explicitly record differences. Do not pretend a headless cloud sandbox reproduces KDE/Wayland, portal permissions or physical microphone testing.

### Receiving agent: mandatory first pass before code or cleanup

1. Read this handoff and checklist; inventory the current sandbox checkout, branch, diff, local-only commits, running tasks and downloaded files. The live Claude session was **not accessible** to the handing-off assistant. Its current state, blockers and unique work must be assessed, not assumed.
2. Preserve any unique uncommitted work or unpushed commits as a branch/patch with a small manifest before cleanup. Compare it to the verified delivery branch. The saved older Claude report ends at a repository-attachment/push-permission blocker, not simply unfinished model additions; that report may no longer reflect the live session.
3. Use the authorized GitHub connection and verify the account and push access to `Hieraphant/SpeedofSound`. Do not reuse revoked tokens, retrieve old credential documents, bypass a permission denial or infer sandbox access from the user's desktop CLI. Anonymous access can clone the public repo; it does not imply push permission.
4. Fetch the approved feature branch. Require `b6a87a0` to be an ancestor and review any unexpected newer changes before continuing. Read the versioned handoff from the fetched tip. Do not replace the newer repo with the old sandbox checkout or replay already-delivered patches.
5. Use one clean delivery checkout and a separate linked experiment worktree in the sandbox. A safe fresh layout is a delivery checkout of `origin/feat/moonshine-v2-asr` plus an experiment branch made from that exact tip with `git worktree add`. Never overwrite an existing directory or reset it to make this layout fit.
6. Verify Java 25, Git/LFS, tracked model blobs, GTK runtime requirements, the checked-in wrapper checksum/version and the vendored library hashes. Use the project wrapper, not an unrelated system Gradle. Run `make build && make check`; record environmental failures separately from code failures.

### Cleanup authorization is narrow, not a blanket wipe

The user wants obsolete downloads from the old **Claude sandbox** purged, not their desktop models or unique work destroyed. Only after the inventory/preservation/comparison above shows what is redundant, remove explicitly identified session-owned downloaded archives, expanded model downloads, temporary caches and disposable build outputs that are reproducible or no longer needed. Record the paths removed. Do not recursively wipe a home directory, repository, `.git`, credential location, current source, evidence that exists nowhere else, shared caches, or an unrelated session. Retain minimal verified fixtures needed for the next test, or redownload into an isolated test profile. If important unresolved work exists, keep it and explain the exception instead of purging it.

### After each focused repair

Experiment → run relevant automated/native checks → commit the tested change → provide the exact candidate and targeted desktop instructions to the user when runtime behavior changed → record their result → apply the accepted change to the delivery checkout → compare content → push normally to `Hieraphant/SpeedofSound:feat/moonshine-v2-asr` → verify the remote SHA and current CI → update the checklist/evidence.

The user has authorized routine normal pushes of validated repairs on that branch; another approval is not required for every completed repair. That is not authorization to force-push, rewrite history, change main, expand scope to unrelated models, install over the desktop application or create competing upstream PRs. If a sandbox candidate must reach the user's machine before acceptance, provide a patch/export or request a clearly named temporary remote test branch; never present an untested candidate as the validated feature tip.

Avoid simultaneous writers: fetch/check remote state before applying or pushing. On unexpected local changes, a newer remote tip or non-fast-forward rejection, inspect and preserve the work; do not force it away. After every push state exact SHA, changed files, tests, CI status and remaining gates. Do not leave a completed fix only in the experiment folder. Do not mark a pending CI job or unperformed desktop test as complete.

### Desktop testing instructions and profile isolation

The user, not the headless agent, operates microphone/GUI tests. First fully quit the installed Flatpak and any other development copy; both use `io.speedofsound.SpeedOfSound` and can forward activation to an already-running instance. Run `make run` from the directory containing the intended candidate. Confirm the actual `Recognizer created:` model, not just a changed dropdown name. With the reload fix, creation can happen at the first transcription after a switch, not immediately on selection.

Normal clones share `~/.local/share/speedofsound/` data/settings, so a fresh clone is not a fresh model installation. For a new download-path test use a new owned test directory, set `XDG_DATA_HOME` and `XDG_CACHE_HOME`, unset `SNAP_USER_COMMON`, and run `./gradlew --no-configuration-cache :app:run` with `SOS_DISABLE_GIO_STORE=true`, `SOS_DISABLE_GSTREAMER=false` and the documented native-access flag. Do not copy the user's properties file, portal restore values or API credentials into the sandbox. Do not repeat the completed Base/Tiny download, Whisper smoke test or same-session switch tests unless the new change affects them.

## 7. Consolidation and privacy record

Reviewed material: the full available project thread; the supplied original agent response; the saved `MOONSHINE-V2-INTEGRATION.md` and `moonshine agent.md`; upstream review/audit/coordination drafts; reload before/after/native/desktop evidence; original-repo delivery/packaging receipts; the latest 25-item checklist; and fresh Git/source/GitHub API checks. `verification.json` is the compact provenance ledger. The current source is authoritative for implementation, while historical reports retain their original evidence scope.

Superseded instructions include “add Moonshine”, “GUI download still untested”, “fix only local”, “Whisper test pending”, “token revocation unconfirmed”, “Flatpak still failing downstream”, “Kotlin 2.3.10”, and “open a new upstream PR immediately”. Do not execute them again. The old instruction to simply bump the dependency and run a download target was incomplete: all JARs, verified checksums, metadata and longer-audio regressions are required.

Historical documents are not copied wholesale into this public repo. Raw logs contain portal restore/session values and local settings details; credential/privilege notes are deliberately excluded. The exposed GitHub token was confirmed revoked by the user. Preserve backup/evidence folders on the user's machine; no local purge is part of this handoff. The older downloads may still exist as historical records, but these versioned documents are the continuation source of truth.

### Direct user-to-Claude transfer; no browser prerequisite

The user can paste the continuation prompt directly into the existing Claude Code session. The receiving agent fetches the target feature branch and reads the versioned files in `docs/handoff/`; uploading the ZIP is optional when repository access works. Neither reopening this ChatGPT chat nor using its Browser/Host tools is required.

The earlier browser review did not occur, so no claim has been made that the live Claude sandbox was inspected or cleaned. Claude must assess its own current tasks, unique work and repository access using the first-pass reconciliation above before purging obsolete session-owned downloads. A clean, verified setup replaces stale assumptions, not unique source, Git history or the user's desktop data.
