package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun InventoryScreen(
    heroState: HeroState,
    onEquipItem: (Item) -> Unit,
    onUseItem: (Item) -> Unit,
    onSellItem: (Item) -> Unit,
    onBack: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }

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
                    text = "🎒 กระเป๋าสัมภาระ (Inventory)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                CurrencyChip(symbol = "🪙", amount = heroState.gold, color = GoldPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Equipped Gear Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚔️ อุปกรณ์ที่ติดตั้งอยู่ปัจจุบัน",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EquippedGearSlot(
                            title = "อาวุธ (Weapon)",
                            item = heroState.equippedWeapon,
                            modifier = Modifier.weight(1f)
                        )
                        EquippedGearSlot(
                            title = "เกราะ (Armor)",
                            item = heroState.equippedArmor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ช่องเก็บไอเทมทั้งหมด (${heroState.inventory.size} ชิ้น)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Inventory Grid View
            if (heroState.inventory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ไม่มีไอเทมในกระเป๋า!", color = TextMuted)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(heroState.inventory) { item ->
                        InventoryItemCard(item = item, onClick = { selectedItem = item })
                    }
                }
            }
        }

        // Item Detail Sheet Dialog
        selectedItem?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                title = {
                    Text(
                        text = item.displayName,
                        color = Color(item.rarity.colorHex),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text("ประเภท: ${item.type.name}", color = TextSecondary, fontSize = 12.sp)
                        Text(item.description, color = TextPrimary, modifier = Modifier.padding(vertical = 6.dp))
                        Text(
                            text = "มูลค่าขาย: ${item.price / 2} Gold",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (item.type == ItemType.WEAPON || item.type == ItemType.ARMOR) {
                            Button(
                                onClick = {
                                    onEquipItem(item)
                                    selectedItem = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                            ) {
                                Text("ติดตั้ง (Equip)", color = Color.Black)
                            }
                        } else if (item.type == ItemType.POTION_HP || item.type == ItemType.POTION_MP) {
                            Button(
                                onClick = {
                                    onUseItem(item)
                                    selectedItem = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ExpGreen)
                            ) {
                                Text("ใช้งาน (Use)", color = Color.White)
                            }
                        }

                        Button(
                            onClick = {
                                onSellItem(item)
                                selectedItem = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardLight)
                        ) {
                            Text("ขาย", color = HpRed)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedItem = null }) {
                        Text("ปิด", color = TextSecondary)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
fun EquippedGearSlot(title: String, item: Item?, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = SurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item != null) Color(item.rarity.colorHex) else SurfaceCardLight
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
            Spacer(modifier = Modifier.height(4.dp))
            if (item != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.iconSymbol, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            item.displayName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(item.rarity.colorHex)
                            )
                        )
                        Text(
                            "+${item.currentBonus} โบนัส",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary)
                        )
                    }
                }
            } else {
                Text("ไม่ได้ติดตั้ง", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: Item, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(item.rarity.colorHex))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(item.iconSymbol, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1
            )
        }
    }
}
