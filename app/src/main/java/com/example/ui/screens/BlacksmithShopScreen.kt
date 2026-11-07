package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.Item
import com.example.model.ItemType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlacksmithShopScreen(
    heroState: HeroState,
    shopCatalog: List<Item>,
    forgeMessage: String?,
    onBuyItem: (Item) -> Unit,
    onUpgradeItem: (Item) -> Unit,
    onEquipItem: (Item) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedItemForForge by remember { mutableStateOf<Item?>(null) }

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
                    text = "⚒️ โรงตีเหล็ก & ร้านค้า (Blacksmith)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                CurrencyChip(symbol = "🪙", amount = heroState.gold, color = GoldPrimary)
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
                    text = { Text("🛒 ร้านค้า (Shop)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🔥 ตีบวกเกียร์ (Forge)", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // System Message Banner
            forgeMessage?.let { msg ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = GoldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (selectedTab == 0) {
                // SHOP CATALOG TAB
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(shopCatalog) { item ->
                        val canAfford = heroState.gold >= item.price
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(item.rarity.colorHex).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.iconSymbol,
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark)
                                        .wrapContentSize(Alignment.Center)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(item.rarity.colorHex)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(item.rarity.colorHex).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = item.rarity.labelTh,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    color = Color(item.rarity.colorHex)
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { onBuyItem(item) },
                                    enabled = canAfford,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text("${item.price} G", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // FORGE ENHANCEMENT TAB
                val upgradableItems = (heroState.inventory + listOfNotNull(heroState.equippedWeapon, heroState.equippedArmor))
                    .filter { it.type == ItemType.WEAPON || it.type == ItemType.ARMOR }
                    .distinctBy { it.id }

                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "เลือกไอเทมที่จะนำมาตีบวกเพิ่มระดับพลัง (+1 ถึง +10)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (upgradableItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ไม่มีอาวุธหรือชุดเกราะในกระเป๋า!", color = TextMuted)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(upgradableItems) { item ->
                                val isSelected = selectedItemForForge?.id == item.id
                                val upgradeCost = 80 + (item.enhancementLevel * 60)
                                val successRate = (100 - (item.enhancementLevel * 10)).coerceAtLeast(30)
                                val canAfford = heroState.gold >= upgradeCost

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else SurfaceCardLight
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item.iconSymbol, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.displayName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldPrimary
                                                )
                                            )
                                            Text(
                                                text = "โบนัสสแตทปัจจุบัน: +${item.currentBonus} | โอกาสตีบวกสำเร็จ: $successRate%",
                                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp)
                                            )
                                        }

                                        Button(
                                            onClick = { onUpgradeItem(item) },
                                            enabled = canAfford,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = HpRed,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("ตีบวก $upgradeCost G", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
