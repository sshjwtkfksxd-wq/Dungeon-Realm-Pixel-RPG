package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.HeroState
import com.example.ui.theme.*
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownHubScreen(
    heroState: HeroState,
    selectedFloor: Int,
    onFloorSelected: (Int) -> Unit,
    onEnterDungeon: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onResetGame: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Status Header Bar
            HeroHeaderCard(heroState = heroState, onNavigate = onNavigate)

            Spacer(modifier = Modifier.height(16.dp))

            // Dungeon Hero Banner Illustration
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary.copy(alpha = 0.6f)),
                shadowElevation = 8.dp
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.dungeon_banner_1794047373313),
                        contentDescription = "Dungeon Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🏰 เมืองศูนย์กลาง (Town Center)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        )
                        Text(
                            text = "ปลดล็อกดันเจี้ยนสูงสุด: ชั้นที่ ${heroState.maxFloorUnlocked}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Floor Selection & Dungeon Battle Launch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚔️ เลือกระดับดันเจี้ยน",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        )
                        Text(
                            text = "ชั้นที่ $selectedFloor",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ExpGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Floor Slider Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..heroState.maxFloorUnlocked.coerceAtLeast(1)).take(6).forEach { floor ->
                            val isSelected = floor == selectedFloor
                            Button(
                                onClick = { onFloorSelected(floor) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) GoldPrimary else SurfaceCardLight,
                                    contentColor = if (isSelected) Color.Black else TextPrimary
                                ),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "F$floor",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onEnterDungeon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("nav_dungeon_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HpRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "⚔️ ลุยดันเจี้ยนชั้นที่ $selectedFloor (Enter Battle)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "เมนูระบบและกิจกรรม (Game Features)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Features Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Admin Special Feature Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.ADMIN_PANEL) }
                        .testTag("nav_admin_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 32.sp,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldPrimary.copy(alpha = 0.2f))
                                .wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Admin Console (ระบบแอดมินพิเศษ)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                            Text(
                                text = "ใส่รหัส Admin เพื่อเสกเงิน เสกไอเทม และปรับเลเวล",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                        Text(
                            text = "🔐 รหัส: Admin",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureGridCard(
                        title = "สแตท & สกิล",
                        subtitle = if (heroState.unallocatedStats > 0) "⚠️ มี ${heroState.unallocatedStats} พอยท์!" else "อัปเกรดตัวละคร",
                        icon = "📊",
                        color = GoldPrimary,
                        modifier = Modifier.weight(1f).testTag("nav_stats_button"),
                        onClick = { onNavigate(Screen.HERO_STATS) }
                    )
                    FeatureGridCard(
                        title = "ตีเหล็ก & ร้านค้า",
                        subtitle = "ซื้อขาย/อัปเกรดเกียร์",
                        icon = "⚒️",
                        color = MpBlue,
                        modifier = Modifier.weight(1f).testTag("nav_shop_button"),
                        onClick = { onNavigate(Screen.BLACKSMITH) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureGridCard(
                        title = "สัตว์เลี้ยงผู้ช่วย",
                        subtitle = heroState.activePet?.name ?: "ไม่มีสัตว์เลี้ยง",
                        icon = heroState.activePet?.iconSymbol ?: "🐾",
                        color = ExpGreen,
                        modifier = Modifier.weight(1f).testTag("nav_pet_button"),
                        onClick = { onNavigate(Screen.PET_GACHA) }
                    )
                    FeatureGridCard(
                        title = "ศาลากาชาสุ่มโชค",
                        subtitle = "สุ่มสัตว์เลี้ยง/เกียร์",
                        icon = "🔮",
                        color = EpicPurple,
                        modifier = Modifier.weight(1f).testTag("nav_gacha_button"),
                        onClick = { onNavigate(Screen.PET_GACHA) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureGridCard(
                        title = "กระเป๋าสัมภาระ",
                        subtitle = "${heroState.inventory.size} ไอเทม",
                        icon = "🎒",
                        color = GoldSecondary,
                        modifier = Modifier.weight(1f).testTag("nav_inventory_button"),
                        onClick = { onNavigate(Screen.INVENTORY) }
                    )
                    FeatureGridCard(
                        title = "รีเซ็ตเกม",
                        subtitle = "เริ่มใหม่ทั้งหมด",
                        icon = "🔄",
                        color = TextMuted,
                        modifier = Modifier.weight(1f).testTag("reset_game_button"),
                        onClick = { showResetDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("⚠️ ยืนยันการรีเซ็ตเกม", color = GoldPrimary) },
                text = { Text("การรีเซ็ตจะลบข้อมูลผู้กล้า เลเวล ไอเทม และสัตว์เลี้ยงทั้งหมด คุณแน่ใจหรือไม่?", color = TextPrimary) },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            onResetGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HpRed)
                    ) {
                        Text("รีเซ็ตตัวละคร", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("ยกเลิก", color = TextSecondary)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
fun HeroHeaderCard(heroState: HeroState, onNavigate: (Screen) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Class Avatar Icon
                Text(
                    text = heroState.classType.iconSymbol,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.5.dp, GoldPrimary, CircleShape)
                        .wrapContentSize(Alignment.Center)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = heroState.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Lv.${heroState.level}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = heroState.classType.titleTh,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Currency Badges (Gold & Gems)
                Column(horizontalAlignment = Alignment.End) {
                    CurrencyChip(symbol = "🪙", amount = heroState.gold, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    CurrencyChip(symbol = "💎", amount = heroState.gems, color = MpBlue)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bars: HP, MP, EXP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgressBarCard(
                    label = "HP ${heroState.currentHp}/${heroState.maxHp}",
                    progress = (heroState.currentHp.toFloat() / heroState.maxHp.coerceAtLeast(1)).coerceIn(0f, 1f),
                    color = HpRed,
                    modifier = Modifier.weight(1f)
                )
                ProgressBarCard(
                    label = "MP ${heroState.currentMp}/${heroState.maxMp}",
                    progress = (heroState.currentMp.toFloat() / heroState.maxMp.coerceAtLeast(1)).coerceIn(0f, 1f),
                    color = MpBlue,
                    modifier = Modifier.weight(1f)
                )
                ProgressBarCard(
                    label = "EXP ${heroState.exp}/${heroState.maxExp}",
                    progress = (heroState.exp.toFloat() / heroState.maxExp.coerceAtLeast(1)).coerceIn(0f, 1f),
                    color = ExpGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CurrencyChip(symbol: String, amount: Int, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = symbol, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$amount",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}

@Composable
fun ProgressBarCard(label: String, progress: Float, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = TextSecondary
            )
        )
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun FeatureGridCard(
    title: String,
    subtitle: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 28.sp,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f))
                    .wrapContentSize(Alignment.Center)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = color
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
