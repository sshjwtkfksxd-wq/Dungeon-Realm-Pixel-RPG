package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClassType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (name: String, classType: ClassType) -> Unit
) {
    var heroName by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf(ClassType.WARRIOR) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, SurfaceDark, Color(0xFF0F172A))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title Banner
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚔️ DUNGEON REALM ⚔️",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "ตำนานผู้กล้าดันเจี้ยน - สร้างตัวละครใหม่",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Name Input
            OutlinedTextField(
                value = heroName,
                onValueChange = { heroName = it },
                label = { Text("ชื่อผู้กล้า (Hero Name)", color = GoldPrimary) },
                placeholder = { Text("เช่น อัศวินโตโน่", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = SurfaceCardLight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    cursorColor = GoldPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_name_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "เลือกอาชีพประจำตัว (Choose Class)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Class Selection Cards
            ClassType.values().forEach { classType ->
                val isSelected = selectedClass == classType
                val borderColor = if (isSelected) GoldPrimary else SurfaceCardLight
                val bgColor = if (isSelected) SurfaceCardLight else SurfaceCard

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedClass = classType }
                        .testTag("class_card_${classType.name.lowercase()}"),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = classType.iconSymbol,
                            fontSize = 36.sp,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                                .wrapContentSize(Alignment.Center)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = classType.titleTh,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GoldPrimary else TextPrimary
                                )
                            )
                            Text(
                                text = classType.descriptionTh,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            // Initial Stat Badges
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatBadge("HP: ${classType.baseHp}", HpRed)
                                StatBadge("MP: ${classType.baseMp}", MpBlue)
                                StatBadge("STR: ${classType.baseStr}", GoldPrimary)
                                StatBadge("INT: ${classType.baseInt}", EpicPurple)
                                StatBadge("DEX: ${classType.baseDex}", ExpGreen)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = { onCharacterCreated(heroName, selectedClass) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("create_hero_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "เริ่มต้นการผจญภัย (Start Adventure) ⚔️",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
