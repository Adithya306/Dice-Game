package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dicegamecw1.ui.theme.DiceGameCW1Theme
import kotlin.random.Random


//Computer Strategy for Dice Game


//1. Keep High Value Dice - Always keeps 5s and 6s to maximize points
//2. Save Great Rolls - Stops rolling if current sum is 21+ (better than average)
//3. Chase When Behind - Takes more risks when behind by rerolling more dice
//4. Play Safe When Ahead - Keeps more dice when in the lead
//5. Final Push Strategy - Makes calculated risks when close to winning
//6. Last Roll Caution - More conservative on the final reroll
//7. Winning Move Recognition - Recognizes and takes a winning roll immediately


class GameActivity : ComponentActivity() {

    companion object {
        var humanWinCount = 0
        var computerWinCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get target score from intent, default to 101
        val targetScore = intent.getIntExtra("TARGET_SCORE", 101)

        setContent {
            DiceGameCW1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    GameScreen(
                        targetScore = targetScore,
                        onReturnToMainMenu = { finish() }
                    )
                }
            }
        }
    }
}