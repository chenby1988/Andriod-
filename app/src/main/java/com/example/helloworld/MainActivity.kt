package com.example.helloworld

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloworld.ui.theme.HelloWorldTheme
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlinx.coroutines.launch
import kotlin.math.tan

enum class CalcMode { STANDARD, SCIENTIFIC, PROGRAMMER, DATE, CURRENCY, VOLUME, LENGTH, WEIGHT, TEMPERATURE, ENERGY, AREA, SPEED, TIME, POWER, DATA, PRESSURE, ANGLE, ABOUT }
enum class DateCalcMode { DIFFERENCE, ADD_SUBTRACT }
enum class Base { HEX, DEC, OCT, BIN }
enum class BitWidth { BYTE, WORD, DWORD, QWORD }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloWorldTheme {
                CalculatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var previousValue by remember { mutableStateOf<Double?>(null) }
    var previousValueLong by remember { mutableStateOf<Long?>(null) }
    var currentOperation by remember { mutableStateOf<String?>(null) }
    var memory by remember { mutableStateOf(0.0) }
    var memoryLong by remember { mutableStateOf(0L) }
    var shouldResetDisplay by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(CalcMode.STANDARD) }
    var showMenu by remember { mutableStateOf(false) }
    var bracketStack by remember { mutableStateOf(mutableListOf<Pair<Double?, String?>>()) }
    var bracketStackLong by remember { mutableStateOf(mutableListOf<Pair<Long?, String?>>()) }

    var isDegMode by remember { mutableStateOf(true) }
    var currentBase by remember { mutableStateOf(Base.DEC) }
    var bitWidth by remember { mutableStateOf(BitWidth.QWORD) }
    var lastProgrammerResult by remember { mutableStateOf<Long?>(null) }

    // Date calculation states
    var dateMode by remember { mutableStateOf(DateCalcMode.DIFFERENCE) }
    var fromDate by remember { mutableStateOf(LocalDate.now()) }
    var toDate by remember { mutableStateOf(LocalDate.now()) }
    var daysInput by remember { mutableStateOf("0") }

    // Currency states
    var currencyAmount by remember { mutableStateOf("1") }
    var sourceCurrency by remember { mutableStateOf("USD") }
    var targetCurrency by remember { mutableStateOf("CNY") }
    var lastRateUpdate by remember { mutableStateOf(java.time.LocalDateTime.now()) }

    // Volume states
    var volumeAmount by remember { mutableStateOf("1") }
    var sourceVolumeUnit by remember { mutableStateOf("立方米") }
    var targetVolumeUnit by remember { mutableStateOf("毫升") }

    // Length states
    var lengthAmount by remember { mutableStateOf("1") }
    var sourceLengthUnit by remember { mutableStateOf("公里") }
    var targetLengthUnit by remember { mutableStateOf("米") }

    // Weight states
    var weightAmount by remember { mutableStateOf("1") }
    var sourceWeightUnit by remember { mutableStateOf("千克") }
    var targetWeightUnit by remember { mutableStateOf("磅") }

    // Temperature states
    var temperatureAmount by remember { mutableStateOf("0") }
    var sourceTemperatureUnit by remember { mutableStateOf("摄氏度") }
    var targetTemperatureUnit by remember { mutableStateOf("华氏度") }

    // Energy states
    var energyAmount by remember { mutableStateOf("1") }
    var sourceEnergyUnit by remember { mutableStateOf("千焦") }
    var targetEnergyUnit by remember { mutableStateOf("千卡") }

    // Area states
    var areaAmount by remember { mutableStateOf("1") }
    var sourceAreaUnit by remember { mutableStateOf("平方米") }
    var targetAreaUnit by remember { mutableStateOf("平方英尺") }

    // Speed states
    var speedAmount by remember { mutableStateOf("1") }
    var sourceSpeedUnit by remember { mutableStateOf("公里/小时") }
    var targetSpeedUnit by remember { mutableStateOf("英里/小时") }

    // Time states
    var timeAmount by remember { mutableStateOf("1") }
    var sourceTimeUnit by remember { mutableStateOf("小时") }
    var targetTimeUnit by remember { mutableStateOf("分钟") }

    // Power states
    var powerAmount by remember { mutableStateOf("1") }
    var sourcePowerUnit by remember { mutableStateOf("千瓦") }
    var targetPowerUnit by remember { mutableStateOf("马力(公制)") }

    // Data states
    var dataAmount by remember { mutableStateOf("1") }
    var sourceDataUnit by remember { mutableStateOf("吉字节") }
    var targetDataUnit by remember { mutableStateOf("兆字节") }

    // Pressure states
    var pressureAmount by remember { mutableStateOf("1") }
    var sourcePressureUnit by remember { mutableStateOf("千帕") }
    var targetPressureUnit by remember { mutableStateOf("大气压") }

    // Angle states
    var angleAmount by remember { mutableStateOf("90") }
    var sourceAngleUnit by remember { mutableStateOf("度") }
    var targetAngleUnit by remember { mutableStateOf("弧度") }

    LaunchedEffect(mode) {
        display = "0"
        previousValue = null
        previousValueLong = null
        currentOperation = null
        bracketStack.clear()
        bracketStackLong.clear()
        shouldResetDisplay = false
    }

    fun getCurrentValue(): Double {
        return display.toDoubleOrNull() ?: 0.0
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return "Error"
        return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    }

    fun toRadians(deg: Double): Double = deg * kotlin.math.PI / 180.0

    fun factorial(n: Double): Double {
        if (n < 0 || n % 1.0 != 0.0) return Double.NaN
        var result = 1.0
        for (i in 2..n.toInt()) result *= i
        return result
    }

    fun calculateBinary(): Double {
        val current = getCurrentValue()
        val prev = previousValue ?: return current
        return when (currentOperation) {
            "+" -> prev + current
            "−" -> prev - current
            "×" -> prev * current
            "÷" -> if (current != 0.0) prev / current else Double.NaN
            "^" -> prev.pow(current)
            "Mod" -> prev % current
            else -> current
        }
    }

    fun applyBitWidth(value: Long, width: BitWidth = bitWidth): Long {
        return when (width) {
            BitWidth.BYTE -> value.toByte().toLong()
            BitWidth.WORD -> value.toShort().toLong()
            BitWidth.DWORD -> value.toInt().toLong()
            BitWidth.QWORD -> value
        }
    }

    fun parseProgrammerValue(input: String): Long {
        return try {
            val value = when (currentBase) {
                Base.DEC -> input.toLong()
                Base.HEX -> input.toLong(16)
                Base.OCT -> input.toLong(8)
                Base.BIN -> input.toLong(2)
            }
            applyBitWidth(value)
        } catch (e: Exception) {
            0L
        }
    }

    fun formatProgrammerDisplay(value: Long): String {
        return when (currentBase) {
            Base.DEC -> value.toString()
            Base.HEX -> {
                val mask = when (bitWidth) {
                    BitWidth.BYTE -> 0xFFL
                    BitWidth.WORD -> 0xFFFFL
                    BitWidth.DWORD -> 0xFFFFFFFFL
                    BitWidth.QWORD -> -1L
                }
                (value and mask).toString(16).uppercase()
            }
            Base.OCT -> {
                val mask = when (bitWidth) {
                    BitWidth.BYTE -> 0xFFL
                    BitWidth.WORD -> 0xFFFFL
                    BitWidth.DWORD -> 0xFFFFFFFFL
                    BitWidth.QWORD -> -1L
                }
                (value and mask).toString(8)
            }
            Base.BIN -> {
                val mask = when (bitWidth) {
                    BitWidth.BYTE -> 0xFFL
                    BitWidth.WORD -> 0xFFFFL
                    BitWidth.DWORD -> 0xFFFFFFFFL
                    BitWidth.QWORD -> -1L
                }
                (value and mask).toString(2)
            }
        }
    }

    fun calculateProgrammer(): Long {
        val current = parseProgrammerValue(display)
        val prev = previousValueLong ?: return current
        val result = when (currentOperation) {
            "+" -> prev + current
            "−" -> prev - current
            "×" -> prev * current
            "÷" -> if (current != 0L) prev / current else 0L
            "Mod" -> if (current != 0L) prev % current else 0L
            "Lsh" -> if (current >= 0 && current < 64) prev shl current.toInt() else 0L
            "Rsh" -> if (current >= 0 && current < 64) prev ushr current.toInt() else 0L
            "Or" -> prev or current
            "Xor" -> prev xor current
            "And" -> prev and current
            else -> current
        }
        return applyBitWidth(result)
    }

    fun getModeTitle(): String {
        return when (mode) {
            CalcMode.STANDARD -> "计算器"
            CalcMode.SCIENTIFIC -> "科学计算器"
            CalcMode.PROGRAMMER -> "程序员计算器"
            CalcMode.DATE -> "日期计算"
            CalcMode.CURRENCY -> "货币"
            CalcMode.VOLUME -> "体积"
            CalcMode.LENGTH -> "长度"
            CalcMode.WEIGHT -> "重量"
            CalcMode.TEMPERATURE -> "温度"
            CalcMode.ENERGY -> "能量"
            CalcMode.AREA -> "面积"
            CalcMode.SPEED -> "速度"
            CalcMode.TIME -> "时间"
            CalcMode.POWER -> "功率"
            CalcMode.DATA -> "数据"
            CalcMode.PRESSURE -> "压力"
            CalcMode.ANGLE -> "角度"
            CalcMode.ABOUT -> "关于"
        }
    }

    fun isDigitAvailable(digit: String): Boolean {
        if (mode != CalcMode.PROGRAMMER) return true
        return when (digit) {
            "0", "1" -> true
            "2", "3", "4", "5", "6", "7" -> currentBase != Base.BIN
            "8", "9" -> currentBase != Base.BIN && currentBase != Base.OCT
            "A", "B", "C", "D", "E", "F" -> currentBase == Base.HEX
            else -> true
        }
    }

    fun onButtonClick(label: String) {
        if (mode == CalcMode.PROGRAMMER) {
            when (label) {
                "HEX", "DEC", "OCT", "BIN" -> {
                    val newBase = Base.valueOf(label)
                    if (newBase != currentBase) {
                        val value = parseProgrammerValue(display)
                        currentBase = newBase
                        display = formatProgrammerDisplay(value)
                    }
                }
                "QWORD", "DWORD", "WORD", "BYTE" -> {
                    bitWidth = when (label) {
                        "QWORD" -> BitWidth.QWORD
                        "DWORD" -> BitWidth.DWORD
                        "WORD" -> BitWidth.WORD
                        "BYTE" -> BitWidth.BYTE
                        else -> bitWidth
                    }
                    val value = parseProgrammerValue(display)
                    display = formatProgrammerDisplay(applyBitWidth(value))
                }
                "=" -> {
                    val result = calculateProgrammer()
                    display = formatProgrammerDisplay(result)
                    lastProgrammerResult = result
                    previousValueLong = null
                    currentOperation = null
                    shouldResetDisplay = true
                }
                "+", "−", "×", "÷", "Mod", "Lsh", "Rsh", "Or", "Xor", "And" -> {
                    previousValueLong = parseProgrammerValue(display)
                    currentOperation = label
                    shouldResetDisplay = true
                }
                "Not" -> {
                    val value = parseProgrammerValue(display)
                    val result = applyBitWidth(value.inv())
                    display = formatProgrammerDisplay(result)
                    shouldResetDisplay = true
                }
                "CE" -> {
                    display = "0"
                }
                "C" -> {
                    display = "0"
                    previousValueLong = null
                    currentOperation = null
                }
                "⌫" -> {
                    display = if (display.length > 1 && display != "Error") display.dropLast(1) else "0"
                }
                "±" -> {
                    val value = parseProgrammerValue(display)
                    val result = applyBitWidth(-value)
                    display = formatProgrammerDisplay(result)
                    shouldResetDisplay = true
                }
                "↑" -> {
                    lastProgrammerResult?.let {
                        display = formatProgrammerDisplay(it)
                        shouldResetDisplay = true
                    }
                }
                "(" -> {
                    bracketStackLong.add(Pair(previousValueLong, currentOperation))
                    previousValueLong = null
                    currentOperation = null
                    shouldResetDisplay = true
                }
                ")" -> {
                    if (bracketStackLong.isNotEmpty()) {
                        val result = if (currentOperation != null) calculateProgrammer() else parseProgrammerValue(display)
                        val (prev, op) = bracketStackLong.removeAt(bracketStackLong.size - 1)
                        display = formatProgrammerDisplay(result)
                        previousValueLong = prev
                        currentOperation = op
                        if (previousValueLong == null) previousValueLong = result
                        shouldResetDisplay = true
                    }
                }
                "MC" -> memoryLong = 0L
                "MR" -> {
                    display = formatProgrammerDisplay(memoryLong)
                    shouldResetDisplay = true
                }
                "M+" -> {
                    memoryLong = applyBitWidth(memoryLong + parseProgrammerValue(display))
                    shouldResetDisplay = true
                }
                "M-" -> {
                    memoryLong = applyBitWidth(memoryLong - parseProgrammerValue(display))
                    shouldResetDisplay = true
                }
                "MS" -> {
                    memoryLong = parseProgrammerValue(display)
                    shouldResetDisplay = true
                }
                "." -> { }
                else -> {
                    if (!isDigitAvailable(label)) return
                    if (display == "0" || display == "Error" || shouldResetDisplay) {
                        display = label
                        shouldResetDisplay = false
                    } else {
                        display += label
                    }
                }
            }
            return
        }

        when (label) {
            "=" -> {
                if (currentOperation != null) {
                    val result = calculateBinary()
                    display = formatResult(result)
                    previousValue = null
                    currentOperation = null
                }
                shouldResetDisplay = true
            }
            "+", "−", "×", "÷", "^", "Mod" -> {
                if (currentOperation != null) {
                    val result = calculateBinary()
                    display = formatResult(result)
                    previousValue = result
                } else {
                    previousValue = getCurrentValue()
                }
                currentOperation = label
                shouldResetDisplay = true
            }
            "xʸ" -> {
                if (currentOperation != null) {
                    val result = calculateBinary()
                    display = formatResult(result)
                    previousValue = result
                } else {
                    previousValue = getCurrentValue()
                }
                currentOperation = "^"
                shouldResetDisplay = true
            }
            "C" -> {
                display = "0"
                previousValue = null
                currentOperation = null
                bracketStack.clear()
            }
            "CE" -> {
                display = "0"
            }
            "⌫" -> {
                display = when {
                    display.length > 1 && display != "Error" -> display.dropLast(1)
                    else -> "0"
                }
            }
            "±" -> {
                val value = getCurrentValue()
                display = formatResult(-value)
            }
            "%" -> {
                val value = getCurrentValue()
                display = formatResult(value / 100.0)
                shouldResetDisplay = true
            }
            "√" -> {
                val value = getCurrentValue()
                display = if (value >= 0) formatResult(sqrt(value)) else "Error"
                shouldResetDisplay = true
            }
            "x²" -> {
                val value = getCurrentValue()
                display = formatResult(value * value)
                shouldResetDisplay = true
            }
            "1/x" -> {
                val value = getCurrentValue()
                display = if (value != 0.0) formatResult(1.0 / value) else "Error"
                shouldResetDisplay = true
            }
            "sin" -> {
                val value = getCurrentValue()
                val rad = if (isDegMode) toRadians(value) else value
                display = formatResult(sin(rad))
                shouldResetDisplay = true
            }
            "cos" -> {
                val value = getCurrentValue()
                val rad = if (isDegMode) toRadians(value) else value
                display = formatResult(cos(rad))
                shouldResetDisplay = true
            }
            "tan" -> {
                val value = getCurrentValue()
                val rad = if (isDegMode) toRadians(value) else value
                display = formatResult(tan(rad))
                shouldResetDisplay = true
            }
            "log" -> {
                val value = getCurrentValue()
                display = if (value > 0) formatResult(log10(value)) else "Error"
                shouldResetDisplay = true
            }
            "ln" -> {
                val value = getCurrentValue()
                display = if (value > 0) formatResult(ln(value)) else "Error"
                shouldResetDisplay = true
            }
            "10^" -> {
                val value = getCurrentValue()
                display = formatResult(10.0.pow(value))
                shouldResetDisplay = true
            }
            "π" -> {
                display = "3.141592653589793"
                shouldResetDisplay = true
            }
            "n!" -> {
                val value = getCurrentValue()
                display = formatResult(factorial(value))
                shouldResetDisplay = true
            }
            "MC" -> memory = 0.0
            "MR" -> {
                display = formatResult(memory)
                shouldResetDisplay = true
            }
            "M+" -> {
                memory += getCurrentValue()
                shouldResetDisplay = true
            }
            "M-" -> {
                memory -= getCurrentValue()
                shouldResetDisplay = true
            }
            "MS" -> {
                memory = getCurrentValue()
                shouldResetDisplay = true
            }
            "(" -> {
                bracketStack.add(Pair(previousValue, currentOperation))
                previousValue = null
                currentOperation = null
                shouldResetDisplay = true
            }
            ")" -> {
                if (bracketStack.isNotEmpty()) {
                    val result = if (currentOperation != null) calculateBinary() else getCurrentValue()
                    val (prev, op) = bracketStack.removeAt(bracketStack.size - 1)
                    display = formatResult(result)
                    previousValue = prev
                    currentOperation = op
                    if (previousValue == null) previousValue = result
                    shouldResetDisplay = true
                }
            }
            "DEG", "RAD" -> {
                isDegMode = !isDegMode
            }
            "." -> {
                if (shouldResetDisplay || display == "Error") {
                    display = "0."
                    shouldResetDisplay = false
                } else if (!display.contains(".")) {
                    display += "."
                }
            }
            else -> {
                if (display == "0" || display == "Error" || shouldResetDisplay) {
                    display = label
                    shouldResetDisplay = false
                } else {
                    display += label
                }
            }
        }
    }

    val displayFontSize = when {
        display.length > 12 -> 32.sp
        display.length > 8 -> 40.sp
        else -> 56.sp
    }

    val standardButtons = listOf(
        listOf("MC", "MR", "M+", "M-"),
        listOf("MS", "%", "√", "x²"),
        listOf("1/x", "CE", "C", "⌫"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "−"),
        listOf("±", "0", ".", "+"),
        listOf("", "", "", "=")
    )

    val scientificButtons = listOf(
        listOf(if (isDegMode) "DEG" else "RAD", "", "", "", ""),
        listOf("MC", "MR", "M+", "M-", "MS"),
        listOf("x²", "xʸ", "sin", "cos", "tan"),
        listOf("√", "10^", "log", "ln", "Mod"),
        listOf("(", "CE", "C", "⌫", "÷"),
        listOf("π", "7", "8", "9", "×"),
        listOf("n!", "4", "5", "6", "−"),
        listOf("±", "1", "2", "3", "+"),
        listOf(")", "0", ".", "=", "")
    )

    val programmerButtons = listOf(
        listOf("QWORD", "", "MS", "M+", "", ""),
        listOf("Lsh", "Rsh", "Or", "Xor", "Not", "And"),
        listOf("↑", "Mod", "CE", "C", "⌫", "÷"),
        listOf("A", "B", "7", "8", "9", "×"),
        listOf("C", "D", "4", "5", "6", "−"),
        listOf("E", "F", "1", "2", "3", "+"),
        listOf("(", ")", "±", "0", "", "=")
    )

    val buttons = when (mode) {
        CalcMode.STANDARD -> standardButtons
        CalcMode.SCIENTIFIC -> scientificButtons
        CalcMode.PROGRAMMER -> programmerButtons
        CalcMode.DATE, CalcMode.CURRENCY, CalcMode.VOLUME, CalcMode.LENGTH,
        CalcMode.WEIGHT, CalcMode.TEMPERATURE, CalcMode.ENERGY, CalcMode.AREA,
        CalcMode.SPEED, CalcMode.TIME, CalcMode.POWER, CalcMode.DATA,
        CalcMode.PRESSURE, CalcMode.ANGLE, CalcMode.ABOUT -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getModeTitle()) },
                navigationIcon = {
                    IconButton(onClick = { showMenu = true }) {
                        Text("☰", color = Color.White, fontSize = 20.sp)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("标准") },
                            onClick = { mode = CalcMode.STANDARD; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("科学") },
                            onClick = { mode = CalcMode.SCIENTIFIC; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("程序员") },
                            onClick = { mode = CalcMode.PROGRAMMER; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("日期计算") },
                            onClick = { mode = CalcMode.DATE; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("货币") },
                            onClick = { mode = CalcMode.CURRENCY; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("体积") },
                            onClick = { mode = CalcMode.VOLUME; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("长度") },
                            onClick = { mode = CalcMode.LENGTH; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("重量") },
                            onClick = { mode = CalcMode.WEIGHT; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("温度") },
                            onClick = { mode = CalcMode.TEMPERATURE; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("能量") },
                            onClick = { mode = CalcMode.ENERGY; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("面积") },
                            onClick = { mode = CalcMode.AREA; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("速度") },
                            onClick = { mode = CalcMode.SPEED; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("时间") },
                            onClick = { mode = CalcMode.TIME; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("功率") },
                            onClick = { mode = CalcMode.POWER; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("数据") },
                            onClick = { mode = CalcMode.DATA; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("压力") },
                            onClick = { mode = CalcMode.PRESSURE; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("角度") },
                            onClick = { mode = CalcMode.ANGLE; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("关于") },
                            onClick = { mode = CalcMode.ABOUT; showMenu = false }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00897B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = when (mode) {
                    CalcMode.STANDARD -> 8.dp
                    CalcMode.SCIENTIFIC -> 4.dp
                    CalcMode.PROGRAMMER -> 2.dp
                    CalcMode.DATE -> 16.dp
                    else -> 16.dp
                }, vertical = 4.dp)
        ) {
            if (mode == CalcMode.DATE) {
                DateCalculatorContent(
                    dateMode = dateMode,
                    fromDate = fromDate,
                    toDate = toDate,
                    daysInput = daysInput,
                    onDateModeChange = { dateMode = it },
                    onFromDateChange = { fromDate = it },
                    onToDateChange = { toDate = it },
                    onDaysInputChange = { daysInput = it }
                )
            } else if (mode == CalcMode.CURRENCY) {
                CurrencyConverterContent(
                    amount = currencyAmount,
                    sourceCurrency = sourceCurrency,
                    targetCurrency = targetCurrency,
                    lastRateUpdate = lastRateUpdate,
                    onAmountChange = { currencyAmount = it },
                    onSourceCurrencyChange = { sourceCurrency = it },
                    onTargetCurrencyChange = { targetCurrency = it },
                    onRateUpdate = { lastRateUpdate = java.time.LocalDateTime.now() }
                )
            } else if (mode == CalcMode.VOLUME) {
                VolumeConverterContent(
                    amount = volumeAmount,
                    sourceUnit = sourceVolumeUnit,
                    targetUnit = targetVolumeUnit,
                    onAmountChange = { volumeAmount = it },
                    onSourceUnitChange = { sourceVolumeUnit = it },
                    onTargetUnitChange = { targetVolumeUnit = it }
                )
            } else if (mode == CalcMode.LENGTH) {
                LengthConverterContent(
                    amount = lengthAmount,
                    sourceUnit = sourceLengthUnit,
                    targetUnit = targetLengthUnit,
                    onAmountChange = { lengthAmount = it },
                    onSourceUnitChange = { sourceLengthUnit = it },
                    onTargetUnitChange = { targetLengthUnit = it }
                )
            } else if (mode == CalcMode.WEIGHT) {
                GenericConverterContent(
                    amount = weightAmount,
                    sourceUnit = sourceWeightUnit,
                    targetUnit = targetWeightUnit,
                    units = listOf("毫克", "克", "千克", "公吨", "盎司", "磅", "英石", "英吨", "美吨", "克拉"),
                    toBase = mapOf("毫克" to 0.001, "克" to 1.0, "千克" to 1000.0, "公吨" to 1_000_000.0, "盎司" to 28.3495, "磅" to 453.592, "英石" to 6350.29, "英吨" to 1_016_047.0, "美吨" to 907_185.0, "克拉" to 0.2),
                    getReferences = { grams ->
                        val refs = mutableListOf<String>()
                        val kg = grams / 1000.0
                        if (kg >= 0.001) refs.add(String.format("%.3f 千克", kg))
                        val lb = grams / 453.592
                        if (lb >= 0.01) refs.add(String.format("%.2f 磅", lb))
                        refs
                    },
                    onAmountChange = { weightAmount = it },
                    onSourceUnitChange = { sourceWeightUnit = it },
                    onTargetUnitChange = { targetWeightUnit = it }
                )
            } else if (mode == CalcMode.TEMPERATURE) {
                GenericConverterContent(
                    amount = temperatureAmount,
                    sourceUnit = sourceTemperatureUnit,
                    targetUnit = targetTemperatureUnit,
                    units = listOf("摄氏度", "华氏度", "开尔文"),
                    toBase = mapOf("摄氏度" to 1.0, "华氏度" to 1.0, "开尔文" to 1.0),
                    converter = { value, from, to ->
                        val celsius = when (from) {
                            "摄氏度" -> value
                            "华氏度" -> (value - 32) * 5.0 / 9.0
                            "开尔文" -> value - 273.15
                            else -> value
                        }
                        when (to) {
                            "摄氏度" -> celsius
                            "华氏度" -> celsius * 9.0 / 5.0 + 32.0
                            "开尔文" -> celsius + 273.15
                            else -> celsius
                        }
                    },
                    getReferences = { _ -> emptyList() },
                    onAmountChange = { temperatureAmount = it },
                    onSourceUnitChange = { sourceTemperatureUnit = it },
                    onTargetUnitChange = { targetTemperatureUnit = it }
                )
            } else if (mode == CalcMode.ENERGY) {
                GenericConverterContent(
                    amount = energyAmount,
                    sourceUnit = sourceEnergyUnit,
                    targetUnit = targetEnergyUnit,
                    units = listOf("焦耳", "千焦", "卡路里", "千卡", "瓦时", "千瓦时", "电子伏特"),
                    toBase = mapOf("焦耳" to 1.0, "千焦" to 1000.0, "卡路里" to 4.184, "千卡" to 4184.0, "瓦时" to 3600.0, "千瓦时" to 3_600_000.0, "电子伏特" to 1.60218e-19),
                    getReferences = { joules ->
                        val refs = mutableListOf<String>()
                        val kcal = joules / 4184.0
                        if (kcal >= 0.001) refs.add(String.format("%.3f 千卡", kcal))
                        refs
                    },
                    onAmountChange = { energyAmount = it },
                    onSourceUnitChange = { sourceEnergyUnit = it },
                    onTargetUnitChange = { targetEnergyUnit = it }
                )
            } else if (mode == CalcMode.AREA) {
                GenericConverterContent(
                    amount = areaAmount,
                    sourceUnit = sourceAreaUnit,
                    targetUnit = targetAreaUnit,
                    units = listOf("平方毫米", "平方厘米", "平方米", "公顷", "平方公里", "平方英寸", "平方英尺", "英亩"),
                    toBase = mapOf("平方毫米" to 0.000001, "平方厘米" to 0.0001, "平方米" to 1.0, "公顷" to 10_000.0, "平方公里" to 1_000_000.0, "平方英寸" to 0.00064516, "平方英尺" to 0.092903, "英亩" to 4046.86),
                    getReferences = { sqMeters ->
                        val refs = mutableListOf<String>()
                        val acres = sqMeters / 4046.86
                        if (acres >= 0.01) refs.add(String.format("%.2f 英亩", acres))
                        val sqFt = sqMeters / 0.092903
                        if (sqFt >= 1.0) refs.add(String.format("%,.0f 平方英尺", sqFt))
                        refs
                    },
                    onAmountChange = { areaAmount = it },
                    onSourceUnitChange = { sourceAreaUnit = it },
                    onTargetUnitChange = { targetAreaUnit = it }
                )
            } else if (mode == CalcMode.SPEED) {
                GenericConverterContent(
                    amount = speedAmount,
                    sourceUnit = sourceSpeedUnit,
                    targetUnit = targetSpeedUnit,
                    units = listOf("厘米/秒", "米/秒", "公里/小时", "节", "英里/小时"),
                    toBase = mapOf("厘米/秒" to 0.01, "米/秒" to 1.0, "公里/小时" to 0.277778, "节" to 0.514444, "英里/小时" to 0.44704),
                    getReferences = { mps ->
                        val refs = mutableListOf<String>()
                        val kmh = mps * 3.6
                        if (kmh >= 0.01) refs.add(String.format("%.2f 公里/小时", kmh))
                        refs
                    },
                    onAmountChange = { speedAmount = it },
                    onSourceUnitChange = { sourceSpeedUnit = it },
                    onTargetUnitChange = { targetSpeedUnit = it }
                )
            } else if (mode == CalcMode.TIME) {
                GenericConverterContent(
                    amount = timeAmount,
                    sourceUnit = sourceTimeUnit,
                    targetUnit = targetTimeUnit,
                    units = listOf("微秒", "毫秒", "秒", "分钟", "小时", "天", "周", "年"),
                    toBase = mapOf("微秒" to 0.000001, "毫秒" to 0.001, "秒" to 1.0, "分钟" to 60.0, "小时" to 3600.0, "天" to 86400.0, "周" to 604800.0, "年" to 31_536_000.0),
                    getReferences = { seconds ->
                        val refs = mutableListOf<String>()
                        val days = seconds / 86400.0
                        if (days >= 0.01) refs.add(String.format("%.2f 天", days))
                        val weeks = seconds / 604800.0
                        if (weeks >= 0.01) refs.add(String.format("%.2f 周", weeks))
                        refs
                    },
                    onAmountChange = { timeAmount = it },
                    onSourceUnitChange = { sourceTimeUnit = it },
                    onTargetUnitChange = { targetTimeUnit = it }
                )
            } else if (mode == CalcMode.POWER) {
                GenericConverterContent(
                    amount = powerAmount,
                    sourceUnit = sourcePowerUnit,
                    targetUnit = targetPowerUnit,
                    units = listOf("瓦特", "千瓦", "马力(公制)", "马力(英制)", "英热单位/小时"),
                    toBase = mapOf("瓦特" to 1.0, "千瓦" to 1000.0, "马力(公制)" to 735.499, "马力(英制)" to 745.7, "英热单位/小时" to 0.293071),
                    getReferences = { watts ->
                        val refs = mutableListOf<String>()
                        val hp = watts / 735.499
                        if (hp >= 0.01) refs.add(String.format("%.2f 马力(公制)", hp))
                        refs
                    },
                    onAmountChange = { powerAmount = it },
                    onSourceUnitChange = { sourcePowerUnit = it },
                    onTargetUnitChange = { targetPowerUnit = it }
                )
            } else if (mode == CalcMode.DATA) {
                GenericConverterContent(
                    amount = dataAmount,
                    sourceUnit = sourceDataUnit,
                    targetUnit = targetDataUnit,
                    units = listOf("位", "字节", "千字节", "兆字节", "吉字节", "太字节", "拍字节"),
                    toBase = mapOf("位" to 0.125, "字节" to 1.0, "千字节" to 1024.0, "兆字节" to 1_048_576.0, "吉字节" to 1_073_741_824.0, "太字节" to 1_099_511_627_776.0, "拍字节" to 1_125_899_906_842_624.0),
                    getReferences = { bytes ->
                        val refs = mutableListOf<String>()
                        val cds = bytes / (700.0 * 1024.0 * 1024.0)
                        if (cds >= 0.01) refs.add(String.format("%.2f CD", cds))
                        refs
                    },
                    onAmountChange = { dataAmount = it },
                    onSourceUnitChange = { sourceDataUnit = it },
                    onTargetUnitChange = { targetDataUnit = it }
                )
            } else if (mode == CalcMode.PRESSURE) {
                GenericConverterContent(
                    amount = pressureAmount,
                    sourceUnit = sourcePressureUnit,
                    targetUnit = targetPressureUnit,
                    units = listOf("帕斯卡", "千帕", "巴", "大气压", "毫米汞柱", "磅/平方英寸"),
                    toBase = mapOf("帕斯卡" to 1.0, "千帕" to 1000.0, "巴" to 100_000.0, "大气压" to 101_325.0, "毫米汞柱" to 133.322, "磅/平方英寸" to 6894.76),
                    getReferences = { pascals ->
                        val refs = mutableListOf<String>()
                        val atm = pascals / 101325.0
                        if (atm >= 0.001) refs.add(String.format("%.3f 大气压", atm))
                        refs
                    },
                    onAmountChange = { pressureAmount = it },
                    onSourceUnitChange = { sourcePressureUnit = it },
                    onTargetUnitChange = { targetPressureUnit = it }
                )
            } else if (mode == CalcMode.ANGLE) {
                GenericConverterContent(
                    amount = angleAmount,
                    sourceUnit = sourceAngleUnit,
                    targetUnit = targetAngleUnit,
                    units = listOf("度", "弧度", "梯度"),
                    toBase = mapOf("度" to 1.0, "弧度" to 57.2958, "梯度" to 0.9),
                    getReferences = { degrees ->
                        val refs = mutableListOf<String>()
                        val circles = degrees / 360.0
                        if (circles >= 0.01) refs.add(String.format("%.2f 圆周", circles))
                        refs
                    },
                    onAmountChange = { angleAmount = it },
                    onSourceUnitChange = { sourceAngleUnit = it },
                    onTargetUnitChange = { targetAngleUnit = it }
                )
            } else if (mode == CalcMode.ABOUT) {
                AboutContent()
            } else {
            if (mode == CalcMode.PROGRAMMER) {
                val currentValue = parseProgrammerValue(display)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    listOf(Base.HEX, Base.DEC, Base.OCT, Base.BIN).forEach { base ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onButtonClick(base.name) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(20.dp)
                                    .background(if (currentBase == base) Color(0xFF2196F3) else Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = base.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(
                                text = formatProgrammerDisplay(currentValue),
                                fontSize = 13.sp,
                                fontWeight = if (currentBase == base) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentBase == base) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(8.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = display,
                    fontSize = displayFontSize,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { label ->
                        if (label.isEmpty()) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            val isDisabled = mode == CalcMode.PROGRAMMER && !isDigitAvailable(label)

                            val bgColor = when {
                                isDisabled -> Color(0xFFE0E0E0)
                                label in listOf("C", "CE", "⌫") -> Color(0xFFE53935)
                                label in listOf("=", "+", "−", "×", "÷") -> Color(0xFF00897B)
                                label in listOf("^", "Mod", "Lsh", "Rsh", "Or", "Xor", "Not", "And") -> Color(0xFF00897B)
                                label in listOf("MC", "MR", "M+", "M-", "MS", "QWORD", "DWORD", "WORD", "BYTE") -> Color(0xFFD0D0D0)
                                label in listOf("%", "√", "x²", "1/x", "±", ".", "sin", "cos", "tan", "log", "ln", "10^", "π", "n!", "(", ")", "xʸ", "↑") -> Color(0xFFE0E0E0)
                                label in listOf("DEG", "RAD") -> Color(0xFF607D8B)
                                label in listOf("A", "B", "C", "D", "E", "F") -> Color(0xFFF5F5F5)
                                else -> Color(0xFFF5F5F5)
                            }

                            val textColor = when {
                                isDisabled -> Color(0xFFAAAAAA)
                                label in listOf("C", "CE", "⌫", "=", "+", "−", "×", "÷", "^", "Mod", "Lsh", "Rsh", "Or", "Xor", "Not", "And") -> Color.White
                                label in listOf("DEG", "RAD") -> Color.White
                                else -> Color.Black
                            }

                            Button(
                                onClick = { onButtonClick(label) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = when (mode) {
                                        CalcMode.STANDARD -> 3.dp
                                        CalcMode.SCIENTIFIC -> 2.dp
                                        CalcMode.PROGRAMMER -> 1.dp
                                        else -> 1.dp
                                    })
                                    .fillMaxHeight(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = bgColor,
                                    contentColor = textColor,
                                    disabledContainerColor = Color(0xFFE0E0E0),
                                    disabledContentColor = Color(0xFFAAAAAA)
                                ),
                                enabled = !isDisabled
                            ) {
                                Text(
                                    text = label,
                                    fontSize = when {
                                        label.length > 3 -> 10.sp
                                        label.length > 1 -> 12.sp
                                        else -> 18.sp
                                    }
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
fun AboutContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // App icon placeholder
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF00897B), shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Calc",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Calculator",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "版本 1.0",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "© 2026 Calculator. 保留所有权利。",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Feature list
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("功能模块", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("• 标准计算器", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 科学计算器", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 程序员计算器", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 日期计算", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 货币转换（联网实时汇率）", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 体积 / 长度 / 重量 / 温度", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 能量 / 面积 / 速度 / 时间", fontSize = 14.sp, color = Color.DarkGray)
            Text("• 功率 / 数据 / 压力 / 角度", fontSize = 14.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "使用 Jetpack Compose + Kotlin 开发",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun GenericConverterContent(
    amount: String,
    sourceUnit: String,
    targetUnit: String,
    units: List<String>,
    toBase: Map<String, Double>,
    converter: ((Double, String, String) -> Double)? = null,
    getReferences: (Double) -> List<String> = { emptyList() },
    onAmountChange: (String) -> Unit,
    onSourceUnitChange: (String) -> Unit,
    onTargetUnitChange: (String) -> Unit
) {
    fun defaultConvert(value: Double, from: String, to: String): Double {
        val base = value * (toBase[from] ?: 1.0)
        return base / (toBase[to] ?: 1.0)
    }

    fun convert(value: Double, from: String, to: String): Double {
        return converter?.invoke(value, from, to) ?: defaultConvert(value, from, to)
    }

    fun formatValue(value: Double): String {
        return when {
            value == 0.0 -> "0"
            value % 1.0 == 0.0 -> String.format("%,d", value.toLong())
            value >= 1000 -> String.format("%,.2f", value)
            value >= 1 -> String.format("%.4f", value).trimEnd('0').trimEnd('.')
            else -> String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }

    val value = amount.toDoubleOrNull() ?: 0.0
    val baseValue = if (converter != null) {
        when (sourceUnit) {
            "摄氏度" -> value
            "华氏度" -> (value - 32) * 5.0 / 9.0
            "开尔文" -> value - 273.15
            else -> value
        }
    } else {
        value * (toBase[sourceUnit] ?: 1.0)
    }
    val converted = convert(value, sourceUnit, targetUnit)
    val references = getReferences(baseValue)

    @Composable
    fun UnitSelector(
        selected: String,
        onSelect: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { expanded = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selected, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Text(" ▼", fontSize = 12.sp, color = Color.Gray)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = { onSelect(unit); expanded = false }
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = amount, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        UnitSelector(selected = sourceUnit, onSelect = onSourceUnitChange)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(1.dp)
                .background(Color.LightGray)
        )

        Text(text = formatValue(converted), fontSize = 48.sp, fontWeight = FontWeight.Bold)
        UnitSelector(selected = targetUnit, onSelect = onTargetUnitChange)

        if (references.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("约等于", fontSize = 12.sp, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                references.forEach { ref ->
                    Text(ref, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val padButtons = listOf(
            listOf("CE", "⌫", ""),
            listOf("7", "8", "9"),
            listOf("4", "5", "6"),
            listOf("1", "2", "3"),
            listOf("", "0", ".")
        )

        padButtons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    if (label.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val bgColor = when (label) {
                            "CE", "⌫" -> Color(0xFFE53935)
                            else -> Color(0xFFF5F5F5)
                        }
                        val textColor = when (label) {
                            "CE", "⌫" -> Color.White
                            else -> Color.Black
                        }
                        Button(
                            onClick = {
                                when (label) {
                                    "CE" -> onAmountChange("0")
                                    "⌫" -> onAmountChange(if (amount.length > 1) amount.dropLast(1) else "0")
                                    "." -> {
                                        if (!amount.contains(".")) onAmountChange(amount + ".")
                                    }
                                    else -> {
                                        if (amount == "0") onAmountChange(label) else onAmountChange(amount + label)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bgColor,
                                contentColor = textColor
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = if (label.length > 1) 16.sp else 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LengthConverterContent(
    amount: String,
    sourceUnit: String,
    targetUnit: String,
    onAmountChange: (String) -> Unit,
    onSourceUnitChange: (String) -> Unit,
    onTargetUnitChange: (String) -> Unit
) {
    val lengthUnits = remember {
        listOf("微米", "毫米", "厘米", "米", "公里", "英寸", "英尺", "码", "英里", "海里")
    }

    val toMeters = remember {
        mapOf(
            "微米" to 0.000001,
            "毫米" to 0.001,
            "厘米" to 0.01,
            "米" to 1.0,
            "公里" to 1000.0,
            "英寸" to 0.0254,
            "英尺" to 0.3048,
            "码" to 0.9144,
            "英里" to 1609.344,
            "海里" to 1852.0
        )
    }

    fun convert(value: Double, from: String, to: String): Double {
        val meters = value * (toMeters[from] ?: 1.0)
        return meters / (toMeters[to] ?: 1.0)
    }

    fun formatLength(value: Double): String {
        return when {
            value == 0.0 -> "0"
            value % 1.0 == 0.0 -> String.format("%,d", value.toLong())
            value >= 1000 -> String.format("%,.2f", value)
            value >= 1 -> String.format("%.4f", value).trimEnd('0').trimEnd('.')
            else -> String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun getReferences(meters: Double): List<String> {
        val refs = mutableListOf<String>()
        val miles = meters / 1609.344
        if (miles >= 0.01) refs.add(String.format("%.2f 英里", miles))
        val yards = meters / 0.9144
        if (yards >= 0.01) refs.add(String.format("%,.0f 码", yards))
        val jets = meters / 76.0
        if (jets >= 0.01) refs.add(String.format("%.2f 大型喷气式客机", jets))
        return refs
    }

    val value = amount.toDoubleOrNull() ?: 0.0
    val meters = value * (toMeters[sourceUnit] ?: 1.0)
    val converted = convert(value, sourceUnit, targetUnit)
    val references = getReferences(meters)

    @Composable
    fun UnitSelector(
        selected: String,
        onSelect: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { expanded = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selected, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Text(" ▼", fontSize = 12.sp, color = Color.Gray)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                lengthUnits.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = { onSelect(unit); expanded = false }
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = amount, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        UnitSelector(selected = sourceUnit, onSelect = onSourceUnitChange)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(1.dp)
                .background(Color.LightGray)
        )

        Text(text = formatLength(converted), fontSize = 48.sp, fontWeight = FontWeight.Bold)
        UnitSelector(selected = targetUnit, onSelect = onTargetUnitChange)

        if (references.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("约等于", fontSize = 12.sp, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                references.forEach { ref ->
                    Text(ref, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val padButtons = listOf(
            listOf("CE", "⌫", ""),
            listOf("7", "8", "9"),
            listOf("4", "5", "6"),
            listOf("1", "2", "3"),
            listOf("", "0", ".")
        )

        padButtons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    if (label.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val bgColor = when (label) {
                            "CE", "⌫" -> Color(0xFFE53935)
                            else -> Color(0xFFF5F5F5)
                        }
                        val textColor = when (label) {
                            "CE", "⌫" -> Color.White
                            else -> Color.Black
                        }
                        Button(
                            onClick = {
                                when (label) {
                                    "CE" -> onAmountChange("0")
                                    "⌫" -> onAmountChange(if (amount.length > 1) amount.dropLast(1) else "0")
                                    "." -> {
                                        if (!amount.contains(".")) onAmountChange(amount + ".")
                                    }
                                    else -> {
                                        if (amount == "0") onAmountChange(label) else onAmountChange(amount + label)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bgColor,
                                contentColor = textColor
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = if (label.length > 1) 16.sp else 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VolumeConverterContent(
    amount: String,
    sourceUnit: String,
    targetUnit: String,
    onAmountChange: (String) -> Unit,
    onSourceUnitChange: (String) -> Unit,
    onTargetUnitChange: (String) -> Unit
) {
    val volumeUnits = remember {
        listOf(
            "毫升", "立方厘米", "升", "立方米",
            "茶匙(美制)", "汤匙(美制)", "液盎司(美制)", "杯(美制)", "品脱(美制)", "夸脱(美制)", "加仑(美制)",
            "立方英寸", "立方英尺", "立方码",
            "茶匙(英制)", "汤匙(英制)", "液盎司(英制)", "品脱(英制)", "夸脱(英制)", "加仑(英制)"
        )
    }

    val toLiters = remember {
        mapOf(
            "毫升" to 0.001,
            "立方厘米" to 0.001,
            "升" to 1.0,
            "立方米" to 1000.0,
            "茶匙(美制)" to 0.00492892,
            "汤匙(美制)" to 0.0147868,
            "液盎司(美制)" to 0.0295735,
            "杯(美制)" to 0.236588,
            "品脱(美制)" to 0.473176,
            "夸脱(美制)" to 0.946353,
            "加仑(美制)" to 3.78541,
            "立方英寸" to 0.0163871,
            "立方英尺" to 28.3168,
            "立方码" to 764.555,
            "茶匙(英制)" to 0.00591939,
            "汤匙(英制)" to 0.0177582,
            "液盎司(英制)" to 0.0284131,
            "品脱(英制)" to 0.568261,
            "夸脱(英制)" to 1.13652,
            "加仑(英制)" to 4.54609
        )
    }

    fun convert(value: Double, from: String, to: String): Double {
        val liters = value * (toLiters[from] ?: 1.0)
        return liters / (toLiters[to] ?: 1.0)
    }

    fun formatVolume(value: Double): String {
        return when {
            value == 0.0 -> "0"
            value % 1.0 == 0.0 -> String.format("%,d", value.toLong())
            value >= 1000 -> String.format("%,.2f", value)
            value >= 1 -> String.format("%.4f", value).trimEnd('0').trimEnd('.')
            else -> String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun getReferences(liters: Double): List<String> {
        val refs = mutableListOf<String>()
        val cubicYards = liters / 764.555
        if (cubicYards >= 0.01) refs.add(String.format("%.2f 立方码", cubicYards))
        val cubicFeet = liters / 28.3168
        if (cubicFeet >= 0.01) refs.add(String.format("%.2f 立方英尺", cubicFeet))
        val bathtubs = liters / 378.5
        if (bathtubs >= 0.01) refs.add(String.format("%.2f 浴缸", bathtubs))
        return refs
    }

    val value = amount.toDoubleOrNull() ?: 0.0
    val liters = value * (toLiters[sourceUnit] ?: 1.0)
    val converted = convert(value, sourceUnit, targetUnit)
    val references = getReferences(liters)

    @Composable
    fun UnitSelector(
        selected: String,
        onSelect: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { expanded = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selected, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Text(" ▼", fontSize = 12.sp, color = Color.Gray)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                volumeUnits.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = { onSelect(unit); expanded = false }
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Source amount
        Text(
            text = amount,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        // Source unit selector
        UnitSelector(selected = sourceUnit, onSelect = onSourceUnitChange)

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(1.dp)
                .background(Color.LightGray)
        )

        // Target amount
        Text(
            text = formatVolume(converted),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        // Target unit selector
        UnitSelector(selected = targetUnit, onSelect = onTargetUnitChange)

        // References
        if (references.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("约等于", fontSize = 12.sp, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                references.forEach { ref ->
                    Text(ref, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Number pad
        val padButtons = listOf(
            listOf("CE", "⌫", ""),
            listOf("7", "8", "9"),
            listOf("4", "5", "6"),
            listOf("1", "2", "3"),
            listOf("", "0", ".")
        )

        padButtons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    if (label.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val bgColor = when (label) {
                            "CE", "⌫" -> Color(0xFFE53935)
                            else -> Color(0xFFF5F5F5)
                        }
                        val textColor = when (label) {
                            "CE", "⌫" -> Color.White
                            else -> Color.Black
                        }
                        Button(
                            onClick = {
                                when (label) {
                                    "CE" -> onAmountChange("0")
                                    "⌫" -> onAmountChange(if (amount.length > 1) amount.dropLast(1) else "0")
                                    "." -> {
                                        if (!amount.contains(".")) onAmountChange(amount + ".")
                                    }
                                    else -> {
                                        if (amount == "0") onAmountChange(label) else onAmountChange(amount + label)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bgColor,
                                contentColor = textColor
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = if (label.length > 1) 16.sp else 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyConverterContent(
    amount: String,
    sourceCurrency: String,
    targetCurrency: String,
    lastRateUpdate: java.time.LocalDateTime,
    onAmountChange: (String) -> Unit,
    onSourceCurrencyChange: (String) -> Unit,
    onTargetCurrencyChange: (String) -> Unit,
    onRateUpdate: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var onlineRates by remember { mutableStateOf<Map<String, Double>?>(null) }

    // Fallback rates (used when network is unavailable)
    val fallbackRates = remember {
        mapOf(
            "USD" to 1.0, "CNY" to 6.82, "EUR" to 0.85, "GBP" to 0.72,
            "JPY" to 148.0, "KRW" to 1320.0, "HKD" to 7.75, "AUD" to 1.48,
            "CAD" to 1.32, "CHF" to 0.82, "SGD" to 1.30, "INR" to 82.0,
            "RUB" to 88.0, "THB" to 35.0
        )
    }

    val exchangeRates = onlineRates ?: fallbackRates
    val currencies = remember { fallbackRates.keys.toList() }

    fun getCurrencySymbol(code: String) = when (code) {
        "USD" -> "$"; "CNY" -> "¥"; "EUR" -> "€"; "GBP" -> "£"
        "JPY" -> "¥"; "KRW" -> "₩"; "HKD" -> "HK$"; "AUD" -> "A$"
        "CAD" -> "C$"; "CHF" -> "Fr"; "SGD" -> "S$"; "INR" -> "₹"
        "RUB" -> "₽"; "THB" -> "฿"; else -> code
    }

    fun getCurrencyName(code: String) = when (code) {
        "USD" -> "美国 - 美元"; "CNY" -> "中国 - 人民币"; "EUR" -> "欧盟 - 欧元"
        "GBP" -> "英国 - 英镑"; "JPY" -> "日本 - 日元"; "KRW" -> "韩国 - 韩元"
        "HKD" -> "香港 - 港币"; "AUD" -> "澳大利亚 - 澳元"; "CAD" -> "加拿大 - 加元"
        "CHF" -> "瑞士 - 法郎"; "SGD" -> "新加坡 - 新元"; "INR" -> "印度 - 卢比"
        "RUB" -> "俄罗斯 - 卢布"; "THB" -> "泰国 - 泰铢"; else -> code
    }

    fun convert(value: Double, from: String, to: String): Double {
        val fromRate = exchangeRates[from] ?: 1.0
        val toRate = exchangeRates[to] ?: 1.0
        return value / fromRate * toRate
    }

    fun fetchRates() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = com.example.helloworld.data.ExchangeRateRepository.getRates(sourceCurrency)
            result.onSuccess { response ->
                val filtered = response.rates.filterKeys { it in currencies }
                onlineRates = filtered
                onRateUpdate()
                errorMessage = null
            }.onFailure { error ->
                errorMessage = "网络请求失败，使用离线汇率"
            }
            isLoading = false
        }
    }

    LaunchedEffect(sourceCurrency) {
        fetchRates()
    }

    val converted = convert(amount.toDoubleOrNull() ?: 0.0, sourceCurrency, targetCurrency)
    val rate = convert(1.0, sourceCurrency, targetCurrency)
    val formatter = remember { DateTimeFormatter.ofPattern("M月d, yyyy HH:mm") }
    val updateTimeStr = lastRateUpdate.format(formatter)
    val isOnline = onlineRates != null

    @Composable
    fun CurrencySelector(
        selected: String,
        onSelect: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { expanded = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(getCurrencyName(selected), fontSize = 16.sp, color = Color.Black)
                    Text(" ▼", fontSize = 12.sp, color = Color.Gray)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(getCurrencyName(currency)) },
                        onClick = { onSelect(currency); expanded = false }
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Source amount
        Text(
            text = "${getCurrencySymbol(sourceCurrency)} $amount",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        // Source currency selector
        CurrencySelector(selected = sourceCurrency, onSelect = onSourceCurrencyChange)

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(1.dp)
                .background(Color.LightGray)
        )

        // Target amount
        Text(
            text = "${getCurrencySymbol(targetCurrency)} ${String.format("%.2f", converted)}",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        // Target currency selector
        CurrencySelector(selected = targetCurrency, onSelect = onTargetCurrencyChange)

        // Rate info
        Spacer(modifier = Modifier.height(8.dp))

        // Online indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
                    .background(
                        if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isOnline) "实时汇率" else "离线汇率",
                fontSize = 12.sp,
                color = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }

        Text(
            text = "1 $sourceCurrency = ${String.format("%.4f", rate)} $targetCurrency",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "已更新 $updateTimeStr",
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                fontSize = 12.sp,
                color = Color(0xFFE53935)
            )
        }

        // Loading indicator
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color(0xFF00897B),
                strokeWidth = 2.dp
            )
        }

        TextButton(onClick = { fetchRates() }) {
            Text("更新汇率", fontSize = 14.sp, color = Color(0xFF00897B))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Number pad
        val padButtons = listOf(
            listOf("CE", "⌫", ""),
            listOf("7", "8", "9"),
            listOf("4", "5", "6"),
            listOf("1", "2", "3"),
            listOf("", "0", ".")
        )

        padButtons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    if (label.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val bgColor = when (label) {
                            "CE", "⌫" -> Color(0xFFE53935)
                            else -> Color(0xFFF5F5F5)
                        }
                        val textColor = when (label) {
                            "CE", "⌫" -> Color.White
                            else -> Color.Black
                        }
                        Button(
                            onClick = {
                                when (label) {
                                    "CE" -> onAmountChange("0")
                                    "⌫" -> onAmountChange(if (amount.length > 1) amount.dropLast(1) else "0")
                                    "." -> {
                                        if (!amount.contains(".")) onAmountChange(amount + ".")
                                    }
                                    else -> {
                                        if (amount == "0") onAmountChange(label) else onAmountChange(amount + label)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bgColor,
                                contentColor = textColor
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = if (label.length > 1) 16.sp else 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelector(label: String, date: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年M月d日") }
    Column {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        TextButton(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    onDateSelected(LocalDate.of(year, month + 1, day))
                },
                date.year, date.monthValue - 1, date.dayOfMonth
            ).show()
        }) {
            Text(date.format(dateFormatter), fontSize = 18.sp, color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorContent(
    dateMode: DateCalcMode,
    fromDate: LocalDate,
    toDate: LocalDate,
    daysInput: String,
    onDateModeChange: (DateCalcMode) -> Unit,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
    onDaysInputChange: (String) -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年M月d日") }
    var expanded by remember { mutableStateOf(false) }

    fun calculateDateResult(): String {
        return when (dateMode) {
            DateCalcMode.DIFFERENCE -> {
                val days = ChronoUnit.DAYS.between(fromDate, toDate)
                when {
                    days == 0L -> "相同日期"
                    days == 1L || days == -1L -> "$days 天"
                    else -> {
                        val period = Period.between(fromDate, toDate)
                        val parts = mutableListOf<String>()
                        if (period.years != 0) parts.add("${period.years} 年")
                        if (period.months != 0) parts.add("${period.months} 个月")
                        if (period.days != 0) parts.add("${period.days} 天")
                        if (parts.isEmpty()) "$days 天" else "${parts.joinToString(" ")}（共 $days 天）"
                    }
                }
            }
            DateCalcMode.ADD_SUBTRACT -> {
                val days = daysInput.toLongOrNull() ?: 0
                val resultDate = fromDate.plusDays(days)
                resultDate.format(dateFormatter)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Mode selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = when (dateMode) {
                    DateCalcMode.DIFFERENCE -> "日期之间的相隔时间"
                    DateCalcMode.ADD_SUBTRACT -> "加上或减去天数"
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("日期之间的相隔时间") },
                    onClick = { onDateModeChange(DateCalcMode.DIFFERENCE); expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("加上或减去天数") },
                    onClick = { onDateModeChange(DateCalcMode.ADD_SUBTRACT); expanded = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // From date
        DateSelector("From", fromDate, onFromDateChange)

        Spacer(modifier = Modifier.height(24.dp))

        // To date or days input
        if (dateMode == DateCalcMode.DIFFERENCE) {
            DateSelector("To", toDate, onToDateChange)
        } else {
            Text("加上或减去", fontSize = 14.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = daysInput,
                    onValueChange = {
                        if (it.isEmpty() || it == "-" || it.matches(Regex("-?\\d+"))) {
                            onDaysInputChange(it)
                        }
                    },
                    modifier = Modifier.width(140.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("天", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Result
        Text(
            text = when (dateMode) {
                DateCalcMode.DIFFERENCE -> "间隔天数"
                DateCalcMode.ADD_SUBTRACT -> "结果日期"
            },
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = calculateDateResult(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    HelloWorldTheme {
        CalculatorApp()
    }
}
