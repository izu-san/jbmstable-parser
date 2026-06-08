# Migration verification

Verification performed in this workspace against:

- Source project: `F:\Java\jbmstable-parser`
- beatoraja vendored jar: `F:\Java\Beatoraja\lib\jbmstable-parser.jar`
- Gradle: `9.2.0`
- JDK/toolchain: Java 25

## Commands

```sh
./gradlew test
./gradlew test -PjacksonVersion=2.20.1
./gradlew comparePublicApi -PbaselineJar=F:\Java\Beatoraja\lib\jbmstable-parser.jar
./gradlew jar sourcesJar artifactChecksums publicApiReport
```

## Results

- Tests pass with Jackson `2.9.6`.
- Tests pass with Jackson `2.20.1`.
- Generated `bms.table` public API matches beatoraja's vendored `lib/jbmstable-parser.jar`.
- Generated parser jar contains only project classes under `bms/table/**`.
- Generated parser jar does not contain `com/fasterxml/**` classes.
- `DifficultyTableParser.class` is Java 8 bytecode, major version `52`.
- Artifact checksum generation is available through `artifactChecksums`.

## Current artifact checksum

```text
4ae891a9bfa52304118960201079630823412a5f99a53084a5bd3c7f6020e8a6  jbmstable-parser-0.1.0-SNAPSHOT.jar
```

The checksum is for the local snapshot artifact generated at `build/libs/jbmstable-parser-0.1.0-SNAPSHOT.jar`.
