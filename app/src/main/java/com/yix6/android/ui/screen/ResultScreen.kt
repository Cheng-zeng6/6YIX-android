package com.yix6.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yix6.android.domain.model.HexagramResult
import com.yix6.android.domain.model.SixThrows
import com.yix6.android.ui.viewmodel.AppViewModel
import com.yix6.android.ui.viewmodel.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onAiInterpret: (hexagramName: String, changedName: String?, changingLines: List<Int>) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
    appViewModel: AppViewModel = viewModel(),
    resultViewModel: ResultViewModel = viewModel(),
) {
    val sixThrows by appViewModel.sixThrows.collectAsState()
    val resultState by resultViewModel.state.collectAsState()

    // Compute result when SixThrows is available
    LaunchedEffect(sixThrows) {
        sixThrows?.let { resultViewModel.compute(it) }
    }

    // Store result back in AppViewModel for other screens
    LaunchedEffect(resultState.result) {
        resultState.result?.let { appViewModel.setHexagramResult(it) }
    }

    val result = resultState.result

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reading Result") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
            )
        },
    ) { padding ->
        if (result == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Computing hexagram…")
            }
        } else {
            ResultContent(
                result = result,
                sixThrows = sixThrows,
                modifier = Modifier.padding(padding),
                onAiInterpret = onAiInterpret,
                onHome = onHome,
            )
        }
    }
}

@Composable
private fun ResultContent(
    result: HexagramResult,
    sixThrows: SixThrows?,
    modifier: Modifier = Modifier,
    onAiInterpret: (hexagramName: String, changedName: String?, changingLines: List<Int>) -> Unit,
    onHome: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Primary hexagram
        HexagramCard(
            title = "Primary Hexagram",
            number = result.originalHexagram,
            name = result.originalName,
            symbol = result.originalSymbol,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Changing lines
        if (result.changingLines.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Changing Lines",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.changingLines.joinToString(", ") { "Line $it" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Changed hexagram
            result.changedHexagram?.let { changedNum ->
                HexagramCard(
                    title = "Resulting Hexagram",
                    number = changedNum,
                    name = result.changedName ?: "Hexagram $changedNum",
                    symbol = result.changedSymbol ?: "",
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Throws summary
        if (sixThrows != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Line Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    sixThrows.throws.forEachIndexed { index, t ->
                        val label = when (t.value) {
                            6 -> "Old Yin ●"
                            7 -> "Young Yang —"
                            8 -> "Young Yin - -"
                            9 -> "Old Yang ○"
                            else -> "${t.value}"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Line ${index + 1}: ${t.display()}")
                            Text(
                                text = label,
                                color = if (t.isChanging)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        if (index < 5) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onAiInterpret(result.originalName, result.changedName, result.changingLines)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("✨  AI Interpretation")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("New Reading")
        }
    }
}

@Composable
private fun HexagramCard(
    title: String,
    number: Int,
    name: String,
    symbol: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = symbol,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hexagram $number",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
    }
}
