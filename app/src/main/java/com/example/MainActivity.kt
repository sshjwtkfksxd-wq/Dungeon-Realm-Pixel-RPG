package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repository.GameRepository
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DungeonRealmTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DungeonRealmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val viewModel: GameViewModel = viewModel()
                    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                    val heroState by viewModel.heroState.collectAsStateWithLifecycle()
                    val selectedFloor by viewModel.selectedFloor.collectAsStateWithLifecycle()
                    val currentMonster by viewModel.currentMonster.collectAsStateWithLifecycle()
                    val battleTurn by viewModel.battleTurn.collectAsStateWithLifecycle()
                    val battleLogs by viewModel.battleLogs.collectAsStateWithLifecycle()
                    val damagePopups by viewModel.damagePopups.collectAsStateWithLifecycle()
                    val forgeMessage by viewModel.forgeMessage.collectAsStateWithLifecycle()
                    val gachaResult by viewModel.gachaResult.collectAsStateWithLifecycle()
                    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
                    val adminLogMsg by viewModel.adminLogMsg.collectAsStateWithLifecycle()

                    val repository = remember { GameRepository(applicationContext) }

                    // Navigation BackHandler logic
                    if (currentScreen != Screen.CREATE_HERO && currentScreen != Screen.TOWN_HUB) {
                        BackHandler {
                            if (currentScreen == Screen.BATTLE) {
                                viewModel.escapeDungeon()
                            } else {
                                viewModel.navigateTo(Screen.TOWN_HUB)
                            }
                        }
                    }

                    when (currentScreen) {
                        Screen.CREATE_HERO -> {
                            CharacterCreationScreen(
                                onCharacterCreated = { name, classType ->
                                    viewModel.createCharacter(name, classType)
                                }
                            )
                        }
                        Screen.TOWN_HUB -> {
                            TownHubScreen(
                                heroState = heroState,
                                selectedFloor = selectedFloor,
                                onFloorSelected = { viewModel.selectDungeonFloor(it) },
                                onEnterDungeon = { viewModel.startDungeonBattle() },
                                onNavigate = { viewModel.navigateTo(it) },
                                onResetGame = { viewModel.resetGameProgress() }
                            )
                        }
                        Screen.BATTLE -> {
                            BattleScreen(
                                heroState = heroState,
                                monster = currentMonster,
                                selectedFloor = selectedFloor,
                                battleTurn = battleTurn,
                                battleLogs = battleLogs,
                                damagePopups = damagePopups,
                                onAttack = { viewModel.performPlayerAttack() },
                                onCastSkill = { skill -> viewModel.castSkill(skill) },
                                onUseItem = { potion -> viewModel.useBattlePotion(potion) },
                                onEscape = { viewModel.escapeDungeon() },
                                onNextFloor = { viewModel.startDungeonBattle() }
                            )
                        }
                        Screen.HERO_STATS -> {
                            HeroStatsSkillScreen(
                                heroState = heroState,
                                onAllocateStat = { stat -> viewModel.allocateStat(stat) },
                                onBack = { viewModel.navigateTo(Screen.TOWN_HUB) }
                            )
                        }
                        Screen.BLACKSMITH -> {
                            BlacksmithShopScreen(
                                heroState = heroState,
                                shopCatalog = repository.getShopItems(),
                                forgeMessage = forgeMessage,
                                onBuyItem = { item -> viewModel.buyItem(item) },
                                onUpgradeItem = { item -> viewModel.upgradeEquipment(item) },
                                onEquipItem = { item -> viewModel.equipItem(item) },
                                onBack = { viewModel.navigateTo(Screen.TOWN_HUB) }
                            )
                        }
                        Screen.PET_GACHA -> {
                            PetGachaScreen(
                                heroState = heroState,
                                masterPets = repository.getAllMasterPets(),
                                gachaResult = gachaResult,
                                onPullGacha = { viewModel.pullPetGacha() },
                                onEquipPet = { pet -> viewModel.equipPet(pet) },
                                onBack = { viewModel.navigateTo(Screen.TOWN_HUB) }
                            )
                        }
                        Screen.INVENTORY -> {
                            InventoryScreen(
                                heroState = heroState,
                                onEquipItem = { item -> viewModel.equipItem(item) },
                                onUseItem = { potion -> viewModel.useBattlePotion(potion) },
                                onSellItem = { item -> viewModel.sellItem(item) },
                                onBack = { viewModel.navigateTo(Screen.TOWN_HUB) }
                            )
                        }
                        Screen.ADMIN_PANEL -> {
                            AdminPanelScreen(
                                heroState = heroState,
                                isAdminUnlocked = isAdminUnlocked,
                                adminLogMsg = adminLogMsg,
                                catalogItems = repository.getShopItems(),
                                onUnlockAdmin = { pass -> viewModel.unlockAdminMode(pass) },
                                onLockAdmin = { viewModel.lockAdminMode() },
                                onAddGold = { amount -> viewModel.adminAddGold(amount) },
                                onAddGems = { amount -> viewModel.adminAddGems(amount) },
                                onSetLevel = { level -> viewModel.adminSetLevel(level) },
                                onGiveItem = { item -> viewModel.adminGiveItem(item) },
                                onUnlockPets = { viewModel.adminUnlockAllPets() },
                                onUnlockFloors = { viewModel.adminUnlockAllFloors() },
                                onGodModeGear = { viewModel.adminGodModeGear() },
                                onBack = { viewModel.navigateTo(Screen.TOWN_HUB) }
                            )
                        }
                    }
                }
            }
        }
    }
}
