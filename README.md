<div align="center">

<img src="images/SFrame-Banner.png" alt="SlimeFrame banner" width="100%">

# SlimeFrame

**A Warframe-inspired addon for [Slimefun 4](https://github.com/Slimefun/Slimefun4): relics, Prime machines, stackable generators, and villager trade automation.**

[![Java CI](https://github.com/mybezo/SlimeFrame/actions/workflows/maven.yml/badge.svg)](https://github.com/mybezo/SlimeFrame/actions/workflows/maven.yml)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.19%2B-brightgreen)](#requirements)
[![Slimefun](https://img.shields.io/badge/Slimefun-RC--34%2B-orange)](https://github.com/Slimefun/Slimefun4)
[![License](https://img.shields.io/github/license/mybezo/SlimeFrame)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![pt-br](https://img.shields.io/badge/lang-pt--br-green.svg)](README.pt-br.md)

</div>

---

<details>
  <summary><b>Table of Contents</b></summary>

- [About](#about)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Commands & Permissions](#commands--permissions)
- [Gameplay Guide](#gameplay-guide)
  - [New Resources](#new-resources)
  - [Relics](#relics)
  - [Cumulative Generators](#cumulative-generators)
  - [Auto Trader](#auto-trader)
- [Configuration](#configuration)
- [Building from Source](#building-from-source)
- [Contributing](#contributing)
- [Credits](#credits)
- [License](#license)

</details>

## About

SlimeFrame is a Slimefun addon inspired by **Warframe**. It adds new machines, energy generators, tools, armor and a full relic system to make your Slimefun progression more rewarding.

Among other things you get an **Auto Trader** that trades with villagers automatically, an **Item Projector** that displays any item as a hologram, concrete/wool/glass/terracotta generators, a **Chunk Eater**, and much more. Take your time and explore!

## Features

| | |
|---|---|
| 🏺 **Relics** | Earn relics by fishing, fighting, mining and building, then open them for Prime Components. |
| ⚙️ **Prime machines** | Late-game machine tiers crafted with Prime Components. |
| ⚡ **Cumulative generators** | Generators that produce more energy the more of them you stack together. |
| 🤝 **Auto Trader** | Bind a villager's trades to a contract and automate them. |
| ⛏️ **Special ores** | New resources mined from vanilla ores with the Nosam Pickaxe. |
| ❄️ **Cryogenic gear** | Freezing items and the armor that protects you from them. |
| 🖼️ **Item Projector** | Show off any item as a spinning, resizable hologram. |

## Requirements

| Requirement | Version |
|---|---|
| Server software | **Spigot**, **Paper** or a fork (e.g. **Purpur**) |
| Minecraft | **1.19+** (includes compatibility fixes for 1.21 – 26.2) |
| Java | **17+** (Minecraft 1.20.5+ requires **Java 21+**) |
| Slimefun | **RC-34+** |

Optional integrations: **Geyser** (Bedrock player support), **SlimeHUD**, **PlaceholderAPI**, **AdvancedEnchantments**.

## Installation

1. Download the latest `SlimeFrame-vX.Y.Z.jar`:
   - from the [**Actions**](https://github.com/mybezo/SlimeFrame/actions/workflows/maven.yml) tab (open the latest successful run and download the `SlimeFrame` artifact), or
   - by [building it yourself](#building-from-source).
2. Make sure [Slimefun](https://github.com/Slimefun/Slimefun4) is installed.
3. Drop the JAR into your server's `plugins/` folder.
4. Restart the server. A default `plugins/SlimeFrame/config.yml` will be generated.

## Commands & Permissions

The root command is `/sframe` (aliases: `/slimeframe`, `/wf`). Aliases can be changed in `config.yml`.

| Command | Permission | Description |
|---|---|---|
| — | `slimeframe.anyone.*` | Grants every _player_ command (default: everyone) |
| `/sframe relics` | `slimeframe.anyone.inventory` | Open your relic inventory |
| `/sframe refine <refinement>` | `slimeframe.anyone.refine` | Refine the relic in your main hand |
| `/sframe traces [player]` | `slimeframe.anyone.traces` | Show how many Void Traces you (or a player) have |
| — | `slimeframe.admin.*` | Grants every _admin_ command (default: op) |
| `/sframe invsee <player>` | `slimeframe.admin.invsee` | Open another player's relic inventory |

## Gameplay Guide

### New Resources

Many new resources are used to craft the addon's machines and generators. Some of them are **special ores** that can only be obtained by mining vanilla ores with a **Nosam Pickaxe**.

> [!NOTE]
> Blocks placed by players never drop special ores.

### Relics

Relics contain **Prime Components**, which are needed to craft Prime-tier machines, the best machines in the addon. Each relic has **3 Bronze** (common), **2 Silver** (uncommon) and **1 Gold** (rare) reward.

There are four relic eras, each earned through a different activity:

| Era | How to obtain (default) |
|---|---|
| **Lith** | Every **50** fish caught |
| **Meso** | Every **750** entities killed |
| **Neo** | Every **10,000** blocks broken |
| **Axi** | Every **750** blocks placed |

Relics are delivered to a personal **relic inventory**; open it with `/sframe relics`.

#### Opening a Relic

A relic's lore shows two attributes: **Reactants** and **Refinement**.

1. Take **one** relic out of your relic inventory and hold it in your **off hand**.
2. Kill Endermen. Each kill has a **25%** chance to add one reactant.
3. Once the relic reaches **10/10** reactants, right-click it to open it and receive your reward.

> [!TIP]
> **Bedrock players (via Geyser):** put the relic in the **first hotbar slot** instead of the off hand. This feature is experimental, so please report any issues.

#### Refining a Relic

Refining a relic increases the chance of getting the rarer rewards. Use `/sframe refine <refinement>` with the relic in your main hand. Refining costs **Void Traces**: every opened relic gives you 1–20 of them. Check your balance with `/sframe traces`.

| Refinement | Cost | Effect |
|---|---|---|
| `INTACT` | — | Default refinement |
| `EXCEPTIONAL` | 25 traces | Better uncommon/rare odds |
| `FLAWLESS` | 50 traces | Even better odds |
| `RADIANT` | 100 traces | Best odds for the rare reward |

When upgrading, you only pay the difference between the current and the new refinement.

### Cumulative Generators

Cumulative generators check the six blocks directly next to them (north, south, east, west, above and below). For every neighbouring generator **of the same type**, they produce extra energy equal to the **Bonus Energy** shown in their lore.

> [!NOTE]
> Diagonal blocks are not counted.

### Auto Trader

The Auto Trader trades with villagers automatically.

1. Craft a **Merchant Soul Contract**.
2. Right-click a villager with it. The villager is consumed and its trades are sealed into the contract.
3. Put the contract into the Auto Trader's contract slot and click one of the unlocked trades to select it.
4. Supply the input items and the machine does the rest.

> [!TIP]
> **Automating special ores:** whenever a villager unlocks a new trade there is a small chance (`ore-trade-chance`) that it offers one of SlimeFrame's special ores. Seal that villager into a contract and let the Auto Trader farm it for you.

## Configuration

The most important options in `plugins/SlimeFrame/config.yml`:

| Option | Default | Description |
|---|---|---|
| `options.auto-update` | `true` | Automatically download new (non-major) releases |
| `options.auto-update-major` | `false` | Also auto-download major (potentially breaking) releases |
| `options.enable-researches` | `false` | Lock items behind Slimefun researches |
| `options.enable-ae-items-in-soul-contracts` | `false` | Allow AdvancedEnchantments items in soul contracts |
| `options.ore-trade-chance` | `1` | Chance (%) that a new villager trade sells a special ore |
| `relics.lith` / `meso` / `neo` / `axi` | `50` / `750` / `10000` / `750` | Actions required per relic |
| `drop-chance.*` | see file | Drop chances (%) per mob for special drops and reactants |
| `messages.*` | see file | Prefix and player-facing messages |

## Building from Source

You need **JDK 17+** and **Maven 3.8+**.

```bash
git clone https://github.com/mybezo/SlimeFrame.git
cd SlimeFrame
mvn clean package
```

The plugin JAR will be in `target/SlimeFrame-vX.Y.Z.jar`.

## Contributing

Bug reports and pull requests are welcome!

- Found a bug? [Open an issue](https://github.com/mybezo/SlimeFrame/issues/new/choose) and include the output of `/sf versions`.
- Want to contribute code? Fork the repository, create a branch, follow the existing code style (see `.editorconfig`) and open a pull request.

## Credits

- Original addon by [**Voper**](https://github.com/VoperAD/SlimeFrame).
- Heads provided by [Minecraft Heads](https://minecraft-heads.com/).
- Thanks to everyone in the Slimefun Discord programming channel who helped with development, and to the **AbsolutGG** server for testing.

[![Minecraft Heads](https://minecraft-heads.com/images/banners/minecraft-heads_fullbanner_468x60.png)](https://minecraft-heads.com/)

If you want to support the original author, you can donate on [Ko-fi](https://ko-fi.com/voper) 🙂

## License

SlimeFrame is licensed under the [GNU General Public License v3.0](LICENSE).
