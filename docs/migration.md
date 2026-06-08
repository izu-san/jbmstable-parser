# beatoraja migration notes

This repository now builds `bms.table` classes from source with Jackson as a normal runtime dependency instead of bundling Jackson classes into the parser jar.

## Current compatibility status

- Source package remains `bms.table`.
- Default dependency set keeps Jackson at `2.9.6` to reproduce the vendored beatoraja jar behavior first.
- The produced parser jar is non-shaded. Consumers must provide Jackson on the runtime classpath.
- Java compilation currently uses a Java 25 toolchain with `--release 25`. The source-built artifact requires Java 25 at runtime.
- Generated `bms.table` public API has been compared against `F:\Java\Beatoraja\lib\jbmstable-parser.jar` and matches.
- Generated `DifficultyTableParser.class` is Java 25 bytecode, major version `69`.

## beatoraja impact

beatoraja cannot replace `lib/jbmstable-parser.jar` with only the new parser jar unless its build/runtime classpath also includes:

- `com.fasterxml.jackson.core:jackson-annotations`
- `com.fasterxml.jackson.core:jackson-core`
- `com.fasterxml.jackson.core:jackson-databind`

Gradle consumers can depend on this project artifact and let `jackson-databind` pull the required transitive Jackson artifacts.

For beatoraja, the practical migration is:

1. Replace the vendored shaded parser jar with the source-built parser artifact.
2. Ensure beatoraja is built and run on Java 25.
3. Add Jackson to beatoraja's build/runtime dependencies.
4. Prefer beatoraja's current Jackson target, `2.20.1`, after running the parser test suite with `-PjacksonVersion=2.20.1`.

## Jackson upgrade check

The test suite passes with the default Jackson `2.9.6` and should also be run against beatoraja's target Jackson version:

```sh
./gradlew test -PjacksonVersion=2.20.1
```

No source or public API changes are required for the parser when moving from Jackson `2.9.6` to `2.20.1`; the code uses `ObjectMapper.readValue` and `writeValueAsString` APIs that remain available, and the smoke tests pass with `2.20.1`.

## Public API comparison

Generate the current API report:

```sh
./gradlew publicApiReport
```

The report is written to `build/reports/public-api.txt`. To compare against beatoraja's vendored `lib/jbmstable-parser.jar`, pass the jar:

```sh
./gradlew comparePublicApi -PbaselineJar=/path/to/beatoraja/lib/jbmstable-parser.jar
```

The comparison only checks the `bms.table` public API. It intentionally ignores the embedded Jackson classes in the vendored jar because the migration goal is to externalize Jackson.

See `docs/verification.md` for the latest local verification commands and results.
