package com.example.model

import java.util.UUID

enum class ClassType(
    val titleTh: String,
    val descriptionTh: String,
    val baseHp: Int,
    val baseMp: Int,
    val baseStr: Int,
    val baseInt: Int,
    val baseDex: Int,
    val baseVit: Int,
    val iconSymbol: String
) {
    WARRIOR(
        titleTh = "นักดาบ (Warrior)",
        descriptionTh = "ถึกทน พลังโจมตีกายภาพสูง เหมาะกับการปะทะระยะประชิด",
        baseHp = 150,
        baseMp = 40,
        baseStr = 15,
        baseInt = 5,
        baseDex = 8,
        baseVit = 12,
        iconSymbol = "⚔️"
    ),
    MAGE(
        titleTh = "นักเวท (Mage)",
        descriptionTh = "เชี่ยวชาญการใช้เวทมนตร์รุนแรง พลังมานาสูง แต่พลังป้องกันน้อย",
        baseHp = 90,
        baseMp = 120,
        baseStr = 4,
        baseInt = 18,
        baseDex = 7,
        baseVit = 6,
        iconSymbol = "🔮"
    ),
    ARCHER(
        titleTh = "นักธนู (Archer)",
        descriptionTh = "โจมตีว่องไว อัตราติดคริติคอลสูง มีทักษะการหลบหลีกยอดเยี่ยม",
        baseHp = 110,
        baseMp = 65,
        baseStr = 10,
        baseInt = 7,
        baseDex = 16,
        baseVit = 8,
        iconSymbol = "🏹"
    )
}

enum class ItemType {
    WEAPON, ARMOR, POTION_HP, POTION_MP
}

enum class ItemRarity(val colorHex: Long, val labelTh: String) {
    COMMON(0xFF9E9E9E, "ทั่วไป"),
    RARE(0xFF2196F3, "หายาก"),
    EPIC(0xFF9C27B0, "มหากาพย์"),
    LEGENDARY(0xFFFF9800, "ตำนาน")
}

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: ItemType,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val statBonus: Int, // Attack for weapon, Defense for armor, Heal amount for potion
    val description: String,
    val price: Int,
    var enhancementLevel: Int = 0,
    val iconSymbol: String = "🎒"
) {
    val currentBonus: Int
        get() = statBonus + (enhancementLevel * (statBonus * 0.25).toInt().coerceAtLeast(2))

    val displayName: String
        get() = if (enhancementLevel > 0) "$name +$enhancementLevel" else name
}

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val mpCost: Int,
    val damageMultiplier: Float = 1.0f,
    val healAmount: Int = 0,
    val defenseBuffDuration: Int = 0,
    val reqLevel: Int = 1,
    var level: Int = 1,
    val iconSymbol: String = "✨"
)

enum class PetRarity(val colorHex: Long, val labelTh: String) {
    COMMON(0xFF81C784, "ทั่วไป"),
    RARE(0xFF4FC3F7, "หายาก"),
    EPIC(0xFFBA68C8, "มหากาพย์"),
    LEGENDARY(0xFFFFD54F, "ตำนาน")
}

data class Pet(
    val id: String,
    val name: String,
    val rarity: PetRarity,
    val atkBonus: Int,
    val hpBonus: Int,
    val mpBonus: Int = 0,
    val passiveDesc: String,
    val iconSymbol: String = "🐾"
)

data class Monster(
    val id: String,
    val name: String,
    val maxHp: Int,
    var currentHp: Int,
    val attack: Int,
    val defense: Int,
    val expReward: Int,
    val goldReward: Int,
    val isBoss: Boolean = false,
    val iconSymbol: String = "👾",
    val description: String = ""
)

data class HeroState(
    val name: String = "ผู้กล้าไร้นาม",
    val classType: ClassType = ClassType.WARRIOR,
    val level: Int = 1,
    val exp: Int = 0,
    val maxExp: Int = 100,
    val currentHp: Int = 150,
    val currentMp: Int = 40,
    val str: Int = 15,
    val int: Int = 5,
    val dex: Int = 8,
    val vit: Int = 12,
    val unallocatedStats: Int = 0,
    val gold: Int = 200,
    val gems: Int = 20,
    val maxFloorUnlocked: Int = 1,
    val equippedWeapon: Item? = null,
    val equippedArmor: Item? = null,
    val activePet: Pet? = null,
    val unlockedPets: List<Pet> = emptyList(),
    val inventory: List<Item> = emptyList(),
    val unlockedSkills: List<Skill> = emptyList()
) {
    val maxHp: Int
        get() = classType.baseHp + (vit * 12) + (equippedArmor?.currentBonus ?: 0) + (activePet?.hpBonus ?: 0) + ((level - 1) * 20)

    val maxMp: Int
        get() = classType.baseMp + (int * 10) + (activePet?.mpBonus ?: 0) + ((level - 1) * 10)

    val attackPower: Int
        get() {
            val baseAtk = when (classType) {
                ClassType.WARRIOR -> str * 2 + dex
                ClassType.MAGE -> int * 2.5.toInt()
                ClassType.ARCHER -> dex * 2.2.toInt() + str
            }
            val weaponAtk = equippedWeapon?.currentBonus ?: 0
            val petAtk = activePet?.atkBonus ?: 0
            return baseAtk + weaponAtk + petAtk + (level * 3)
        }

    val defensePower: Int
        get() {
            val baseDef = vit * 1.5.toInt() + (str / 2)
            val armorDef = equippedArmor?.currentBonus ?: 0
            return baseDef + armorDef + (level * 2)
        }
}

enum class BattleTurn {
    PLAYER, MONSTER, VICTORY, DEFEAT
}

data class BattleLog(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val isCritical: Boolean = false,
    val isHeal: Boolean = false,
    val isMonster: Boolean = false
)

data class DamagePopup(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isEnemy: Boolean, // true if popped on enemy, false if popped on hero
    val isCritical: Boolean = false,
    val isHeal: Boolean = false
)
