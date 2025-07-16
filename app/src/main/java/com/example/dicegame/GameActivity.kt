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

@Composable
fun GameScreen(
    targetScore: Int = 101,
    onReturnToMainMenu: () -> Unit = {}
){
    var humanDice by rememberSaveable { mutableStateOf(listOf(1,1,1,1,1)) }
    var computerDice by rememberSaveable { mutableStateOf(listOf(1,1,1,1,1)) }
    var humanScore by rememberSaveable { mutableStateOf(0) }
    var computerScore by rememberSaveable { mutableStateOf(0) }
    var rollCount by rememberSaveable { mutableStateOf(0) }
    var turnEnded by rememberSaveable { mutableStateOf(false) }
    var computerRollCount by rememberSaveable { mutableStateOf(0) }
    var selectedDice by rememberSaveable { mutableStateOf(listOf(false,false,false,false,false)) }
    var gameOver by rememberSaveable { mutableStateOf(false) }
    var humanWins by rememberSaveable { mutableStateOf(false) }
    var humanAttempts by rememberSaveable { mutableStateOf(0) }
    var computerAttempts by rememberSaveable { mutableStateOf(0) }
    var inTieBreaker by rememberSaveable { mutableStateOf(false) }
    var tieBreakerHumanDice by rememberSaveable { mutableStateOf(listOf(1, 1, 1, 1, 1)) }
    var tieBreakerComputerDice by rememberSaveable { mutableStateOf(listOf(1, 1, 1, 1, 1)) }


    LaunchedEffect(humanScore, computerScore) {
        if (humanScore >= targetScore && computerScore >= targetScore) {
            // Check if they have the same number of attempts and same score
            if (humanAttempts == computerAttempts && humanScore == computerScore) {
                inTieBreaker = true
            } else {
                gameOver = true
                humanWins = humanScore > computerScore
            }
        } else if (humanScore >= targetScore) {
            gameOver = true
            humanWins = true
        } else if (computerScore >= targetScore) {
            gameOver = true
            humanWins = false
        }
    }


    LaunchedEffect(gameOver) {
        if (gameOver) {
            if (humanWins) {
                com.example.dicegamecw1.GameActivity.humanWinCount++
            } else {
                com.example.dicegamecw1.GameActivity.computerWinCount++
            }
        }
    }

    if (inTieBreaker) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Tie Breaker! Roll the dice once to determine the winner.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text("Your Dice")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tieBreakerHumanDice.forEach { value -> DiceImage(value) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Computer's Dice")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tieBreakerComputerDice.forEach { value -> DiceImage(value) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    tieBreakerHumanDice = List(5) { Random.nextInt(1, 7) }
                    tieBreakerComputerDice = List(5) { Random.nextInt(1, 7) }


                    val humanSum = tieBreakerHumanDice.sum()
                    val computerSum = tieBreakerComputerDice.sum()

                    if (humanSum != computerSum) {
                        inTieBreaker = false
                        gameOver = true
                        humanWins = humanSum > computerSum
                    }

                }
            ) {
                Text("Roll for Tie Breaker")
            }
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "H:${com.example.dicegamecw1.GameActivity.humanWinCount}/C:${com.example.dicegamecw1.GameActivity.computerWinCount}",
                    style = MaterialTheme.typography.titleMedium
                )


                Text(
                    text = "Score: You $humanScore - Computer $computerScore",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = "Your Dice(tap to select for keeping)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in humanDice.indices){
                    val isSelected = selectedDice[i]

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable(enabled = rollCount>0 && rollCount<3 && !turnEnded && !gameOver) {
                                val newSelection = selectedDice.toMutableList()
                                newSelection[i] = !isSelected
                                selectedDice = newSelection
                            }
                    ) {
                        DiceImage(humanDice[i])

                        if(isSelected){
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .border(2.dp, Color.Green, shape= RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Computer's Dice",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                computerDice.forEach { value -> DiceImage(value) }
            }

            Spacer(modifier = Modifier.height(48.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if(rollCount==0) {
                            humanDice = List(5) { Random.nextInt(1, 7) }
                            computerDice = List(5) { Random.nextInt(1, 7) }
                            selectedDice = List(5) {false}

                            computerRollCount=1

                        } else {
                            val newDice = humanDice.toMutableList()
                            for (i in newDice.indices){
                                if(!selectedDice[i]){
                                    newDice[i]=Random.nextInt(1,7)
                                }
                            }
                            humanDice=newDice

                            if (computerRollCount<3 && computerDecideReroll(computerDice, computerScore, humanScore, targetScore, computerRollCount)){
                                val keepDice = computerSelectDiceToKeep(computerDice, computerScore, humanScore, targetScore)
                                val newComputerDice = computerDice.toMutableList()

                                for(i in newComputerDice.indices){
                                    if(!keepDice[i]){
                                        newComputerDice[i] = Random.nextInt(1,7)
                                    }
                                }

                                computerDice = newComputerDice
                                computerRollCount++
                            }
                        }
                        rollCount++

                        if (rollCount==3){
                            val humanTurnScore = humanDice.sum()
                            humanScore += humanTurnScore

                            val computerFinalDice = completeComputerTurn(computerDice, computerRollCount, computerScore, humanScore, targetScore)
                            computerDice = computerFinalDice
                            val computerTurnScore = computerDice.sum()
                            computerScore+=computerTurnScore

                            turnEnded=true

                            humanAttempts++
                            computerAttempts++

                            if (humanScore >= targetScore || computerScore >= targetScore) {
                                if (humanScore >= targetScore && computerScore >= targetScore &&
                                    humanScore == computerScore && humanAttempts == computerAttempts) {
                                    inTieBreaker = true
                                } else {
                                    gameOver = true
                                    humanWins = when {
                                        humanScore >= targetScore && computerScore >= targetScore -> humanScore > computerScore
                                        humanScore >= targetScore -> true
                                        else -> false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !turnEnded && !gameOver && !inTieBreaker && rollCount<3
                )
                {
                    Text(if(rollCount ==0)"Throw" else "Reroll")
                }
                Button(
                    onClick = {
                        val humanTurnScore = humanDice.sum()
                        humanScore += humanTurnScore

                        val computerFinalDice = completeComputerTurn(computerDice, computerRollCount, computerScore, humanScore, targetScore)
                        computerDice = computerFinalDice
                        val computerTurnScore = computerFinalDice.sum()
                        computerScore += computerTurnScore

                        turnEnded=true

                        humanAttempts++
                        computerAttempts++

                        if (humanScore >= targetScore || computerScore >= targetScore) {
                            if (humanScore >= targetScore && computerScore >= targetScore &&
                                humanScore == computerScore && humanAttempts == computerAttempts) {
                                inTieBreaker = true
                            } else {
                                gameOver = true
                                humanWins = when {
                                    humanScore >= targetScore && computerScore >= targetScore -> humanScore > computerScore
                                    humanScore >= targetScore -> true
                                    else -> false
                                }
                            }
                        }
                    },
                    enabled = !turnEnded && !gameOver && !inTieBreaker && rollCount>0
                ) {
                    Text("Score")
                }
            }

            if(turnEnded && !gameOver && !inTieBreaker){
                Button(
                    onClick = {
                        rollCount=0
                        computerRollCount=0
                        turnEnded=false
                        selectedDice=List(5){false}
                    },
                    modifier = Modifier.padding(top=16.dp)
                ) {
                    Text("Next Turn")
                }
            }
        }
    }

    if (gameOver) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(text = if (humanWins) "You win!" else "You lose") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Final score: You $humanScore - Computer $computerScore",
                        color = if (humanWins) Color.Green else Color.Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Total wins: You ${com.example.dicegamecw1.GameActivity.humanWinCount} - Computer ${com.example.dicegamecw1.GameActivity.computerWinCount}",
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Thanks for playing!")
                }
            },
            confirmButton = {
                Button(onClick = { onReturnToMainMenu() }) {
                    Text("Back to Main Menu")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun DiceImage(value:Int){
    val imageRes = when (value){
        1-> R.drawable.dado1
        2-> R.drawable.dado2
        3-> R.drawable.dado3
        4-> R.drawable.dado4
        5-> R.drawable.dado5
        6-> R.drawable.dado6
        else -> R.drawable.dado1
    }

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Dice showing $value",
        modifier = Modifier.size(60.dp)
    )
}


private fun computerDecideReroll(
    currentDice: List<Int>,
    computerScore: Int,
    humanScore: Int,
    targetScore: Int,
    rollCount: Int
): Boolean {
    val currentSum = currentDice.sum()

    // Strategy 7: Winning Move Recognition - If this roll would win the game, take it
    if (currentSum + computerScore >= targetScore) return false

    // Strategy 2: Save Great Rolls - Don't risk it if we already have a great roll
    if (currentSum >= 21) return false  // 21+ is better than average (17.5)

    // Strategy 6: Last Roll Caution - Be more conservative on the final roll
    if (rollCount == 2) {
        return currentSum < 18  // Only reroll if below average on last roll
    }

    // Strategy 5: Final Push Strategy - Calculate risks when close to target
    val remainingToTarget = targetScore - computerScore
    if (remainingToTarget <= 25) {
        // If human is very close to winning, take more risks
        if (targetScore - humanScore <= 15) return true
    }

    // Strategy 3: Chase When Behind - Take more risks when behind
    if (computerScore < humanScore - 15) {
        return currentSum < 20  // Reroll unless we have a good score
    }

    // Strategy 4: Play Safe When Ahead - Be more conservative when leading
    if (computerScore > humanScore + 15) {
        return currentSum < 16  // Only reroll poor rolls when ahead
    }

    // Default case - reroll if below average
    return currentSum < 18
}

private fun computerSelectDiceToKeep(
    currentDice: List<Int>,
    computerScore: Int,
    humanScore: Int,
    targetScore: Int
): List<Boolean> {
    val keepDice = MutableList(5) { false }

    // Strategy 1: Keep High Value Dice - Always keep 5s and 6s
    for (i in currentDice.indices) {
        if (currentDice[i] >= 5) {
            keepDice[i] = true
        }
    }

    // Strategy 3: Chase When Behind - Take more risks when behind
    val isBehind = computerScore < humanScore - 15
    if (isBehind) {
        // Only keep 6s when far behind to maximize reroll potential
        for (i in currentDice.indices) {
            if (currentDice[i] < 6) {
                keepDice[i] = false
            }
        }
    }

    // Strategy 4: Play Safe When Ahead - Keep more dice when leading
    val isAhead = computerScore > humanScore + 15
    if (isAhead) {
        // Keep 4s, 5s, and 6s when ahead
        for (i in currentDice.indices) {
            if (currentDice[i] >= 4) {
                keepDice[i] = true
            }
        }
    }

    // Strategy 5: Final Push Strategy - Calculated risks when close to winning
    val remainingToTarget = targetScore - computerScore
    if (remainingToTarget <= 25) {
        // Keep 3s, 4s, 5s, and 6s when close to target
        for (i in currentDice.indices) {
            if (currentDice[i] >= 3) {
                keepDice[i] = true
            }
        }
    }

    // Always keep at least one die unless all dice are very poor
    if (keepDice.none { it } && currentDice.any { it >= 3 }) {
        val maxValue = currentDice.maxOrNull() ?: 1
        for (i in currentDice.indices) {
            if (currentDice[i] == maxValue) {
                keepDice[i] = true
                break
            }
        }
    }

    return keepDice
}

private fun completeComputerTurn(
    currentDice: List<Int>,
    currentRollCount: Int,
    computerScore: Int,
    humanScore: Int,
    targetScore: Int
): List<Int> {
    var dice = currentDice.toMutableList()
    var rollCount = currentRollCount

    while (rollCount < 3) {
        val shouldReroll = computerDecideReroll(
            dice,
            computerScore,
            humanScore,
            targetScore,
            rollCount
        )

        if (!shouldReroll) break

        val keepDice = computerSelectDiceToKeep(
            dice,
            computerScore,
            humanScore,
            targetScore
        )

        for (i in dice.indices) {
            if (!keepDice[i]) {
                dice[i] = Random.nextInt(1, 7)
            }
        }

        rollCount++
    }

    return dice
}