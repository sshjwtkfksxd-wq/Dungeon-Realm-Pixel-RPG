# Dungeon Realm: Pixel RPG (ตำนานผู้กล้าดันเจี้ยน)

An immersive retro pixel-styled Turn-Based Fantasy RPG Android application built with Jetpack Compose. Features hero class selection (Warrior, Mage, Archer), character stat leveling, skill tree progression, multi-floor dungeon exploration with boss fights, equipment forging & upgrading, pet companions, and a gacha summoning shrine.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following choices were selected and confirmed for this RPG build:

- **Confirmed Decision 1 (Combat System)**: Turn-Based Combat (การต่อสู้แบบสลับตาเดิน) with Attack, Class Skill, Consumable Item, and Escape actions, featuring floating damage numbers, HP/MP bars, and status effects.
- **Confirmed Decision 2 (Visual Theme)**: Retro Pixel Fantasy Aesthetic with dark atmospheric dungeon backgrounds, golden arcade badges, high-contrast action buttons, and glowing skill visual indicators.
- **Confirmed Decision 3 (Full Feature Suite)**:
  1. Multi-Floor Dungeon with Mini-Bosses & Floor Bosses
  2. Character Class Selection (Warrior, Mage, Archer) with Stat Point Allocation & Skill Trees
  3. Equipment Shop & Blacksmith Upgrade System (+1 to +10 enhancement)
  4. Pet Companion Sanctuary with active combat assistance
  5. Gacha Summoning Shrine for rare pets and legendary gear

---

## 1. Overview & Core Concept

- **What It Does**: A complete mobile RPG game where players create a custom hero, choose a class, explore dangerous dungeon floors, battle pixel monsters in turn-based combat, collect EXP and gold, allocate stat points (STR, INT, DEX, VIT), unlock active/passive skills, upgrade equipment, summon pet companions, and climb to defeat floor bosses.
- **Target Audience**: Mobile RPG fans, fantasy game enthusiasts, retro arcade lovers, and strategy gamers who enjoy leveling up, building character stats, and collecting loot.
- **Key Value**: Deep, addictive RPG gameplay loop with tactile Compose UI, visual combat feedback, clear progression goals, and offline progress persistence.

---

## 2. User Experience & Visual Design

### Key User Flows

```
 ┌─────────────────────────────────────────────────────────┐
 │               1. Character Creation Screen             │
 │   Choose Name + Class (Warrior / Mage / Archer)         │
 └────────────────────────────┬────────────────────────────┘
                              │
                              ▼
 ┌─────────────────────────────────────────────────────────┐
 │                  2. Town Hub Screen                     │
 │   [Dungeon Gate]  [Hero & Skill]  [Blacksmith Shop]     │
 │   [Pet Sanctuary] [Gacha Shrine]  [Inventory / Save]    │
 └──────┬─────────────────────┬───────────────────┬────────┘
        │                     │                   │
        ▼                     ▼                   ▼
┌──────────────┐      ┌───────────────┐   ┌───────────────┐
│3. Dungeon    │      │4. Blacksmith  │   │5. Pet Sanctuary│
│   Combat     │      │   & Shop      │   │   & Gacha     │
│   Battle     │      │ Buy, Sell,    │   │ Summon & Equip│
│   Monsters & │      │ Forge Gear    │   │ Pet Companions│
│   Bosses     │      └───────────────┘   └───────────────┘
└──────────────┘
```

1. **Character Creation**: Pick hero class (Warrior / นักดาบ, Mage / นักเวท, Archer / นักธนู), view base stat tendencies, set hero name, and start journey.
2. **Town Hub**: Main navigation center showing hero level, HP/MP, gold, gems, and direct tabs to Dungeon, Hero Stats, Blacksmith, Pet House, and Gacha.
3. **Turn-Based Battle View**:
   - **Top Panel**: Enemy Sprite card, HP bar, status icons, floor indicator.
   - **Middle Panel**: Battle Log console with action descriptions (e.g. "Warrior used Power Slash for 45 Damage!"). Floating damage popups.
   - **Bottom Panel**: Hero status (HP/MP bars, Pet status) + Action Command Deck (Attack, Skill list, Inventory potion, Run).
4. **Hero Stats & Skill Tree**: Allocate stat points (STR, INT, DEX, VIT) earned upon leveling up; unlock and upgrade active/passive skills.
5. **Blacksmith & Shop**: Buy weapons/armor, sell unwanted loot, and enhance gear level (+1 to +10) with risk/reward progression.
6. **Pet Companion & Gacha**: Spend Gacha Tickets or Gold to pull common/rare/epic/legendary pets (Slime, Flame Fox, Shadow Dragon, Light Fairy) that accompany the hero into battle.

### Visual Identity & Theme

