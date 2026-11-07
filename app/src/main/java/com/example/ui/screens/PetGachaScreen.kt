package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeroState
import com.example.model.Pet
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetGachaScreen(
    heroState: HeroState,
    masterPets: List<Pet>,
    gachaResult: Pet?,
    onPullGacha: () -> Unit,
    onEquipPet: (Pet) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gachaResult) {
        if (gachaResult != null) {
            showResultDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("⬅️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🐾 สัตว์เลี้ยง & ศาลากาชา (Pets)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                CurrencyChip(symbol = "💎", amount = heroState.gems, color = MpBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceCard,
                contentColor = GoldPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🐾 รังน้องสัตว์เลี้ยง", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🔮 กาชาสุ่มสัตว์เลี้ยง", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // PET SANCTUARY TAB
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(masterPets) { pet ->
                        val isUnlocked = heroState.unlockedPets.any { it.id == pet.id }
                        val isEquipped = heroState.activePet?.id == pet.id

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) SurfaceCard else SurfaceDark
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isEquipped) 2.dp else 1.dp,
                                color = if (isEquipped) GoldPrimary else Color(pet.rarity.colorHex).copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUnlocked) pet.iconSymbol else "❓",
                                    fontSize = 32.sp,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark)
                                        .wrapContentSize(Alignment.Center)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = pet.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Color(pet.rarity.colorHex) else TextMuted
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(pet.rarity.colorHex).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = pet.rarity.labelTh,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    color = Color(pet.rarity.colorHex)
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (isUnlocked) pet.passiveDesc else "ยังไม่ได้ครอบครอง สุ่มได้ที่ศาลากาชา",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                if (isUnlocked) {
                                    Button(
                                        onClick = { onEquipPet(pet) },
                                        enabled = !isEquipped,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEquipped) ExpGreen else GoldPrimary,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (isEquipped) "ติดตั้งอยู่" else "เลือกพาไป", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // GACHA SHRINE TAB
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔮 ศาลากาชาอธิษฐานสัตว์เลี้ยง",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EpicPurple
                        )
                    )
                    Text(
                        text = "อัญเชิญสหายสู้รบระดับสวรรค์ (โอกาสติดระดับตำนาน 5%)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "🌌✨🔮✨🌌", fontSize = 64.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onPullGacha,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp)
                            .testTag("pull_gacha_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EpicPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "🔮 สุ่มอัญเชิญ (10 Gems / 250 Gold)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Gacha Result Reveal Dialog
        if (showResultDialog && gachaResult != null) {
            AlertDialog(
                onDismissRequest = { showResultDialog = false },
                title = { Text("🎉 อัญเชิญสำเร็จ!", color = GoldPrimary, fontSize = 20.sp) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(gachaResult.iconSymbol, fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = gachaResult.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(gachaResult.rarity.colorHex)
                            )
                        )
                        Surface(
                            color = Color(gachaResult.rarity.colorHex).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "ระดับ ${gachaResult.rarity.labelTh}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(gachaResult.rarity.colorHex),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = gachaResult.passiveDesc,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResultDialog = false
                            onEquipPet(gachaResult)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text("พาออกต่อสู้เลย!", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}
