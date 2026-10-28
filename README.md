# More Geodes

More Geodes adds emerald, quartz, diamond, echo, lapis lazuli and gypsum geodes to Minecraft, along with the crystal locators used to find them.

This repository is a multi-version edition of [More Geodes](https://github.com/TheDeathlyCow/more-geodes) by TheDeathlyCow. It keeps the upstream mod id (`geodes`) and content, and builds for several Minecraft versions on both Fabric and NeoForge from one source tree.

## Supported versions

| Minecraft | Java | Fabric | NeoForge |
| --------- | ---- | ------ | -------- |
| 1.20.4    | 17   | yes    | yes      |
| 1.20.6    | 21   | yes    | yes      |
| 1.21.1    | 21   | yes    | yes      |
| 1.21.4    | 21   | yes    | yes      |
| 26.3      | 25   | yes    | yes      |

Minecraft 1.20.1 and older are covered by the [upstream project](https://github.com/TheDeathlyCow/more-geodes). Forge is covered by More Geodes Reforged.

The Fabric build for 1.21.1 declares support for 1.21 to 1.21.1, and the 1.21.4 build for 1.21.2 to 1.21.4; the tests run against the version in the table.

## Building

Gradle itself has to run on JDK 25 (the Fabric Loom versions used here require it). Point `JAVA_HOME` at a JDK 25 install; the JDKs the targets compile with (17, 21 and 25) are resolved through Gradle toolchains.

```
./gradlew buildAll       # builds every target into dist/<mc>-<loader>/
./gradlew verifyAll      # unit tests, GameTests and coverage for every target
./gradlew :1.21.1-fabric:build
./gradlew :1.21.1-fabric:runClient
```

On Windows use `gradlew.bat`. Each target is a Gradle project named `<mc>-<loader>`, for example `:1.20.4-neoforge` or `:26.3-fabric`. Per-target tasks include `build`, `test`, `runGametest` and `jacocoTestReport`. Jars are named `more-geodes-<loader>-<mc>-1.9.3.jar`.

`verifyAll --continue` runs the remaining targets when one fails. The layout of the source tree and the differences between versions are described in [docs/source-layout.md](docs/source-layout.md).

# New Geodes

## Emerald Geodes
Emerald geodes behave very similarly to amethyst geodes; they generate at high elevations throughout the Overworld, though they are must abundant in Mountain-type biomes. They are encased in calcite and smooth basalt casings underground and can grow emerald crystal buds. When these buds are harvested, they of course drop emeralds. This provides an automate-able alternative to emerald farming than just trading endlessly. 

## Quartz Geodes
Quartz Geodes generate in The Nether and have many similarities to amethyst geodes and emerald geodes. Quartz buds and clusters grow on budding quartz, but budding quartz cannot be harvested. Quartz clusters will drop quartz when broken, but will drop an item form of itself when broken with silk touch, etc. However, quartz geodes spawn exclusively in The Nether, and with a middle layer of tuff instead of calcite. 

### Quartz Oscillators
Quartz buds and clusters behave as an oscillator when powered by an external redstone source. That is, when a bud or cluster receives power, it will act as a redstone clock, and emitting redstone power on and off at difference frequencies, depending on the size of the bud.

Buds pulse with the following frequencies:

| Block             | Frequency (Hz) |
| ----------------- | -------------- |
| Small Quartz Bud  | 0.5 Hz         |
| Medium Quartz Bud | 1 Hz           |
| Large Quartz Bud  | 2 Hz           |
| Quartz Cluster    | 10 Hz          |

## Diamond Geodes

Diamond geodes generate in all biomes of the Overworld filled with water. Diamond geodes have an outer layer of smooth basalt and a middle layer of 75% deepslate coal ore and 25% smooth basalt. They also only have a single cluster block and do not include a budding block to grow new diamonds. It is best to thing of these geodes as 'goody boxes' of diamonds and coal to be a small boost to your deep mining.

## Echo Geodes

Echo geodes generate exclusively in the Deep Dark. The budding echo block provides a renewable source of Echo Clusters, which can be harvested to obtain Echo Shards. This mod also adds a new use for echo geodes in crafting the [Echo Locator](#echo-locator).

## Lapis Lazuli Geodes

Lapis Lazuli Geodes are a blue geode that generate abundanelty in deserts, and at shallow depths in the rest of the Overworld. They have a middle layer of [Pyrite](#Pyrite). While you can harvest Medium and Large Lapis Lazuli Buds and Lapis Lazuli Clusters to get Lapis Lazuli, you can also harvest Small Lapis Lazuli Buds to get Pyrite Chunks, which can be used to craft Pyrite

## Gypsum Patches

Gypsum Patches are a new type of feature that grow Gypum Roses and can be found in Deserts and Badlands. While not technically a Geode, they are a quite similar idea with crystal blocks, budding blocks, and growable crystals. There are still a number of notable differences though. Firstly, Gypsum Patches generate on the surface rather than underground. The budding blocks also only grow crystals upwards, rather than to any side (though the crystals can still be placed in any direction). Finally, Gypsum Roses can grow to be 2 blocks tall. 

When harvested, Gypsum Roses drop Gypsum Shards, which can be composted, craft sand, cut into blocks, or used to craft the [Crystal Locator](#crystal-locator). Gypsum blocks come in polished, smooth, chiseled, and pillar variants. They are purely decorative, and have a orangish hue. 

# Crystal Locator

The Crystal Locator is an item that can be used to help find geodes. When tuned to the correct Crystal type, they can be used to make any nearby crystal clusters or buds of the corresponding type to glow for about a second and make a dinging sound. They are crafted using 4 Gypsum Shards and 2 sticks. 

### Crystal Locator Tunings

In order to tune a crystal locator, you must craft an untuned base Crystal Locator with its tuning material. These materials include Amethyst Shards, Emeralds, Gypsum Shards, Quartz, Echo Shards, Lapis Lazuli, and Diamonds. There are also tunings available for [Spectrum](https://modrinth.com/mod/spectrum), if it is installed. These Spectrum tunings include Citrine, Moonstone, and Topaz. 

### For Mod and Mod Pack Authors -- Datapack Tunings

If you want to add a tuning for a geode in your own mod or mod packs, you can do so with a datapack.

First, specify the tuning in a file in `data/<namespace>/geodes/tunings/<tuning_path>.json`.

<details>

<summary>
Tuning JSON schema
</summary>

```json5
{
    // A base-10 encoded RGB color. The following example 
    // is the colour #C890F0, which is used for the 
    // Amethyst tuning in More Geodes
    "color": 13144304,

    // A json text component that is used for the
    // item tooltip. Also allows for translation.
    // See: https://minecraft.wiki/w/Raw_JSON_text_format
    "description": {
        "text": "Tuned to Amethyst Crystals",
        "color": "#5d3a9a"
    },

    // A Location predicate. Same as the `predicate` field 
    // in a minecraft:location_check loot condition 
    // See: https://minecraft.wiki/w/Predicate 
    "tuned_to": {
        "block": {
            "blocks": [
                "minecraft:amethyst_cluster",
                "minecraft:large_amethyst_bud",
                "minecraft:medium_amethyst_bud",
                "minecraft:small_amethyst_bud"
            ]
        }
    }
}
```
</details>

If you want to make the tuning craftable, you must use the special `geodes:crystal_tuning` recipe type. This recipe type is very similar to a shapeless recipe, except the output item is replaced by the output tuning. The output item will always be a Tuned Crystal Locator. The recipe goes in `data/<namespace>/recipes/` on 1.20.4 and 1.20.6 and in `data/<namespace>/recipe/` on 1.21 and later.

<details>

<summary> 
Example crystal tuning recipe (1.20.4, 1.20.6 and 1.21.1)
</summary>

```json5

{
    // Custom recipe type
    "type": "geodes:crystal_tuning",
    "group": "geodes_tunings",
    "ingredients": [
        {
            "item": "minecraft:amethyst_shard"
        },
        {
            "item": "geodes:crystal_locator"
        }
    ],
    // Specify the output tuning ID instead
    // of the item. This tuning will be applied
    // to a Tuned Crystal Locator
    "tuning": "geodes:amethyst"
}
```

</details>

On 1.21.4 and 26.3 ingredients are plain item ids instead of `{"item": ...}` objects:

```json
"ingredients": [
    "minecraft:amethyst_shard",
    "geodes:crystal_locator"
]
```

# Echo Locator

The Echo Locator has a similar function to the Crystal Locator, however instead of locating Geodes it can be used to locate ores within 6 blocks of the user. Every ore it detects causes 1 damage to the item. The Echo Locator is crafted from 4 Echo Shards and 2 Bones. This is useful for getting those pesky ores hidden just inside a corner or behind a block of stone next to your strip mine.

The blocks that the Echo Locator can detect is controlled with the block tag `geodes:echo_locatable`. 

# Pyrite

Pyrite, commonly known as Fool's Gold, is the middle layer of Lapis Lazuli Geodes. It is a decorative block with a stair, slab, and wall variants. It also has some similar properties to gold, such as being able to be bartered with Piglins. Though once they figure out your ruse they will be quite angry indeed, and after seeing Pyrite once they will not forget it (though its shine will still be mildly distracting to them).

# Calcite Blocks

This mod also adds a stair, slab, and wall variant of Calcite. They use the vanilla Calcite texture, so resource packs that retexture calcite should work automatically.

# Datapack Integration

This mod uses the namespace `geodes`. If you wish to make changes to the features in this mod using a datapack, all files must be put under that namespace. The emerald and quartz geodes use very similar values to the amethyst geode, and it can be used as an example for how to configure them. Misode's datapack generators are very helpful for this, and can be found [here](https://misode.github.io/).

## Datapack Addons

In the [datapack addons folder](https://github.com/TheDeathlyCow/more-geodes/tree/main/datapack_addons) of the upstream repository you can find downloads for two datapack addons for this mod. They're each pretty self explanatory, and act as ideas for how one might decide to try and make a datapack configuration for this mod. 

## Configured features
You can make changes to the geode configured features by replacing their files in the directories `geodes/worldgen/configured_feature/emerald_geode.json`, `geodes/worldgen/configured_feature/quartz_geode.json`, etc. On 26.3 the directory is `geodes/worldgen/feature/` instead.

## Placed features
Similar to the configured features, you can edit the geode placed features by replacing their files in the directories `geodes/worldgen/placed_feature/emerald_geode.json`, `geodes/worldgen/placed_feature/quartz_geode.json`, etc. 

## Biome placement
Rather than editing or overriding specific biomes in order to add or remove different geode features, it would instead be better to edit the biome tags `geodes:has_emerald_geode`, `geodes:has_quartz_geode`, etc. These tags exist in the directory `geodes/tags/worldgen/biome/`, and the default tags for them can be found [here](https://github.com/TheDeathlyCow/more-geodes/tree/main/src/main/resources/data/geodes/tags/worldgen/biome). Note that Emerald and Lapis geodes generate more frequently in some biomes. These are controlled by the biome tags `geodes:has_extra_emerald_geodes` and `geodes:has_extra_lapis_geodes`.

**Important note**: These changes can ONLY take affect after a server or world restart. A simple reload is not sufficient! 

# Configuration

The generation of each geode can be independently disabled via a config. The config should be placed in your standard config directory, under the file name `geodes.properties`. Any changes to the config require a game or server restart.

The default config is as follows:
```properties
generate_emerald_geodes=true
generate_quartz_geodes=true
generate_diamond_geodes=true
generate_echo_geodes=true
generate_lapis_geodes=true
generate_gypsum_patches=true
```

Note that this only disables the biome modifications, so the command `/place feature` should still work for disabled geodes, and it *shouldn't* break any pre-existing worlds that had these enabled.

Each of these values are booleans and are parsed using [Boolean#parseBoolean](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Boolean.html#parseBoolean(java.lang.String)). Therefore, entering anything other than a case insentive version of "true" will be interpreted as false. 

## Carpet Integration

On Fabric, if you have the [Carpet](https://modrinth.com/mod/carpet) mod installed, turning on the setting `moveableAmethyst` will allow you to pick up Budding blocks with silk touch and push them with pistons.

## AE2 Integration

If you have the [Applied Energistics 2](https://modrinth.com/mod/ae2) mod installed, you'll be able to use the `generate_certus_geodes` setting to find certus quartz geodes in your world. Also, all of this here mod's budding blocks will be able to generate clusters more efficiently using AE2's `Crystal Growth Accelerator`.

## Spectrum Integration

If you have [Spectrum](https://modrinth.com/mod/spectrum) installed, you'll be able to use the `generate_bismuth_geodes` setting to find Bismuth geodes in your world. Furthermore, tunings for Topaz, Citrine, and Moonstone geodes will automatically be added.

# Translations

If you'd like to create translations for More Geodes, the upstream project is on [Crowdin](https://crowdin.com/project/more-geodes). You can also provide translations via issue or pull request if you so wish.

# Credits

More Geodes was created by [TheDeathlyCow](https://github.com/TheDeathlyCow). Contributors to the original project: Nerjal Nosk and SalveMundiProd. Upstream source: https://github.com/TheDeathlyCow/more-geodes

# License

More Geodes is licensed under the GNU Lesser General Public License v3.0; see [LICENSE](LICENSE). This is a modified version of the original; see [NOTICE](NOTICE) for the list of changes and copyright notices.