- **Aesthetic Direction**: Retro Pixel Fantasy Arcade with dark slate backgrounds (`#12131C`), gold accents (`#FFD700`), Mana Blue (`#3B82F6`), Health Crimson (`#EF4444`), and Level Green (`#10B981`).
- **Typography**: High-impact retro header typography, pixel-styled badges, clear numeric indicators for HP/MP/Damage values.
- **Component Styling**: Custom Jetpack Compose Canvas elements for progress bars, glowing skill buttons, animated damage popups, item grid cards with rarity borders (Common: Gray, Rare: Blue, Epic: Purple, Legendary: Gold).

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Jetpack Compose Turn-Based Engine with Reactive StateFlow**
  - *Chosen Approach*: Build a pure Jetpack Compose state-driven turn engine in Kotlin ViewModel.
  - *Why*: Seamless integration with Compose UI, zero heavy 3D engine overhead, instant touch response, and smooth animated transitions for floating damage popups and turn switches.

- **Decision 2: Integrated Local Save Management via DataStore & ViewModel Repository**
  - *Chosen Approach*: Centralized `GameRepository` managing hero stats, inventory items, pets, equipment upgrades, and floor unlocks with automatic local persistence.
  - *Why*: Player effort (Level, Gold, Gear, Pets, Floor progress) is preserved automatically across app restarts without requiring forced cloud sign-in friction.

- **Decision 3: Multi-Class Skill & Pet System**
  - *Chosen Approach*:
    - **Warrior**: High HP/STR, skills like *Heavy Slash*, *Iron Shield*, *Whirlwind*.
    - **Mage**: High MP/INT, skills like *Fireball*, *Ice Spear*, *Arcane Barrier*.
    - **Archer**: High DEX/Speed, skills like *Triple Shot*, *Poison Arrow*, *Evasion*.
    - **Pets**: Provide passive stat boosts and launch automatic companion attacks every N turns.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Jetpack Compose UI                            │
│  ┌─────────────────┐  ┌──────────────────┐  ┌───────────────────────┐  │
│  │ TownHubScreen   │  │ BattleScreen     │  │ HeroSkillScreen       │  │
│  └─────────────────┘  └──────────────────┘  └───────────────────────┘  │
│  ┌─────────────────┐  ┌──────────────────┐  ┌───────────────────────┐  │
│  │ BlacksmithScreen│  │ PetGachaScreen   │  │ CharacterCreateScreen │  │
│  └─────────────────┘  └──────────────────┘  └───────────────────────┘  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ (StateFlow / Event Actions)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                            GameViewModel                               │
│  - Combat Engine (Turn handling, Damage calculations, Battle Log)     │
│  - Hero Leveling & Stat Distribution Manager                           │
│  - Equipment & Blacksmith Upgrade Logic                                │
│  - Pet Collection & Gacha Pull Engine                                  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           GameRepository                               │
│  - In-Memory Game State + Local Persistence Storage                     │
│  - Monsters Database, Skill Data, Item Catalog, Pet Roster             │
└────────────────────────────────────────────────────────────────────────┘
```

### Core Data Models

- `HeroState`: Name, Class, Level, EXP, MaxEXP, HP, MaxHP, MP, MaxMP, STR, INT, DEX, VIT, StatPoints, Gold, Gems, FloorReached, EquippedWeapon, EquippedArmor, ActivePet.
- `Skill`: ID, Name, Description, ManaCost, Cooldown, DamageMultiplier, RequiredLevel, Icon.
- `Item`: ID, Name, Type (Weapon, Armor, Potion), Rarity, StatBonus, BuyPrice, SellPrice, EnhancementLevel.
- `Pet`: ID, Name, Rarity, AttackBonus, PassiveEffect, Element, ImageRes/Icon.
- `Monster`: ID, Name, MaxHP, Attack, Defense, ExpReward, GoldReward, SpriteType, IsBoss.
- `BattleState`: TurnState (PLAYER_TURN, ENEMY_TURN, VICTORY, DEFEAT), CurrentMonster, BattleLogs, DamageFloaters.

---

## Implementation Checklist

- [ ] **Metadata & Identity**: Update `metadata.json` and `strings.xml` to "Dungeon Realm: Pixel RPG".
- [ ] **Theme & Colors**: Configure M3 Retro Fantasy ColorScheme in `Color.kt` and `Theme.kt`.
- [ ] **Data Layer**: Implement `GameModels.kt`, `GameRepository.kt`, and `LocalSaveStorage.kt`.
- [ ] **ViewModel**: Build `GameViewModel.kt` with full turn-based battle engine, leveling logic, item upgrade, gacha rolls, and pet management.
- [ ] **UI Screens**:
  - `CharacterCreationScreen`: Name input + class pick (Warrior, Mage, Archer).
  - `TownHubScreen`: Navigation center with status header & main game modes.
  - `BattleScreen`: Turn-based RPG view with animated HP bars, enemy sprite card, battle log console, floating damage popups, and skill/item menu.
  - `HeroStatsSkillScreen`: Level-up stat point allocation & skill tree.
  - `BlacksmithShopScreen`: Weapon/Armor shop, selling, and gear upgrade (+1 to +10).
  - `PetGachaScreen`: Pet sanctuary roster + Gacha summon animation & reward reveal.
- [ ] **Verification**: Compile applet and test all CUJ state flows cleanly.
