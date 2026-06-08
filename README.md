# jbmstable-parser

Java parser for BMS difficulty table JSON metadata and entries.

## Build

Use the Gradle wrapper:

```sh
./gradlew test
./gradlew jar
```

The default build uses Jackson `2.9.6` to match the beatoraja vendored jar migration baseline.

To test with another Jackson version:

```sh
./gradlew test -PjacksonVersion=2.20.1
```

## Java target

The build uses a Java 25 toolchain and compiles with `--release 8`. This preserves Java 8 bytecode compatibility with beatoraja's current vendored `lib/jbmstable-parser.jar` while still verifying the source with a modern JDK.

## Jackson dependency policy

Jackson is a normal Gradle dependency and is not shaded into this jar. Consumers must include Jackson on their runtime classpath. The default dependency is:

```text
com.fasterxml.jackson.core:jackson-databind:2.9.6
```

`jackson-databind` brings `jackson-core` and `jackson-annotations` transitively in Gradle/Maven builds.

## Compatibility notes for beatoraja

See [docs/migration.md](docs/migration.md) and [docs/verification.md](docs/verification.md). In short, beatoraja needs build/runtime classpath changes before replacing the currently vendored all-in-one `lib/jbmstable-parser.jar`, because this project now produces a non-shaded parser jar.

## Public API report

```sh
./gradlew publicApiReport
```

This writes `build/reports/public-api.txt`. Compare it with beatoraja's vendored jar:

```sh
./gradlew comparePublicApi -PbaselineJar=/path/to/beatoraja/lib/jbmstable-parser.jar
```

## Release checklist

1. Run `./gradlew clean test -PjacksonVersion=2.9.6`.
2. Run `./gradlew clean test -PjacksonVersion=2.20.1`.
3. Run `./gradlew comparePublicApi -PbaselineJar=/path/to/beatoraja/lib/jbmstable-parser.jar`.
4. Run `./gradlew jar sourcesJar artifactChecksums`.
5. Review `docs/LICENSE_STATUS.md` before publishing outside an internal migration.
6. Tag the release from a clean worktree.
