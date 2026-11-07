package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroStatsSkillScreen(
    heroState: HeroState,
    onAllocateStat: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("⬅️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📊 สแตท & ทักษะผู้กล้า (Hero Stats)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
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
                    text = { Text("สแตท (Attributes)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("ผังทักษะ (Skill Tree)", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // STAT ALLOCATION TAB
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Summary Combat Stats Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "⚔️ พลังการต่อสู้รวม (Combat Overview)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatSummaryRow("พลังโจมตี (Attack)", "${heroState.attackPower}", GoldPrimary)
                                StatSummaryRow("พลังป้องกัน (Defense)", "${heroState.defensePower}", MpBlue)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatSummaryRow("พลังชีวิตสูงสุด (Max HP)", "${heroState.maxHp}", HpRed)
                                StatSummaryRow("พลังมานาสูงสุด (Max MP)", "${heroState.maxMp}", ExpGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Unallocated Stat Points Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (heroState.unallocatedStats > 0) GoldPrimary.copy(alpha = 0.2f) else SurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (heroState.unallocatedStats > 0) GoldPrimary else SurfaceCardLight
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "พอยท์คงเหลือ (Available Points):",
                                style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary)
                            )
                            Text(
                                text = "${heroState.unallocatedStats} Points",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Attribute Allocation Buttons
                    StatAllocationRow(
                        title = "STR (ความแข็งแกร่ง)",
                        value = heroState.str,
                        desc = "เพิ่มพลังโจมตีกายภาพและพลังชีวิตเล็กน้อย",
                        color = GoldPrimary,
                        canAdd = heroState.unallocatedStats > 0,
                        testTag = "stat_str_button",
                        onAdd = { onAllocateStat("STR") }
                    )

                    StatAllocationRow(
                        title = "INT (สติปัญญา)",
                        value = heroState.int,
                        desc = "เพิ่มพลังเวทมนตร์และพลังมานา (Max MP)",
                        color = EpicPurple,
                        canAdd = heroState.unallocatedStats > 0,
                        testTag = "stat_int_button",
                        onAdd = { onAllocateStat("INT") }
                    )

                    StatAllocationRow(
                        title = "DEX (ความว่องไว)",
                        value = heroState.dex,
                        desc = "เพิ่มอัตราคริติคอล พลังโจมตีธนู และการหลบหลีก",
                        color = ExpGreen,
                        canAdd = heroState.unallocatedStats > 0,
                        testTag = "stat_dex_button",
                        onAdd = { onAllocateStat("DEX") }
                    )

                    StatAllocationRow(
                        title = "VIT (ความทนทาน)",
                        value = heroState.vit,
                        desc = "เพิ่ม HP สูงสุด (Max HP) และเพิ่มพลังป้องกัน",
                        color = HpRed,
                        canAdd = heroState.unallocatedStats > 0,
                        testTag = "stat_vit_button",
                        onAdd = { onAllocateStat("VIT") }
                    )
                }
            } else {
                // SKILL TREE TAB
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ทักษะประจำสายอาชีพ ${heroState.classType.titleTh}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    heroState.unlockedSkills.forEach { skill ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MpBlue.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = skill.iconSymbol,
                                    fontSize = 32.sp,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark)
                                        .wrapContentSize(Alignment.Center)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = skill.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GoldPrimary
                                            )
                                        )
                                        Text(
                                            text = "${skill.mpCost} MP",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MpBlue
                                            )
                                        )
                                    }

                                    Text(
                                        text = skill.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatSummaryRow(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color))
    }
}

@Composable
fun StatAllocationRow(
    title: String,
    value: Int,
    desc: String,
    color: Color,
    canAdd: Boolean,
    testTag: String,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = onAdd,
                enabled = canAdd,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (canAdd) color else SurfaceDark)
                    .testTag(testTag)
            ) {
                Text("+", color = if (canAdd) Color.Black else TextMuted, fontWeight = FontWeight.Bold)
            }
        }
    }
}
