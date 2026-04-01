# ☁️ AuraRPG: Infinite Progression

**AuraRPG** is a Fabric-based Minecraft mod that transforms your survival experience into a dynamic RPG journey. Every action you take—from swinging a pickaxe to surviving a Creeper blast—makes you stronger. As your power grows, a visible **Aural Cloud** manifests around you, expanding to show the world your status.

-----

## ✨ Core Features

### 📈 The Infinite Scaling System

There are **no level caps**. You can progress your skills infinitely, but the journey becomes steeper the higher you climb.

  * **Progression Curve:** The experience required for the next level follows an exponential formula:
    $$XP_{required} = Base \times (1.15)^{Level}$$
  * **Diminishing Returns:** While power is infinite, the difficulty of leveling ensures that "God-tier" status is earned through true dedication.

### ☁️ The Manifesting Aura

The player is surrounded by a shifting, cloud-like particle aura.

  * **Growth:** The radius of your aura is tied to your **Total Skill Level**.
  * **Visuals:** As you master specific branches, the aura density and particle types evolve.

-----

## 🛠 Skills & Perks

| Skill | Primary Action | Level Bonus |
| :--- | :--- | :--- |
| **Mining** | Breaking stone/ores | Increased mining speed & % chance for double drops. |
| **Woodcutting** | Chopping logs | Increased axe efficiency & chance for whole-tree felling. |
| **Excavation** | Shoveling dirt/sand | Chance to find "buried treasures" (flint, gold nuggets, etc). |
| **Fishing** | Catching fish | Reduced wait time & higher quality loot tables. |
| **Combat** | Dealing damage | Increased base attack damage & critical hit chance. |
| **Defense** | Taking damage | Natural damage reduction & knockback resistance. |
| **Constitution** | General activity | Increases your maximum **Health Hearts** permanently. |
| **Agility** | Sprinting/Jumping | Increased movement speed & reduced fall damage. |

-----

## ⚔️ Combat & Survival Mechanics

### Defense & Health Scaling

Unlike traditional mods, your survivability is reactive:

  * **Defense:** By taking damage and surviving, your body hardens. Every level in Defense adds a small percentage of global damage reduction.
  * **Vitality:** Your health bar is not static. As your total skill level increases, you will unlock extra rows of hearts, allowing you to tank late-game bosses with ease.

-----

## 💻 Technical Setup (For Developers/Jules)

This mod is built using the **Fabric Toolchain** and is designed to be highly compatible with other mods.

### Key Implementation Details:

  * **Data Handling:** Player skills are stored via `Component` attachments to ensure data persists across dimensions and server restarts.
  * **Optimization:** Particle auras are rendered client-side using `ClientTickEvents` to ensure zero impact on server TPS.
  * **Math Logic:** \* Mining Speed Multiplier: $1.0 + (Level \times 0.05)$
      * Aura Radius: $0.5 + (TotalLevel \times 0.02)$

### Requirements:

  * Fabric Loader
  * Fabric API

-----

## 🤖 AI Development Note

This project is maintained and expanded with the assistance of **Jules**, GitHub's AI coding agent.

> **Jules Prompting Tip:** When adding new skills, ensure you hook into the `PlayerBlockBreakEvents` for gathering skills and `LivingHurtEvent` for combat/defense skills. Always check for `player.isCreative()` to prevent XP exploits\!

-----

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](https://www.google.com/search?q=LICENSE) file for details.
