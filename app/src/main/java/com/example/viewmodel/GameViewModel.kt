package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Screen {
    CREATE_HERO,
    TOWN_HUB,
    BATTLE,
    HERO_STATS,
    BLACKSMITH,
    PET_GACHA,
    INVENTORY,
    ADMIN_PANEL
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application.applicationContext)

    // Current Active Screen
    private val _currentScreen = MutableStateFlow(Screen.CREATE_HERO)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Hero Game State
    private val _heroState = MutableStateFlow(HeroState())
    val heroState: StateFlow<HeroState> = _heroState.asStateFlow()

    // Battle State
    private val _selectedFloor = MutableStateFlow(1)
    val selectedFloor: StateFlow<Int> = _selectedFloor.asStateFlow()

    private val _currentMonsterList = MutableStateFlow<List<Monster>>(emptyList())
    val currentMonsterList: StateFlow<List<Monster>> = _currentMonsterList.asStateFlow()

    private val _currentMonsterIndex = MutableStateFlow(0)
    val currentMonsterIndex: StateFlow<Int> = _currentMonsterIndex.asStateFlow()

    private val _currentMonster = MutableStateFlow<Monster?>(null)
    val currentMonster: StateFlow<Monster?> = _currentMonster.asStateFlow()

    private val _battleTurn = MutableStateFlow(BattleTurn.PLAYER)
    val battleTurn: StateFlow<BattleTurn> = _battleTurn.asStateFlow()

    private val _battleLogs = MutableStateFlow<List<BattleLog>>(emptyList())
    val battleLogs: StateFlow<List<BattleLog>> = _battleLogs.asStateFlow()

    private val _damagePopups = MutableStateFlow<List<DamagePopup>>(emptyList())
    val damagePopups: StateFlow<List<DamagePopup>> = _damagePopups.asStateFlow()

    private val _turnCount = MutableStateFlow(0)

    // Blacksmith & Forge State
    private val _forgeMessage = MutableStateFlow<String?>(null)
    val forgeMessage: StateFlow<String?> = _forgeMessage.asStateFlow()

    // Gacha State
    private val _gachaResult = MutableStateFlow<Pet?>(null)
    val gachaResult: StateFlow<Pet?> = _gachaResult.asStateFlow()

    private val _isGachaPulling = MutableStateFlow(false)
    val isGachaPulling: StateFlow<Boolean> = _isGachaPulling.asStateFlow()

    init {
        // Load existing saved game if available
        val savedHero = repository.loadHeroState()
        if (savedHero != null) {
            _heroState.value = savedHero
            _currentScreen.value = Screen.TOWN_HUB
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun createCharacter(name: String, classType: ClassType) {
        val heroName = if (name.isBlank()) "ผู้กล้าแห่งโชคชะตา" else name.trim()
        val starterWeapon = repository.getStarterWeapon(classType)
        val starterArmor = repository.getStarterArmor()
        val defaultSkills = repository.getClassSkills(classType)
        val starterPet = repository.getAllMasterPets().first()

        val newHero = HeroState(
            name = heroName,
            classType = classType,
            level = 1,
            exp = 0,
            maxExp = 100,
            str = classType.baseStr,
            int = classType.baseInt,
            dex = classType.baseDex,
            vit = classType.baseVit,
            currentHp = classType.baseHp + (classType.baseVit * 12) + starterArmor.statBonus + starterPet.hpBonus,
            currentMp = classType.baseMp + (classType.baseInt * 10),
            gold = 300,
            gems = 50,
            equippedWeapon = starterWeapon,
            equippedArmor = starterArmor,
            activePet = starterPet,
            unlockedPets = listOf(starterPet),
            inventory = listOf(
                Item(name = "ยาโพชั่น HP เล็ก", type = ItemType.POTION_HP, statBonus = 60, description = "ฟื้นฟู HP 60 หน่วย", price = 30, iconSymbol = "🧪"),
                Item(name = "ยาโพชั่น HP เล็ก", type = ItemType.POTION_HP, statBonus = 60, description = "ฟื้นฟู HP 60 หน่วย", price = 30, iconSymbol = "🧪"),
                Item(name = "ยาโพชั่น MP เล็ก", type = ItemType.POTION_MP, statBonus = 40, description = "ฟื้นฟู MP 40 หน่วย", price = 30, iconSymbol = "💧")
            ),
            unlockedSkills = defaultSkills
        )

        _heroState.value = newHero
        repository.saveHeroState(newHero)
        _currentScreen.value = Screen.TOWN_HUB
    }

    // --- DUNGEON & COMBAT LOGIC ---

    fun selectDungeonFloor(floor: Int) {
        if (floor <= _heroState.value.maxFloorUnlocked) {
            _selectedFloor.value = floor
        }
    }

    fun startDungeonBattle() {
        val floor = _selectedFloor.value
        val rawMonsters = repository.getMonstersForFloor(floor)
        // Clone monster objects so HP resets
        val monsters = rawMonsters.map { it.copy(currentHp = it.maxHp) }
        _currentMonsterList.value = monsters
        _currentMonsterIndex.value = 0
        _currentMonster.value = monsters.firstOrNull()
        _battleTurn.value = BattleTurn.PLAYER
        _turnCount.value = 0
        _damagePopups.value = emptyList()

        _battleLogs.value = listOf(
            BattleLog(message = "⚔️ เข้าสู่ดันเจี้ยนชั้นที่ $floor! เผชิญหน้ากับ ${_currentMonster.value?.name}")
        )

        _currentScreen.value = Screen.BATTLE
    }

    fun performPlayerAttack() {
        val monster = _currentMonster.value ?: return
        if (_battleTurn.value != BattleTurn.PLAYER) return

        viewModelScope.launch {
            _turnCount.update { it + 1 }
            val hero = _heroState.value

            // Critical hit check
            val critRoll = Random.nextInt(100)
            val isCrit = critRoll < (5 + hero.dex)
            val critMult = if (isCrit) 1.8f else 1.0f

            val rawDmg = (hero.attackPower * (0.9 + Random.nextDouble(0.2)) * critMult).toInt()
            val netDmg = (rawDmg - (monster.defense / 2)).coerceAtLeast(1)

            val updatedMonsterHp = (monster.currentHp - netDmg).coerceAtLeast(0)
            val updatedMonster = monster.copy(currentHp = updatedMonsterHp)
            _currentMonster.value = updatedMonster

            // Add damage popup
            addDamagePopup(text = "-$netDmg", isEnemy = true, isCritical = isCrit)

            val logMsg = if (isCrit) {
                "💥 ${hero.name} โจมตีจุดตาย (CRITICAL)! สร้างความเสียหาย $netDmg แก่ ${monster.name}"
            } else {
                "⚔️ ${hero.name} โจมตีสร้างความเสียหาย $netDmg แก่ ${monster.name}"
            }
            addBattleLog(logMsg, isCritical = isCrit)

            // Pet companion assist action every 2 turns
            if (_turnCount.value % 2 == 0 && hero.activePet != null) {
                delay(300)
                val pet = hero.activePet
                val petDmg = (pet.atkBonus * 1.5 + 5).toInt()
                val monsterHpAfterPet = (updatedMonster.currentHp - petDmg).coerceAtLeast(0)
                _currentMonster.value = updatedMonster.copy(currentHp = monsterHpAfterPet)
                addDamagePopup(text = "-$petDmg", isEnemy = true)
                addBattleLog("🐾 สัตว์เลี้ยง [${pet.name}] ช่วยจู่โจมซ้ำ สร้างความเสียหาย $petDmg!")
            }

            // Check if monster defeated
            val finalHp = _currentMonster.value?.currentHp ?: 0
            if (finalHp <= 0) {
                delay(400)
                handleMonsterDefeated(updatedMonster)
            } else {
                // Switch turn to monster
                _battleTurn.value = BattleTurn.MONSTER
                delay(800)
                performMonsterAttack()
            }
        }
    }

    fun castSkill(skill: Skill) {
        val hero = _heroState.value
        val monster = _currentMonster.value ?: return
        if (_battleTurn.value != BattleTurn.PLAYER) return

        if (hero.currentMp < skill.mpCost) {
            addBattleLog("⚠️ มานา (MP) ไม่เพียงพอสำหรับการใช้สกิล ${skill.name}!", isMonster = false)
            return
        }

        viewModelScope.launch {
            _turnCount.update { it + 1 }
            // Deduct MP
            _heroState.update { it.copy(currentMp = (it.currentMp - skill.mpCost).coerceAtLeast(0)) }

            // Check if skill is heal
            if (skill.healAmount > 0) {
                val healVal = skill.healAmount + (hero.int * 2)
                val newHp = (hero.currentHp + healVal).coerceAtMost(hero.maxHp)
                _heroState.update { it.copy(currentHp = newHp) }
                addDamagePopup(text = "+$healVal HP", isEnemy = false, isHeal = true)
                addBattleLog("✨ ${hero.name} ร่ายสกิล [${skill.name}] ฟื้นฟู HP $healVal หน่วย!", isHeal = true)
            }

            // Check if skill does attack damage
            if (skill.damageMultiplier > 0) {
                val rawDmg = (hero.attackPower * skill.damageMultiplier * (0.95 + Random.nextDouble(0.1))).toInt()
                val netDmg = (rawDmg - (monster.defense / 3)).coerceAtLeast(2)
                val updatedMonsterHp = (monster.currentHp - netDmg).coerceAtLeast(0)
                _currentMonster.value = monster.copy(currentHp = updatedMonsterHp)

                addDamagePopup(text = "-$netDmg", isEnemy = true, isCritical = true)
                addBattleLog("🔥 ${hero.name} ใช้สกิล [${skill.name}] ปล่อยพลังสร้างความเสียหาย $netDmg!", isCritical = true)
            }

            val finalHp = _currentMonster.value?.currentHp ?: 0
            if (finalHp <= 0) {
                delay(400)
                handleMonsterDefeated(monster)
            } else {
                _battleTurn.value = BattleTurn.MONSTER
                delay(800)
                performMonsterAttack()
            }
        }
    }

    fun useBattlePotion(item: Item) {
        val hero = _heroState.value
        if (_battleTurn.value != BattleTurn.PLAYER) return

        if (item.type == ItemType.POTION_HP) {
            val healAmount = item.statBonus
            val newHp = (hero.currentHp + healAmount).coerceAtMost(hero.maxHp)
            _heroState.update { state ->
                val newInv = state.inventory.toMutableList()
                newInv.remove(item)
                state.copy(currentHp = newHp, inventory = newInv)
            }
            addDamagePopup(text = "+$healAmount HP", isEnemy = false, isHeal = true)
            addBattleLog("🧪 ดื่ม ${item.name} ฟื้นฟู HP $healAmount หน่วย!", isHeal = true)
        } else if (item.type == ItemType.POTION_MP) {
            val mpAmount = item.statBonus
            val newMp = (hero.currentMp + mpAmount).coerceAtMost(hero.maxMp)
            _heroState.update { state ->
                val newInv = state.inventory.toMutableList()
                newInv.remove(item)
                state.copy(currentMp = newMp, inventory = newInv)
            }
            addDamagePopup(text = "+$mpAmount MP", isEnemy = false, isHeal = true)
            addBattleLog("💧 ดื่ม ${item.name} ฟื้นฟู MP $mpAmount หน่วย!", isHeal = true)
        }

        repository.saveHeroState(_heroState.value)
    }

    private suspend fun performMonsterAttack() {
        val monster = _currentMonster.value ?: return
        val hero = _heroState.value

        val rawDmg = (monster.attack * (0.9 + Random.nextDouble(0.2))).toInt()
        val netDmg = (rawDmg - (hero.defensePower / 2)).coerceAtLeast(1)

        val newHeroHp = (hero.currentHp - netDmg).coerceAtLeast(0)
        _heroState.update { it.copy(currentHp = newHeroHp) }

        addDamagePopup(text = "-$netDmg", isEnemy = false)
        addBattleLog("👹 ${monster.name} โจมตีสวนกลับ! ${hero.name} สูญเสีย HP $netDmg", isMonster = true)

        if (newHeroHp <= 0) {
            delay(500)
            _battleTurn.value = BattleTurn.DEFEAT
            addBattleLog("💀 ผู้กล้า ${hero.name} พ่ายแพ้แก่ ${monster.name}...", isMonster = true)
        } else {
            _battleTurn.value = BattleTurn.PLAYER
        }
    }

    private fun handleMonsterDefeated(monster: Monster) {
        val hero = _heroState.value
        val gainedExp = monster.expReward
        val gainedGold = monster.goldReward

        addBattleLog("🎉 ปราบ ${monster.name} สำเร็จ! ได้รับ +$gainedExp EXP และ +$gainedGold Gold!", isHeal = true)

        var newExp = hero.exp + gainedExp
        var newLevel = hero.level
        var newMaxExp = hero.maxExp
        var newStats = hero.unallocatedStats
        var leveledUp = false

        while (newExp >= newMaxExp) {
            newExp -= newMaxExp
            newLevel += 1
            newMaxExp = (newMaxExp * 1.35).toInt()
            newStats += 5
            leveledUp = true
        }

        val nextMonsterIndex = _currentMonsterIndex.value + 1
        val monsterList = _currentMonsterList.value

        _heroState.update { h ->
            val updatedGold = h.gold + gainedGold
            val updatedHp = if (leveledUp) h.maxHp else h.currentHp
            val updatedMp = if (leveledUp) h.maxMp else h.currentMp

            h.copy(
                level = newLevel,
                exp = newExp,
                maxExp = newMaxExp,
                unallocatedStats = newStats,
                gold = updatedGold,
                currentHp = updatedHp,
                currentMp = updatedMp
            )
        }

        if (leveledUp) {
            addBattleLog("⭐ LEVEL UP! ผู้กล้าบรรลุเลเวล $newLevel (+5 พอยท์สแตท)", isCritical = true)
        }

        if (nextMonsterIndex < monsterList.size) {
            // Next monster in current floor
            _currentMonsterIndex.value = nextMonsterIndex
            _currentMonster.value = monsterList[nextMonsterIndex]
            _battleTurn.value = BattleTurn.PLAYER
            addBattleLog("⚔️ ศัตรูตัวถัดไปปรากฏตัว: ${_currentMonster.value?.name}")
        } else {
            // Cleared floor!
            _battleTurn.value = BattleTurn.VICTORY
            val currentFloor = _selectedFloor.value
            val nextFloor = currentFloor + 1
            val newMaxUnlocked = _heroState.value.maxFloorUnlocked.coerceAtLeast(nextFloor)
            _heroState.update { it.copy(maxFloorUnlocked = newMaxUnlocked) }

            addBattleLog("🏆 เคลียร์ดันเจี้ยนชั้นที่ $currentFloor สมบูรณ์! ปลดล็อกดันเจี้ยนชั้นที่ $nextFloor", isCritical = true)
        }

        repository.saveHeroState(_heroState.value)
    }

    fun escapeDungeon() {
        addBattleLog("🏃 หลบหนีออกจากดันเจี้ยนสำเร็จ!")
        _currentScreen.value = Screen.TOWN_HUB
    }

    // --- HERO STATS & SKILLS ---

    fun allocateStat(statType: String) {
        val hero = _heroState.value
        if (hero.unallocatedStats <= 0) return

        val updatedHero = when (statType) {
            "STR" -> hero.copy(str = hero.str + 1, unallocatedStats = hero.unallocatedStats - 1)
            "INT" -> hero.copy(int = hero.int + 1, unallocatedStats = hero.unallocatedStats - 1)
            "DEX" -> hero.copy(dex = hero.dex + 1, unallocatedStats = hero.unallocatedStats - 1)
            "VIT" -> hero.copy(vit = hero.vit + 1, unallocatedStats = hero.unallocatedStats - 1)
            else -> hero
        }

        _heroState.value = updatedHero
        repository.saveHeroState(updatedHero)
    }

    // --- BLACKSMITH & SHOP LOGIC ---

    fun buyItem(item: Item) {
        val hero = _heroState.value
        if (hero.gold < item.price) {
            _forgeMessage.value = "⚠️ ทองไม่เพียงพอสำหรับซื้อ ${item.name}"
            return
        }

        val newGold = hero.gold - item.price
        val newInventory = hero.inventory + item.copy()

        val updatedHero = hero.copy(gold = newGold, inventory = newInventory)
        _heroState.value = updatedHero
        repository.saveHeroState(updatedHero)
        _forgeMessage.value = "✅ ซื้อ ${item.name} สำเร็จ!"
    }

    fun sellItem(item: Item) {
        val hero = _heroState.value
        val sellPrice = item.price / 2
        val newInventory = hero.inventory.toMutableList().apply { remove(item) }
        val updatedHero = hero.copy(gold = hero.gold + sellPrice, inventory = newInventory)

        _heroState.value = updatedHero
        repository.saveHeroState(updatedHero)
        _forgeMessage.value = "💰 ขาย ${item.name} ได้รับ $sellPrice Gold"
    }

    fun upgradeEquipment(item: Item) {
        val hero = _heroState.value
        val upgradeCost = 80 + (item.enhancementLevel * 60)

        if (hero.gold < upgradeCost) {
            _forgeMessage.value = "⚠️ ทองไม่เพียงพอสำหรับตีบวก (ต้องการ $upgradeCost Gold)"
            return
        }

        val successRate = (100 - (item.enhancementLevel * 10)).coerceAtLeast(30)
        val roll = Random.nextInt(100)
        val isSuccess = roll < successRate

        val newGold = hero.gold - upgradeCost

        if (isSuccess) {
            val updatedLevel = item.enhancementLevel + 1
            val updatedInventory = hero.inventory.map {
                if (it.id == item.id) it.copy(enhancementLevel = updatedLevel) else it
            }
            val updatedWeapon = if (hero.equippedWeapon?.id == item.id) hero.equippedWeapon.copy(enhancementLevel = updatedLevel) else hero.equippedWeapon
            val updatedArmor = if (hero.equippedArmor?.id == item.id) hero.equippedArmor.copy(enhancementLevel = updatedLevel) else hero.equippedArmor

            val updatedHero = hero.copy(
                gold = newGold,
                inventory = updatedInventory,
                equippedWeapon = updatedWeapon,
                equippedArmor = updatedArmor
            )
            _heroState.value = updatedHero
            repository.saveHeroState(updatedHero)
            _forgeMessage.value = "🔥 ตีบวกสำเร็จ! [${item.name} +$updatedLevel] (+อัตราสแตทโบนัส)"
        } else {
            val updatedHero = hero.copy(gold = newGold)
            _heroState.value = updatedHero
            repository.saveHeroState(updatedHero)
            _forgeMessage.value = "💥 ตีบวกล้มเหลว! ไอเทมไม่ถูกทำลาย แต่สูญเสียวัตถุดิบและทอง"
        }
    }

    fun equipItem(item: Item) {
        val hero = _heroState.value
        val updatedHero = when (item.type) {
            ItemType.WEAPON -> hero.copy(equippedWeapon = item)
            ItemType.ARMOR -> hero.copy(equippedArmor = item)
            else -> hero
        }
        _heroState.value = updatedHero
        repository.saveHeroState(updatedHero)
        _forgeMessage.value = "⚔️ ติดตั้ง ${item.displayName} เรียบร้อย"
    }

    // --- PET & GACHA LOGIC ---

    fun pullPetGacha() {
        val hero = _heroState.value
        val costGems = 10
        val costGold = 250

        if (hero.gems < costGems && hero.gold < costGold) {
            _forgeMessage.value = "⚠️ เพชรหรือทองไม่เพียงพอสำหรับสุ่มกาชา (ต้องการ 10 Gems หรือ 250 Gold)"
            return
        }

        val masterPets = repository.getAllMasterPets()
        val roll = Random.nextInt(100)

        val pulledRarity = when {
            roll < 5 -> PetRarity.LEGENDARY
            roll < 20 -> PetRarity.EPIC
            roll < 50 -> PetRarity.RARE
            else -> PetRarity.COMMON
        }

        val pool = masterPets.filter { it.rarity == pulledRarity }
        val pulledPet = pool.randomOrNull() ?: masterPets.random()

        val newGems = if (hero.gems >= costGems) hero.gems - costGems else hero.gems
        val newGold = if (hero.gems < costGems) hero.gold - costGold else hero.gold

        val isAlreadyOwned = hero.unlockedPets.any { it.id == pulledPet.id }
        val newUnlockedPets = if (isAlreadyOwned) hero.unlockedPets else hero.unlockedPets + pulledPet

        val updatedHero = hero.copy(
            gems = newGems,
            gold = newGold,
            unlockedPets = newUnlockedPets,
            activePet = hero.activePet ?: pulledPet
        )

        _gachaResult.value = pulledPet
        _heroState.value = updatedHero
        repository.saveHeroState(updatedHero)
    }

    fun equipPet(pet: Pet) {
        val hero = _heroState.value
        val updatedHero = hero.copy(activePet = pet)
        _heroState.value = updatedHero
        repository.saveHeroState(updatedHero)
    }

    // --- UTILS & HELPERS ---

    private fun addBattleLog(msg: String, isCritical: Boolean = false, isHeal: Boolean = false, isMonster: Boolean = false) {
        _battleLogs.update { logs ->
            listOf(BattleLog(message = msg, isCritical = isCritical, isHeal = isHeal, isMonster = isMonster)) + logs
        }
    }

    private fun addDamagePopup(text: String, isEnemy: Boolean, isCritical: Boolean = false, isHeal: Boolean = false) {
        val popup = DamagePopup(text = text, isEnemy = isEnemy, isCritical = isCritical, isHeal = isHeal)
        _damagePopups.update { popups -> popups + popup }

        viewModelScope.launch {
            delay(1200)
            _damagePopups.update { popups -> popups.filter { it.id != popup.id } }
        }
    }

    fun resetGameProgress() {
        repository.clearSave()
        _heroState.value = HeroState()
        _currentScreen.value = Screen.CREATE_HERO
    }

    // --- ADMIN MODE & CHEAT ENGINE ---

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _adminLogMsg = MutableStateFlow<String?>(null)
    val adminLogMsg: StateFlow<String?> = _adminLogMsg.asStateFlow()

    fun unlockAdminMode(password: String): Boolean {
        val cleanPass = password.trim()
        return if (cleanPass.equals("Admin", ignoreCase = true)) {
            _isAdminUnlocked.value = true
            _adminLogMsg.value = "🔓 ปลดล็อกสิทธิ์ Admin Console สำเร็จ!"
            true
        } else {
            _adminLogMsg.value = "❌ รหัสผ่านไม่ถูกต้อง! (รหัสผ่านคือ: Admin)"
            false
        }
    }

    fun lockAdminMode() {
        _isAdminUnlocked.value = false
        _adminLogMsg.value = null
    }

    fun adminAddGold(amount: Int) {
        val updated = _heroState.value.copy(gold = (_heroState.value.gold + amount).coerceAtLeast(0))
        _heroState.value = updated
        repository.saveHeroState(updated)
        _adminLogMsg.value = "✨ เสกเงิน Gold +$amount (รวม: ${updated.gold} G)"
    }

    fun adminAddGems(amount: Int) {
        val updated = _heroState.value.copy(gems = (_heroState.value.gems + amount).coerceAtLeast(0))
        _heroState.value = updated
        repository.saveHeroState(updated)
        _adminLogMsg.value = "💎 เสกเพชร Gems +$amount (รวม: ${updated.gems} Gems)"
    }

    fun adminSetLevel(newLevel: Int) {
        val levelClamped = newLevel.coerceIn(1, 99)
        val levelDiff = (levelClamped - _heroState.value.level).coerceAtLeast(0)
        val updated = _heroState.value.copy(
            level = levelClamped,
            exp = 0,
            maxExp = 100 + (levelClamped * 50),
            unallocatedStats = _heroState.value.unallocatedStats + (levelDiff * 5)
        )
        val refreshed = updated.copy(currentHp = updated.maxHp, currentMp = updated.maxMp)
        _heroState.value = refreshed
        repository.saveHeroState(refreshed)
        _adminLogMsg.value = "⚡ ปรับเลเวลเป็น Lv.$levelClamped (ฟื้นฟู HP/MP เต็ม)"
    }

    fun adminGiveItem(item: Item, enhancement: Int = 0) {
        val newItem = item.copy(enhancementLevel = enhancement)
        val updatedInv = _heroState.value.inventory + newItem
        val updated = _heroState.value.copy(inventory = updatedInv)
        _heroState.value = updated
        repository.saveHeroState(updated)
        _adminLogMsg.value = "🎒 เสกไอเทม [${newItem.displayName}] เข้ากระเป๋า!"
    }

    fun adminUnlockAllPets() {
        val allPets = repository.getAllMasterPets()
        val updated = _heroState.value.copy(
            unlockedPets = allPets,
            activePet = allPets.lastOrNull() ?: _heroState.value.activePet
        )
        _heroState.value = updated
        repository.saveHeroState(updated)
        _adminLogMsg.value = "🐉 เสกสัตว์เลี้ยงระดับตำนานทั้งหมดเรียบร้อย!"
    }

    fun adminUnlockAllFloors() {
        val updated = _heroState.value.copy(maxFloorUnlocked = 10)
        _heroState.value = updated
        repository.saveHeroState(updated)
        _selectedFloor.value = 10
        _adminLogMsg.value = "🏰 ปลดล็อกดันเจี้ยนทุกชั้น (ชั้น 1 - 10) สำเร็จ!"
    }

    fun adminGodModeGear() {
        val godWeapon = Item(
            name = "ดาบเทพมังกรโบราณ (God Dragon Blade)",
            type = ItemType.WEAPON,
            rarity = ItemRarity.LEGENDARY,
            statBonus = 500,
            description = "ดาบระดับพระเจ้าเสกจาก Admin Console",
            price = 99999,
            enhancementLevel = 10,
            iconSymbol = "👑⚔️"
        )
        val godArmor = Item(
            name = "เกราะอมตะ (Immortal God Armor)",
            type = ItemType.ARMOR,
            rarity = ItemRarity.LEGENDARY,
            statBonus = 400,
            description = "เกราะอมตะป้องกันไร้เทียมทาน เสกโดย Admin",
            price = 99999,
            enhancementLevel = 10,
            iconSymbol = "👑🛡️"
        )
        val updated = _heroState.value.copy(
            equippedWeapon = godWeapon,
            equippedArmor = godArmor,
            inventory = _heroState.value.inventory + godWeapon + godArmor
        )
        _heroState.value = updated
        repository.saveHeroState(updated)
        _adminLogMsg.value = "🔥 ติดตั้งชุดเกียร์ God Mode +10 ครบชุด!"
    }
}
