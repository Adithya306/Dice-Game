package com.example.dicegame

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame.ui.theme.DiceGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameCW1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onNewGameClick = { targetScore ->
                            startGameWithTarget(targetScore)
                        },
                        onAboutClick = { showAboutDialog() }
                    )
                }
            }
        }
    }

    private fun startGameWithTarget(targetScore: Int){
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("TARGET_SCORE", targetScore)
        }
        startActivity(intent)
    }

    private fun showAboutDialog(){
        val context = this
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.apply {
            setTitle("About")
            setMessage("Student ID: w2053188/20230900\n" +
                    "Student Name: Garuka Adithya \n\n" +
                    "I confirm that I understand what plagiarism is and have read and\n" +
                    "understood the section on Assessment Offences in the Essential\n" +
                    "Information for Students. The work that I have submitted is\n" +
                    "entirely my own. Any work from other authors is duly referenced\n" +
                    "and acknowledged.")
            setPositiveButton("OK"){dialog,_->dialog.dismiss()}
            create().show()
        }
    }
}

@Composable
fun MainScreen(onNewGameClick: (Int) -> Unit, onAboutClick: () -> Unit) {
    var showTargetDialog by remember { mutableStateOf(false) }
    var targetScore by remember { mutableStateOf("101") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Dice Game",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        Button(
            onClick = { showTargetDialog = true },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("New Game", fontSize = 18.sp)
        }

        Button(
            onClick = onAboutClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "About", fontSize = 18.sp)
        }
    }

    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Set Target Score") },
            text = {
                Column {
                    Text("Enter the target score to win (default: 101)")
                    TextField(
                        value = targetScore,
                        onValueChange = { input ->
                            // Only accept numeric input
                            if (input.isEmpty() || input.all { it.isDigit() }) {
                                targetScore = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val target = targetScore.toIntOrNull() ?: 101
                    onNewGameClick(target.coerceAtLeast(1)) // Ensure positive value
                    showTargetDialog = false
                }) {
                    Text("Start Game")
                }
            },
            dismissButton = {
                Button(onClick = { showTargetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}