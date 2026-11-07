package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    heroState: HeroState,
    monster: Monster?,
    selectedFloor: Int,
    battleTurn: BattleTurn,
    battleLogs: List<BattleLog>,
    damagePopups: List<DamagePopup>,
    onAttack: () -> Unit,
    onCastSkill: (Skill) -> Unit,
    onUseItem: (Item) -> Unit,
    onEscape: () -> Unit,
    onNextFloor: () -> Unit
) {
    var showSkillSheet by remember { mutableStateOf(false) }
    var showItemSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), DarkBackground, SurfaceDark)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Stage Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = GoldPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                ) {
                    Text(
                        text = "⚔️ ดันเจี้ยนชั้นที่ $selectedFloor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                OutlinedButton(
                    onClick = onEscape,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.testTag("battle_run_button")
                ) {
                    Text("🏃 หนี (Run)", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Monster Card & Sprite Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
            ) {
                if (monster != null) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (monster.isBoss) 2.dp else 1.dp,
                            color = if (monster.isBoss) HpRed else SurfaceCardLight
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (monster.isBoss) {
                                Surface(
                                    color = HpRed.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = "⚠️ FLOOR BOSS ⚠️",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = HpRed
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Monster Animated Sprite
                            val isMonsterHit = damagePopups.any { it.isEnemy }
                            val scaleAnim by animateFloatAsState(
                                targetValue = if (isMonsterHit) 1.15f else 1.0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
                            )

                            Text(
                                text = monster.iconSymbol,
                                fontSize = if (monster.isBoss) 72.sp else 56.sp,
                                modifier = Modifier
                                    .scale(scaleAnim)
                                    .padding(8.dp)
                            )

                            Text(
                                text = monster.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Monster Health Bar
                            val monsterHpRatio = (monster.currentHp.toFloat() / monster.maxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
                            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("HP", style = MaterialTheme.typography.labelSmall.copy(color = HpRed))
                                    Text("${monster.currentHp} / ${monster.maxHp}", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { monsterHpRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    color = HpRed,
                                    trackColor = HpRed.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }

                    // Floating Damage Popups Layer
                    Box(modifier = Modifier.fillMaxSize()) {
                        damagePopups.forEach { popup ->
                            AnimatedDamagePopupView(popup = popup)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Battle Log Console
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardLight)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    reverseLayout = false
                ) {
                    items(battleLogs, key = { it.id }) { log ->
                        val textColor = when {
                            log.isCritical -> GoldPrimary
                            log.isHeal -> ExpGreen
                            log.isMonster -> HpRed
                            else -> TextPrimary
                        }
                        Text(
                            text = log.message,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = textColor,
                                fontWeight = if (log.isCritical) FontWeight.Bold else FontWeight.Normal
                            ),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hero State Card & HP/MP Bars
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = heroState.classType.iconSymbol,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                                .wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${heroState.name} (Lv.${heroState.level})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            heroState.activePet?.let { pet ->
                                Text(
                                    text = "ผู้ช่วย: ${pet.iconSymbol} ${pet.name}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = ExpGreen
                                    )
                                )
                            }
                        }

                        // Turn Badge Indicator
                        val turnLabel = if (battleTurn == BattleTurn.PLAYER) "ตาของคุณ" else "ศัตรูกำลังโจมตี"
                        val turnColor = if (battleTurn == BattleTurn.PLAYER) GoldPrimary else HpRed
                        Surface(
                            color = turnColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = turnLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = turnColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Command Deck Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAttack,
                    enabled = battleTurn == BattleTurn.PLAYER,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("battle_attack_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("⚔️ โจมตี", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showSkillSheet = true },
                    enabled = battleTurn == BattleTurn.PLAYER,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("battle_skill_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MpBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("✨ สกิล", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showItemSheet = true },
                    enabled = battleTurn == BattleTurn.PLAYER,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("battle_item_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("🧪 ไอเทม", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Skill Selection Modal
        if (showSkillSheet) {
            AlertDialog(
                onDismissRequest = { showSkillSheet = false },
                title = { Text("✨ เลือกทักษะสกิล (Cast Skill)", color = GoldPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        heroState.unlockedSkills.forEach { skill ->
                            val canAfford = heroState.currentMp >= skill.mpCost
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = canAfford) {
                                        showSkillSheet = false
                                        onCastSkill(skill)
                                    },
                                color = if (canAfford) SurfaceCardLight else SurfaceDark,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (canAfford) MpBlue else TextMuted
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(skill.iconSymbol, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = skill.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = if (canAfford) TextPrimary else TextMuted,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = skill.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${skill.mpCost} MP",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (canAfford) MpBlue else HpRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSkillSheet = false }) {
                        Text("ปิด", color = TextSecondary)
                    }
                },
                containerColor = SurfaceCard
            )
        }

        // Item Selection Modal
        if (showItemSheet) {
            val potions = heroState.inventory.filter { it.type == ItemType.POTION_HP || it.type == ItemType.POTION_MP }
            AlertDialog(
                onDismissRequest = { showItemSheet = false },
                title = { Text("🧪 เลือกใช้ยาโพชั่น (Use Item)", color = ExpGreen) },
                text = {
                    if (potions.isEmpty()) {
                        Text("ไม่มียาโพชั่นในกระเป๋า สามารถซื้อเพิ่มได้ที่ร้านค้า!", color = TextSecondary)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            potions.forEach { potion ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showItemSheet = false
                                            onUseItem(potion)
                                        },
                                    color = SurfaceCardLight,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ExpGreen)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(potion.iconSymbol, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = potion.name,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = potion.description,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = "ใช้",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = ExpGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showItemSheet = false }) {
                        Text("ปิด", color = TextSecondary)
                    }
                },
                containerColor = SurfaceCard
            )
        }

        // Victory Dialog
        if (battleTurn == BattleTurn.VICTORY) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("🏆 ชัยชนะเหนือดันเจี้ยน!", color = GoldPrimary, fontSize = 20.sp) },
                text = {
                    Text(
                        text = "คุณปราบมอนสเตอร์ประจำดันเจี้ยนชั้นที่ $selectedFloor สำเร็จ!\n\nได้รับ EXP และ Gold รางวัล พร้อมปลดล็อกชั้นถัดไป",
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onNextFloor,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text("ลุยชั้นถัดไป / กลับเมือง", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = SurfaceCard
            )
        }

        // Defeat Dialog
        if (battleTurn == BattleTurn.DEFEAT) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("💀 คุณพ่ายแพ้ในดันเจี้ยน", color = HpRed, fontSize = 20.sp) },
                text = { Text("พลังชีวิตของคุณหมดลงในการต่อสู้ ร่างของคุณถูกส่งกลับมาพักฟื้นที่เมืองศูนย์กลาง", color = TextPrimary) },
                confirmButton = {
                    Button(
                        onClick = onEscape,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardLight)
                    ) {
                        Text("กลับสู่เมือง", color = TextPrimary)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
fun AnimatedDamagePopupView(popup: DamagePopup) {
    val alphaAnim by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = tween(durationMillis = 300)
    )

    val color = when {
        popup.isHeal -> ExpGreen
        popup.isCritical -> GoldPrimary
        popup.isEnemy -> HpRed
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        contentAlignment = if (popup.isEnemy) Alignment.TopCenter else Alignment.Center
    ) {
        Text(
            text = popup.text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (popup.isCritical) 32.sp else 24.sp,
                color = color
            )
        )
    }
}
