package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeroState
import com.example.model.Item
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    heroState: HeroState,
    isAdminUnlocked: Boolean,
    adminLogMsg: String?,
    catalogItems: List<Item>,
    onUnlockAdmin: (String) -> Boolean,
    onLockAdmin: () -> Unit,
    onAddGold: (Int) -> Unit,
    onAddGems: (Int) -> Unit,
    onSetLevel: (Int) -> Unit,
    onGiveItem: (Item) -> Unit,
    onUnlockPets: () -> Unit,
    onUnlockFloors: () -> Unit,
    onGodModeGear: () -> Unit,
    onBack: () -> Unit
) {
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var customLevelInput by remember { mutableStateOf("99") }
    var customGoldInput by remember { mutableStateOf("100000") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1B4B), DarkBackground, SurfaceDark)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("⬅️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚙️ ADMIN COMMAND CONSOLE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                if (isAdminUnlocked) {
                    IconButton(onClick = onLockAdmin) {
                        Text("🔒", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Log Output Banner
            adminLogMsg?.let { msg ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = GoldPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!isAdminUnlocked) {
                // PASSWORD GATE UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔐", fontSize = 54.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "กรอกรหัสผ่านเพื่อเข้าใช้งาน Admin UI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "รหัสผ่านสำหรับการเข้าใช้งานคือ: Admin",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Admin Password", color = GoldPrimary) },
                                singleLine = true,
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    TextButton(onClick = { showPassword = !showPassword }) {
                                        Text(if (showPassword) "ซ่อน" else "แสดง", color = TextSecondary)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = SurfaceCardLight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = GoldPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_password_input")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { onUnlockAdmin(passwordInput) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("admin_password_unlock_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "🔓 ปลดล็อกสิทธิ์ Admin Console",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // UNLOCKED ADMIN GOD MODE CHEAT CONSOLE
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "⚡ Quick Cheat Actions (เสกความสามารถพิเศษ)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Quick Cheat Buttons Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminActionButton(
                            title = "🪙 เสก +100,000 Gold",
                            color = GoldPrimary,
                            modifier = Modifier.weight(1f).testTag("admin_gold_button"),
                            onClick = { onAddGold(100000) }
                        )
                        AdminActionButton(
                            title = "💎 เสก +10,000 Gems",
                            color = MpBlue,
                            modifier = Modifier.weight(1f).testTag("admin_gems_button"),
                            onClick = { onAddGems(10000) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminActionButton(
                            title = "⚡ เลเวล Max (Lv.99)",
                            color = ExpGreen,
                            modifier = Modifier.weight(1f).testTag("admin_level_99_button"),
                            onClick = { onSetLevel(99) }
                        )
                        AdminActionButton(
                            title = "🐉 สัตว์เลี้ยงตำนานครบ",
                            color = EpicPurple,
                            modifier = Modifier.weight(1f).testTag("admin_unlock_pets_button"),
                            onClick = onUnlockPets
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminActionButton(
                            title = "🏰 ปลดล็อกดันเจี้ยนทุกชั้น",
                            color = HpRed,
                            modifier = Modifier.weight(1f).testTag("admin_unlock_floors_button"),
                            onClick = onUnlockFloors
                        )
                        AdminActionButton(
                            title = "👑 ชุดเกียร์ God Mode +10",
                            color = LegendaryGold,
                            modifier = Modifier.weight(1f).testTag("admin_god_gear_button"),
                            onClick = onGodModeGear
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "🛠️ ปรับระดับค่าสแตทเฉพาะตัว (Custom Setter)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Custom Level Setter Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("กำหนดระดับเลเวลเฉพาะ (Set Custom Level)", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = customLevelInput,
                                    onValueChange = { customLevelInput = it },
                                    label = { Text("Level (1 - 99)", color = TextSecondary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        val lvl = customLevelInput.toIntOrNull() ?: 1
                                        onSetLevel(lvl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("ปรับเลเวล", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "🎁 เสกไอเทมและอุปกรณ์เข้ากระเป๋า (Item Spawner)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Item Spawner Catalog
                    catalogItems.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.iconSymbol, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(item.rarity.colorHex)
                                        )
                                    )
                                    Text(item.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp))
                                }
                                Button(
                                    onClick = { onGiveItem(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ExpGreen, contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("เสก 1 ชิ้น", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun AdminActionButton(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
