# Source layout

The repository builds ten jars (five Minecraft versions, Fabric and NeoForge)
from one shared source tree.

```
src/main/              code and data shared by every target (Mojang names)
src/loader/fabric/     Fabric entrypoints, biome modification, client hooks
src/loader/neoforge/   NeoForge entrypoint, creative tab
src/versions/<mc>/     code and data that differ per Minecraft version
src/test/              unit tests shared by every target
targets/<mc>-<loader>/ one Gradle project per jar
```

A target project adds `src/main`, `src/versions/<mc>` and the matching
`src/loader/<loader>` directories to its own source set. Each target also
carries `src/test` (unit tests), `src/gametest` (a test-only mod run on a
headless server) and its loader metadata (`fabric.mod.json` or
`mods.toml`/`neoforge.mods.toml`, plus `geodes.mixins.json`).

`settings.gradle` includes every directory under `targets/` that has a
`build.gradle`. `enabled-targets.txt` lists the targets that `buildAll` and
`verifyAll` cover.

## What differs between versions

| Area | Differences |
| ---- | ----------- |
| Block and item construction | From 1.21.4 `Properties` must carry the registry id before the block or item is constructed. |
| Tuned locator data | 1.20.4 stores the tuning id in item NBT; 1.20.6 and later use a data component. |
| Resource ids | 1.20.4 and 1.20.6 use the public `ResourceLocation` constructors; later versions use static factories (`Identifier` on 26.x). |
| Silk touch check | 1.20.4 and 1.20.6 read enchantments directly; 1.21.1 and later resolve them through a registry holder. |
| Entity types | Up to 1.21.1 the builder takes the id path; from 1.21.2 it takes the registry key. |
| Data pack folders | 1.20.x uses `recipes`, `loot_tables`, `advancements`, `tags/blocks`, `tags/items`; 1.21 and later use the singular names. |
| Recipe ingredients | Up to 1.21.1 ingredients are `{"item": "..."}` objects; 1.21.4 and 26.3 use plain id strings. |
| Item models | 1.21.4 and later use `assets/<ns>/items/*.json` model definitions and a data-driven tint source for the tuned locator. |
| Composting | A vanilla static map up to 1.21.4; the `compostable` item component on 26.x. |
| Recipe serializer | On 26.x `RecipeSerializer` is a record of codecs rather than an interface. |
| Piglin brain mixin | `Piglin.brainProvider()` no longer exists on 26.3, so the memory modules are injected where the provider is built. |
| Fabric API | The gametest API is bundled in `fabric-api` on 1.20.4 and 1.20.6, and is a separate module from 1.21.1. `BlockRenderLayerMap` and the composting registry are gone on 26.x. |
| NeoForge | 1.20.4 reads `META-INF/mods.toml`; later versions read `neoforge.mods.toml`. The tunings data pack registry event is `DataPackRegistryEvent.NewRegistry` on 1.21.1 (that target has its own `MoreGeodesNeoForge`). |

## Tests

* Unit tests (`test`) run in a plain JVM with the registries bootstrapped by
  `TestBootstrap`. `DataResourcesTest` reads the version under test from the
  `geodes.test.mc` system property.
* GameTests (`runGametest`) start a headless server with a test-only mod
  (`geodes_gametest`) and write a JUnit XML report to
  `targets/<target>/build/gametest-results/gametest.xml`. Fabric targets use the
  Fabric GameTest API; NeoForge targets register the tests through
  `GameTestRegistration` and report through `GametestXmlReporter`.
* `jacocoTestReport` merges unit test and GameTest coverage.
