# Task: Modernize jbmstable-parser for beatoraja dependency migration

## Completion status

Completed in this workspace.

- Current vendored jar behavior is covered by smoke tests.
- Generated `bms.table` public API matches `F:\Java\Beatoraja\lib\jbmstable-parser.jar`.
- Jackson is externalized as a normal Gradle dependency.
- Tests pass with Jackson `2.9.6` and `2.20.1`.
- Gradle wrapper/build, README, migration notes, license status notes, release/checksum tasks, and verification notes are present.

See:

- `README.md`
- `docs/migration.md`
- `docs/verification.md`
- `docs/LICENSE_STATUS.md`

## Goal

Prepare `exch-bms2/jbmstable-parser` so beatoraja can eventually replace the vendored `lib/jbmstable-parser.jar` with a reproducible, source-built dependency.

Current beatoraja vendored jar facts:

- File: `lib/jbmstable-parser.jar`
- Main package: `bms.table/**`
- Contains 12 project classes
- `DifficultyTableParser.class` is Java 8 bytecode
- `DifficultyTableParser.class` timestamp in vendored jar: 2018-09-08
- Vendored jar embeds:
  - `com.fasterxml.jackson.core:jackson-annotations:2.9.6`
  - `com.fasterxml.jackson.core:jackson-core:2.9.6`
  - `com.fasterxml.jackson.core:jackson-databind:2.9.6`
- Repository HEAD message appears consistent with vendored jar: `update jackson (2.5.0 -> 2.9.6)`.

## Required Work

1. Reproduce the current vendored jar behavior

- Keep Jackson at `2.9.6` initially.
- Build a jar from source.
- Compare the generated public API against beatoraja's current `lib/jbmstable-parser.jar`.
- Add smoke tests for at least:
  - parsing a minimal difficulty table header
  - parsing table elements
  - parsing course data if supported by existing code
  - verifying fields beatoraja uses, such as table name, level, md5/sha256/url/title
- Do not modernize behavior yet unless required to make the build reproducible.

2. Externalize Jackson

- Stop shading/embedding Jackson into the parser jar.
- Use normal build dependencies instead.
- Confirm tests pass with Jackson `2.9.6` externalized.
- Then test with current beatoraja Jackson target, currently `2.20.1`.
- Document any source/API changes required by Jackson upgrade.

3. Modernize project structure

- Add Gradle build.
- Use Java 25 toolchain or at least verify Java 25 compatibility.
- Preserve Java 8 bytecode only if there is a clear compatibility reason; otherwise document chosen target.
- Add `LICENSE` or clearly document license status if upstream license cannot be confirmed.
- Add README build instructions.
- Add release/tag instructions and artifact checksum generation.

## Acceptance Criteria

- `./gradlew test` passes.
- A non-shaded parser jar can be produced.
- Tests pass with Jackson `2.20.1`.
- README documents:
  - how to build
  - Java target
  - Jackson dependency policy
  - compatibility notes for beatoraja
- A migration note explains whether beatoraja can replace `lib/jbmstable-parser.jar` directly or needs code/build changes.
