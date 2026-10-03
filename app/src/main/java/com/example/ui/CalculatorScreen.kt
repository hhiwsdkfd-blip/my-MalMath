package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showOfflineSolution by viewModel.showOfflineSolution.collectAsState()
    val currentOfflineSolution by viewModel.currentOfflineSolution.collectAsState()
    val showConstantsSheet by viewModel.showConstantsSheet.collectAsState()
    val showConverterSheet by viewModel.showConverterSheet.collectAsState()

    var showHistory by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "الحاسبة العلمية الفائقة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${currentTab.title} • بدون إنترنت 100%",
                            fontSize = 11.sp,
                            color = MathCyan
                        )
                    }
                },
                actions = {
                    // Open Constants
                    IconButton(onClick = { viewModel.setShowConstantsSheet(true) }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "الثوابت",
                            tint = MathGold
                        )
                    }
                    // Open Converter
                    IconButton(onClick = { viewModel.setShowConverterSheet(true) }) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "محول الوحدات",
                            tint = MathEmerald
                        )
                    }
                    // Open AI Solver
                    IconButton(
                        onClick = { viewModel.setTab(CalculatorTab.AI_SOLVER) },
                        modifier = Modifier.testTag("open_ai_solver_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "المعلم الذكي",
                            tint = if (currentTab == CalculatorTab.AI_SOLVER) MathGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Open History
                    IconButton(
                        onClick = { showHistory = true },
                        modifier = Modifier.testTag("open_history_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "سجل العمليات",
                            tint = MathCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                listOf(
                    Triple(CalculatorTab.SCIENTIFIC, Icons.Default.Calculate, "علمية"),
                    Triple(CalculatorTab.CALCULUS, Icons.Default.Functions, "تفاضل"),
                    Triple(CalculatorTab.LIMITS, Icons.Default.Timeline, "نهايات"),
                    Triple(CalculatorTab.ALGEBRA, Icons.Default.DataObject, "الجبر"),
                    Triple(CalculatorTab.GEOMETRY, Icons.Default.SquareFoot, "الهندسة"),
                    Triple(CalculatorTab.GRAPH, Icons.AutoMirrored.Filled.ShowChart, "بياني")
                ).forEach { (tab, icon, label) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) MathCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MathCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MathCyanContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                CalculatorTab.SCIENTIFIC -> ScientificCalculatorView(viewModel = viewModel)
                CalculatorTab.CALCULUS -> CalculusView(viewModel = viewModel)
                CalculatorTab.LIMITS -> LimitsView(viewModel = viewModel)
                CalculatorTab.ALGEBRA -> AlgebraView(viewModel = viewModel)
                CalculatorTab.GEOMETRY -> GeometryView(viewModel = viewModel)
                CalculatorTab.GRAPH -> GraphingView(viewModel = viewModel)
                CalculatorTab.AI_SOLVER -> AiSolverView(viewModel = viewModel)
            }
        }
    }

    // MalMath-style Step-by-Step Offline Solution Sheet
    if (showOfflineSolution && currentOfflineSolution != null) {
        OfflineSolutionSheet(
            solution = currentOfflineSolution!!,
            onDismiss = { viewModel.closeOfflineSolution() },
            onOpenGraph = { func ->
                viewModel.graphExpr.value = func
                viewModel.setTab(CalculatorTab.GRAPH)
                viewModel.closeOfflineSolution()
            }
        )
    }

    // Calc Business & HiEdu CONST Sheet
    if (showConstantsSheet) {
        ConstantsSheet(
            onDismiss = { viewModel.setShowConstantsSheet(false) },
            onSelectConstant = { constant ->
                viewModel.insertConstant(constant)
            }
        )
    }

    // Calc Business & HiEdu CONV Unit Converter Sheet
    if (showConverterSheet) {
        UnitConverterSheet(
            onDismiss = { viewModel.setShowConverterSheet(false) },
            onInsertResult = { valStr ->
                viewModel.appendSci(valStr)
            }
        )
    }

    // Calculation History
    if (showHistory) {
        HistorySheet(
            viewModel = viewModel,
            onDismiss = { showHistory = false }
        )
    }
}
