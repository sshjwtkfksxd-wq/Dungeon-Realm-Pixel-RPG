package com.example.repository

import android.content.Context
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class GameRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("dungeon_realm_save_v1", Context.MODE_PRIVATE)

    // Initial Starter Items
    fun getStarterWeapon(classType: ClassType): Item {
        return when (classType) {
            ClassType.WARRIOR -> Item(
                name = "ดาบเหล็กเก่า (Iron Sword)",
                type = ItemType.WEAPON,
                rarity = ItemRarity.COMMON,
                statBonus = 8,
                description = "ดาบเหล็กมาตรฐานสำหรับนักดาบฝึกหัด",
                price = 50,
                iconSymbol = "🗡️"
            )
            ClassType.MAGE -> Item(
                name = "คฑาไม้โอ๊ค (Oak Staff)",
                type = ItemType.WEAPON,
                rarity = ItemRarity.COMMON,
                statBonus = 10,
                description = "คฑาไม้ที่ลงคาถาพื้นฐานไว้",
                price = 50,
                iconSymbol = "🪄"
            )
            ClassType.ARCHER -> Item(
                name = "ธนูไม้ไผ่ (Bamboo Bow)",
                type = ItemType.WEAPON,
                rarity = ItemRarity.COMMON,
                statBonus = 9,
                description = "ธนูเบา ยิงได้รวดเร็วและแม่นยำ",
                price = 50,
                iconSymbol = "🏹"
            )
        }
    }

    fun getStarterArmor(): Item {
        return Item(
            name = "ชุดเกราะผ้าเบา (Cloth Armor)",
            type = ItemType.ARMOR,
            rarity = ItemRarity.COMMON,
            statBonus = 5,
            description = "เสื้อเกราะเบาที่ช่วยลดแรงกระแทกได้เล็กน้อย",
            price = 40,
            iconSymbol = "🛡️"
        )
    }

    // Default Class Skills Catalog
    fun getClassSkills(classType: ClassType): List<Skill> {
        return when (classType) {
            ClassType.WARRIOR -> listOf(
                Skill(
                    id = "warrior_slash",
                    name = "เพลงดาบฟันหนัก (Heavy Slash)",
                    description = "ฟันศัตรูอย่างรุนแรง สร้างความเสียหาย 160% ของพลังโจมตี",
                    mpCost = 10,
                    damageMultiplier = 1.6f,
                    reqLevel = 1,
                    iconSymbol = "💥"
                ),
                Skill(
                    id = "warrior_shield",
                    name = "โล่เหล็กกล้า (Iron Wall)",
                    description = "ตั้งรับและฟื้นฟู HP 25% ของ MaxHP",
                    mpCost = 15,
                    healAmount = 40,
                    reqLevel = 2,
                    iconSymbol = "🛡️"
                ),
                Skill(
                    id = "warrior_whirlwind",
                    name = "พายุหมุนกวาดล้าง (Whirlwind)",
                    description = "หมุนตัวฟันสร้างความเสียหายมหาศาล 240%",
                    mpCost = 25,
                    damageMultiplier = 2.4f,
                    reqLevel = 4,
                    iconSymbol = "🌀"
                )
            )
            ClassType.MAGE -> listOf(
                Skill(
                    id = "mage_fireball",
                    name = "ลูกไฟเวทมนตร์ (Fireball)",
                    description = "ยิงลูกไฟความร้อนสูง สร้างความเสียหายเวท 180%",
                    mpCost = 15,
                    damageMultiplier = 1.8f,
                    reqLevel = 1,
                    iconSymbol = "🔥"
                ),
                Skill(
                    id = "mage_heal",
                    name = "ละอองฟื้นฟู (Arcane Heal)",
                    description = "ร่ายเวทรักษา ฟื้นฟู HP และ MP ให้ผู้กล้า",
                    mpCost = 20,
                    healAmount = 60,
                    reqLevel = 2,
                    iconSymbol = "✨"
                ),
                Skill(
                    id = "mage_lightning",
                    name = "อัสนีบาตไร้ปรานี (Lightning Nova)",
                    description = "ผ่าสายฟ้าฟ้าร้อง สร้างความเสียหาย 280%",
                    mpCost = 35,
                    damageMultiplier = 2.8f,
                    reqLevel = 4,
                    iconSymbol = "⚡"
                )
            )
            ClassType.ARCHER -> listOf(
                Skill(
                    id = "archer_quickshot",
                    name = "ศรยิงรัว (Double Shot)",
                    description = "ยิงศร 2 ดอกติดกัน สร้างความเสียหาย 150%",
                    mpCost = 12,
                    damageMultiplier = 1.5f,
                    reqLevel = 1,
                    iconSymbol = "🎯"
                ),
                Skill(
                    id = "archer_poison",
                    name = "ศรพิษสังหาร (Poison Arrow)",
                    description = "ยิงศรเคลือบพิษร้ายแรง สร้างความเสียหาย 200%",
                    mpCost = 18,
                    damageMultiplier = 2.0f,
                    reqLevel = 2,
                    iconSymbol = "🧪"
                ),
                Skill(
                    id = "archer_snipestrike",
                    name = "ศรสไนเปอร์ปลิดชีพ (Sniper Shot)",
                    description = "เล็งยิงจุดตาย สร้างความเสียหายคริติคอล 300%",
                    mpCost = 30,
                    damageMultiplier = 3.0f,
                    reqLevel = 4,
                    iconSymbol = "🏹"
                )
            )
        }
    }

    // Shop Catalog Items
    fun getShopItems(): List<Item> {
        return listOf(
            Item(name = "ยาโพชั่น HP เล็ก", type = ItemType.POTION_HP, statBonus = 50, description = "ฟื้นฟู HP 50 หน่วย", price = 30, iconSymbol = "🧪"),
            Item(name = "ยาโพชั่น HP ใหญ่", type = ItemType.POTION_HP, statBonus = 120, description = "ฟื้นฟู HP 120 หน่วย", price = 70, iconSymbol = "🧪"),
            Item(name = "ยาโพชั่น MP เล็ก", type = ItemType.POTION_MP, statBonus = 30, description = "ฟื้นฟู MP 30 หน่วย", price = 30, iconSymbol = "💧"),
            Item(name = "ยาโพชั่น MP ใหญ่", type = ItemType.POTION_MP, statBonus = 80, description = "ฟื้นฟู MP 80 หน่วย", price = 70, iconSymbol = "💧"),
            Item(name = "ดาบอัศวินเหล็กกล้า", type = ItemType.WEAPON, rarity = ItemRarity.RARE, statBonus = 22, description = "ดาบเหล็กเนื้อดี เพิ่มพลังโจมตีอย่างมาก", price = 250, iconSymbol = "⚔️"),
            Item(name = "คฑาแก้วคริสตัล", type = ItemType.WEAPON, rarity = ItemRarity.RARE, statBonus = 26, description = "คฑาฝังคริสตัลประกายแสง", price = 250, iconSymbol = "🔮"),
            Item(name = "ธนูเขาแกะโบราณ", type = ItemType.WEAPON, rarity = ItemRarity.RARE, statBonus = 24, description = "ธนูประดิษฐ์จากเขาแกะภูเขา", price = 250, iconSymbol = "🏹"),
            Item(name = "เกราะเหล็กอัศวิน", type = ItemType.ARMOR, rarity = ItemRarity.RARE, statBonus = 18, description = "เกราะเหล็กหนาช่วยลดความเสียหายกายภาพ", price = 220, iconSymbol = "🛡️"),
            Item(name = "ดาบมังกรเพลิง", type = ItemType.WEAPON, rarity = ItemRarity.EPIC, statBonus = 48, description = "ดาบลงอาคมเพลิงมังกร หลอมจากแร่มืด", price = 800, iconSymbol = "🔥"),
            Item(name = "เกราะเทพสงคราม", type = ItemType.ARMOR, rarity = ItemRarity.LEGENDARY, statBonus = 50, description = "ชุดเกราะระดับตำนานที่ทนทานต่อการโจมตีทุกชนิด", price = 1500, iconSymbol = "👑")
        )
    }

    // All Master Pets
    fun getAllMasterPets(): List<Pet> {
        return listOf(
            Pet("pet_slime", "สไลม์น้อย (Slime)", PetRarity.COMMON, atkBonus = 3, hpBonus = 20, passiveDesc = "เพิ่ม HP 20 และ Atk 3", iconSymbol = "🟢"),
            Pet("pet_fox", "จิ้งจอกอัคคี (Flame Fox)", PetRarity.RARE, atkBonus = 10, hpBonus = 35, passiveDesc = "เพิ่ม Atk 10 และ HP 35", iconSymbol = "🦊"),
            Pet("pet_fairy", "แฟรี่แห่งแสง (Light Fairy)", PetRarity.EPIC, atkBonus = 18, hpBonus = 80, passiveDesc = "เพิ่ม Atk 18 และ HP 80", iconSymbol = "🧚"),
            Pet("pet_dragon", "มังกรทมิฬ (Shadow Dragon)", PetRarity.LEGENDARY, atkBonus = 35, hpBonus = 150, passiveDesc = "เพิ่ม Atk 35 และ HP 150", iconSymbol = "🐉")
        )
    }

    // Dungeon Monsters Floor 1 - 10
    fun getMonstersForFloor(floor: Int): List<Monster> {
        return when (floor) {
            1 -> listOf(
                Monster("m1_1", "สไลม์ป่า (Green Slime)", maxHp = 45, currentHp = 45, attack = 8, defense = 2, expReward = 20, goldReward = 15, iconSymbol = "🟢"),
                Monster("m1_2", "ค้างคาวถ้ำ (Cave Bat)", maxHp = 40, currentHp = 40, attack = 10, defense = 1, expReward = 25, goldReward = 18, iconSymbol = "🦇"),
                Monster("m1_boss", "ราชาสไลม์ยักษ์ (King Slime Boss)", maxHp = 130, currentHp = 130, attack = 18, defense = 5, expReward = 90, goldReward = 100, isBoss = true, iconSymbol = "👑🟢")
            )
            2 -> listOf(
                Monster("m2_1", "ก๊อบลินมีดสั้น (Goblin Scout)", maxHp = 70, currentHp = 70, attack = 14, defense = 4, expReward = 35, goldReward = 28, iconSymbol = "👺"),
                Monster("m2_2", "หมาป่าทมิฬ (Dark Wolf)", maxHp = 80, currentHp = 80, attack = 17, defense = 3, expReward = 45, goldReward = 35, iconSymbol = "🐺"),
                Monster("m2_boss", "หัวหน้าก๊อบลิน (Goblin Chieftain Boss)", maxHp = 220, currentHp = 220, attack = 28, defense = 9, expReward = 150, goldReward = 180, isBoss = true, iconSymbol = "👹")
            )
            3 -> listOf(
                Monster("m3_1", "โครงกระดูกดาบ (Skeleton Warrior)", maxHp = 110, currentHp = 110, attack = 22, defense = 7, expReward = 65, goldReward = 50, iconSymbol = "💀"),
                Monster("m3_2", "แมงมุมพิษ (Poison Spider)", maxHp = 95, currentHp = 95, attack = 26, defense = 5, expReward = 70, goldReward = 55, iconSymbol = "🕷️"),
                Monster("m3_boss", "แม่ทัพโครงกระดูก (Skeleton Warlord Boss)", maxHp = 340, currentHp = 340, attack = 42, defense = 14, expReward = 240, goldReward = 300, isBoss = true, iconSymbol = "☠️⚔️")
            )
            4 -> listOf(
                Monster("m4_1", "ออร์คขวานยักษ์ (Orc Berserker)", maxHp = 160, currentHp = 160, attack = 34, defense = 10, expReward = 100, goldReward = 85, iconSymbol = "🧌"),
                Monster("m4_2", "พรายน้ำแข็ง (Frost Sprite)", maxHp = 140, currentHp = 140, attack = 38, defense = 8, expReward = 110, goldReward = 95, iconSymbol = "❄️"),
                Monster("m4_boss", "ราชินีพรายหิมะ (Frost Queen Boss)", maxHp = 480, currentHp = 480, attack = 58, defense = 18, expReward = 380, goldReward = 450, isBoss = true, iconSymbol = "👸❄️")
            )
            else -> {
                // Higher floors generate scaling encounters
                val scale = floor - 4
                val baseHp = 200 + (scale * 70)
                val baseAtk = 45 + (scale * 12)
                val baseDef = 12 + (scale * 4)
                val exp = 150 + (scale * 60)
                val gold = 120 + (scale * 50)
                listOf(
                    Monster("m_scaled_1", "อสูรเงาพันปี (Shadow Fiend Floor $floor)", maxHp = baseHp, currentHp = baseHp, attack = baseAtk, defense = baseDef, expReward = exp, goldReward = gold, iconSymbol = "👾"),
                    Monster("m_scaled_2", "มังกรศิลา (Stone Golem Floor $floor)", maxHp = baseHp + 50, currentHp = baseHp + 50, attack = baseAtk + 5, defense = baseDef + 6, expReward = exp + 30, goldReward = gold + 25, iconSymbol = "🗿"),
                    Monster("m_boss_scaled", "จอมอสูรดันเจี้ยน (Abyssal Lord Boss Floor $floor)", maxHp = (baseHp * 2.8).toInt(), currentHp = (baseHp * 2.8).toInt(), attack = (baseAtk * 1.6).toInt(), defense = (baseDef * 1.5).toInt(), expReward = exp * 3, goldReward = gold * 3, isBoss = true, iconSymbol = "🔥🐉")
                )
            }
        }
    }

    // Save Game State
    fun saveHeroState(hero: HeroState) {
        val json = JSONObject().apply {
            put("name", hero.name)
            put("classType", hero.classType.name)
            put("level", hero.level)
            put("exp", hero.exp)
            put("maxExp", hero.maxExp)
            put("currentHp", hero.currentHp)
            put("currentMp", hero.currentMp)
            put("str", hero.str)
            put("int", hero.int)
            put("dex", hero.dex)
            put("vit", hero.vit)
            put("unallocatedStats", hero.unallocatedStats)
            put("gold", hero.gold)
            put("gems", hero.gems)
            put("maxFloorUnlocked", hero.maxFloorUnlocked)

            // Save active pet id
            put("activePetId", hero.activePet?.id ?: "")

            // Save unlocked pets
            val petsArr = JSONArray()
            hero.unlockedPets.forEach { petsArr.put(it.id) }
            put("unlockedPets", petsArr)

            // Save items inventory
            val invArr = JSONArray()
            hero.inventory.forEach { item ->
                val itemObj = JSONObject().apply {
                    put("name", item.name)
                    put("type", item.type.name)
                    put("rarity", item.rarity.name)
                    put("statBonus", item.statBonus)
                    put("description", item.description)
                    put("price", item.price)
                    put("enhancementLevel", item.enhancementLevel)
                    put("iconSymbol", item.iconSymbol)
                }
                invArr.put(itemObj)
            }
            put("inventory", invArr)

            // Save equipped weapon
            hero.equippedWeapon?.let { w ->
                val wObj = JSONObject().apply {
                    put("name", w.name)
                    put("type", w.type.name)
                    put("rarity", w.rarity.name)
                    put("statBonus", w.statBonus)
                    put("description", w.description)
                    put("price", w.price)
                    put("enhancementLevel", w.enhancementLevel)
                    put("iconSymbol", w.iconSymbol)
                }
                put("equippedWeapon", wObj)
            }

            // Save equipped armor
            hero.equippedArmor?.let { a ->
                val aObj = JSONObject().apply {
                    put("name", a.name)
                    put("type", a.type.name)
                    put("rarity", a.rarity.name)
                    put("statBonus", a.statBonus)
                    put("description", a.description)
                    put("price", a.price)
                    put("enhancementLevel", a.enhancementLevel)
                    put("iconSymbol", a.iconSymbol)
                }
                put("equippedArmor", aObj)
            }
        }

        prefs.edit().putString("save_data", json.toString()).apply()
    }

    // Load Game State
    fun loadHeroState(): HeroState? {
        val saveStr = prefs.getString("save_data", null) ?: return null
        return try {
            val json = JSONObject(saveStr)
            val classType = ClassType.valueOf(json.optString("classType", "WARRIOR"))
            val masterPets = getAllMasterPets().associateBy { it.id }

            val unlockedPetsIds = mutableListOf<String>()
            val petsArr = json.optJSONArray("unlockedPets")
            if (petsArr != null) {
                for (i in 0 until petsArr.length()) {
                    unlockedPetsIds.add(petsArr.getString(i))
                }
            }
            val unlockedPetsList = unlockedPetsIds.mapNotNull { masterPets[it] }
            val activePetId = json.optString("activePetId", "")
            val activePet = masterPets[activePetId]

            // Inventory parsing
            val inventoryList = mutableListOf<Item>()
            val invArr = json.optJSONArray("inventory")
            if (invArr != null) {
                for (i in 0 until invArr.length()) {
                    val obj = invArr.getJSONObject(i)
                    inventoryList.add(
                        Item(
                            name = obj.getString("name"),
                            type = ItemType.valueOf(obj.getString("type")),
                            rarity = ItemRarity.valueOf(obj.getString("rarity")),
                            statBonus = obj.getInt("statBonus"),
                            description = obj.getString("description"),
                            price = obj.getInt("price"),
                            enhancementLevel = obj.optInt("enhancementLevel", 0),
                            iconSymbol = obj.optString("iconSymbol", "🎒")
                        )
                    )
                }
            }

            // Equipped parse
            var weapon: Item? = null
            json.optJSONObject("equippedWeapon")?.let { wObj ->
                weapon = Item(
                    name = wObj.getString("name"),
                    type = ItemType.valueOf(wObj.getString("type")),
                    rarity = ItemRarity.valueOf(wObj.getString("rarity")),
                    statBonus = wObj.getInt("statBonus"),
                    description = wObj.getString("description"),
                    price = wObj.getInt("price"),
                    enhancementLevel = wObj.optInt("enhancementLevel", 0),
                    iconSymbol = wObj.optString("iconSymbol", "🗡️")
                )
            }

            var armor: Item? = null
            json.optJSONObject("equippedArmor")?.let { aObj ->
                armor = Item(
                    name = aObj.getString("name"),
                    type = ItemType.valueOf(aObj.getString("type")),
                    rarity = ItemRarity.valueOf(aObj.getString("rarity")),
                    statBonus = aObj.getInt("statBonus"),
                    description = aObj.getString("description"),
                    price = aObj.getInt("price"),
                    enhancementLevel = aObj.optInt("enhancementLevel", 0),
                    iconSymbol = aObj.optString("iconSymbol", "🛡️")
                )
            }

            HeroState(
                name = json.optString("name", "ผู้กล้าไร้นาม"),
                classType = classType,
                level = json.optInt("level", 1),
                exp = json.optInt("exp", 0),
                maxExp = json.optInt("maxExp", 100),
                currentHp = json.optInt("currentHp", 150),
                currentMp = json.optInt("currentMp", 40),
                str = json.optInt("str", classType.baseStr),
                int = json.optInt("int", classType.baseInt),
                dex = json.optInt("dex", classType.baseDex),
                vit = json.optInt("vit", classType.baseVit),
                unallocatedStats = json.optInt("unallocatedStats", 0),
                gold = json.optInt("gold", 200),
                gems = json.optInt("gems", 20),
                maxFloorUnlocked = json.optInt("maxFloorUnlocked", 1),
                equippedWeapon = weapon,
                equippedArmor = armor,
                activePet = activePet,
                unlockedPets = unlockedPetsList,
                inventory = inventoryList,
                unlockedSkills = getClassSkills(classType)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearSave() {
        prefs.edit().clear().apply()
    }
}
