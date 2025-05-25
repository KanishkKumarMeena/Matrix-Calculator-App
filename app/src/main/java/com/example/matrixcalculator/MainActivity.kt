package com.example.matrixcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixcalculator.ui.theme.MatrixCalculatorTheme
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    private val matrixOperations = MatrixOperations()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatrixCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MatrixCalculatorApp()
                }
            }
        }
    }

    @Composable
    fun MatrixCalculatorApp() {
        val scrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()
        val operations = listOf("Add", "Subtract", "Multiply", "Divide")
        var selectedOperation by remember { mutableStateOf(operations[0]) }
        
        var matrixARowsValue by remember { mutableStateOf(TextFieldValue("2", TextRange(1))) }
        var matrixAColumnsValue by remember { mutableStateOf(TextFieldValue("2", TextRange(1))) }
        var matrixBRowsValue by remember { mutableStateOf(TextFieldValue("2", TextRange(1))) }
        var matrixBColumnsValue by remember { mutableStateOf(TextFieldValue("2", TextRange(1))) }
        
        // Derived state for actual values
        val matrixARows = matrixARowsValue.text
        val matrixAColumns = matrixAColumnsValue.text
        val matrixBRows = matrixBRowsValue.text
        val matrixBColumns = matrixBColumnsValue.text
        
        var matrixA by remember { mutableStateOf(Array(2) { DoubleArray(2) { 0.0 } }) }
        var matrixB by remember { mutableStateOf(Array(2) { DoubleArray(2) { 0.0 } }) }
        var resultMatrix by remember { mutableStateOf<Array<DoubleArray>?>(null) }
        var errorMessage by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "MATRIX CALCULATOR",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp),
                letterSpacing = 2.sp
            )
            
            // Operation selection
            Card(
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    operations.forEach { operation ->
                        OutlinedButton(
                            onClick = { 
                                selectedOperation = operation 
                                
                                // For division, ensure matrix B is square
                                if (operation == "Divide" && matrixBRows.toInt() != matrixBColumns.toInt()) {
                                    // Make matrix B square by default
                                    val squareDim = matrixBRows
                                    matrixBColumnsValue = TextFieldValue(squareDim, TextRange(squareDim.length))
                                    
                                    matrixB = Array(squareDim.toInt()) { i ->
                                        DoubleArray(squareDim.toInt()) { j ->
                                            if (i < matrixB.size && j < matrixB[0].size) matrixB[i][j] else 0.0
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .height(44.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedOperation == operation) 
                                    MaterialTheme.colorScheme.secondaryContainer
                                else 
                                    MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            if (operation == "Divide") {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_divide),
                                    contentDescription = operation,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                val icon = when(operation) {
                                    "Add" -> Icons.Default.Add
                                    "Subtract" -> Icons.Default.Remove
                                    "Multiply" -> Icons.Default.Close
                                    else -> Icons.Default.Close
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = operation,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                operation.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
            
            // Matrix A dimensions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "MATRIX A",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = matrixARowsValue,
                            onValueChange = { textFieldValue -> 
                                val text = textFieldValue.text
                                if (text.isNotEmpty() && text.toIntOrNull() != null && text.toInt() > 0) {
                                    matrixARowsValue = textFieldValue
                                    val newRowSize = text.toInt()
                                    val newColSize = matrixAColumns.toInt()
                                    matrixA = Array(newRowSize) { i ->
                                        DoubleArray(newColSize) { j ->
                                            if (i < matrixA.size && j < matrixA[0].size) matrixA[i][j] else 0.0
                                        }
                                    }
                                }
                            },
                            label = { Text("Rows") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Text("×", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        
                        OutlinedTextField(
                            value = matrixAColumnsValue,
                            onValueChange = { textFieldValue -> 
                                val text = textFieldValue.text
                                if (text.isNotEmpty() && text.toIntOrNull() != null && text.toInt() > 0) {
                                    matrixAColumnsValue = textFieldValue
                                    val newRowSize = matrixARows.toInt()
                                    val newColSize = text.toInt()
                                    matrixA = Array(newRowSize) { i ->
                                        DoubleArray(newColSize) { j ->
                                            if (i < matrixA.size && j < matrixA[0].size) matrixA[i][j] else 0.0
                                        }
                                    }
                                    
                                    // For multiplication, Matrix B rows must equal Matrix A columns
                                    if (selectedOperation == "Multiply") {
                                        matrixBRowsValue = TextFieldValue(text, TextRange(text.length))
                                        val newBRowSize = text.toInt()
                                        val newBColSize = matrixBColumns.toInt()
                                        matrixB = Array(newBRowSize) { i ->
                                            DoubleArray(newBColSize) { j ->
                                                if (i < matrixB.size && j < matrixB[0].size) matrixB[i][j] else 0.0
                                            }
                                        }
                                    }
                                }
                            },
                            label = { Text("Columns") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    
                    MatrixInputGrid(
                        matrix = matrixA,
                        onValueChange = { row, col, value ->
                            val updatedMatrix = matrixA.clone()
                            updatedMatrix[row][col] = value
                            matrixA = updatedMatrix
                        }
                    )
                }
            }
            
            // Matrix B dimensions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "MATRIX B",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = matrixBRowsValue,
                            onValueChange = { textFieldValue -> 
                                val text = textFieldValue.text
                                if (text.isNotEmpty() && text.toIntOrNull() != null && text.toInt() > 0) {
                                    // For multiplication, Matrix B rows must equal Matrix A columns
                                    if (selectedOperation == "Multiply" && text.toInt() != matrixAColumns.toInt()) {
                                        errorMessage = "For multiplication, Matrix B rows must equal Matrix A columns"
                                        return@OutlinedTextField
                                    }
                                    
                                    // For division, Matrix B must be square
                                    if (selectedOperation == "Divide" && text.toInt() != matrixBColumns.toInt()) {
                                        // Make B square by updating columns too
                                        matrixBColumnsValue = TextFieldValue(text, TextRange(text.length))
                                    }
                                    
                                    matrixBRowsValue = textFieldValue
                                    val newRowSize = text.toInt()
                                    val newColSize = matrixBColumns.toInt()
                                    matrixB = Array(newRowSize) { i ->
                                        DoubleArray(newColSize) { j ->
                                            if (i < matrixB.size && j < matrixB[0].size) matrixB[i][j] else 0.0
                                        }
                                    }
                                    errorMessage = ""
                                }
                            },
                            label = { Text("Rows") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            enabled = selectedOperation != "Multiply" && (selectedOperation != "Divide" || matrixBColumns == matrixBRows),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Text("×", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        
                        OutlinedTextField(
                            value = matrixBColumnsValue,
                            onValueChange = { textFieldValue -> 
                                val text = textFieldValue.text
                                if (text.isNotEmpty() && text.toIntOrNull() != null && text.toInt() > 0) {
                                    // For division, Matrix B must be square
                                    if (selectedOperation == "Divide" && text.toInt() != matrixBRows.toInt()) {
                                        // Make B square by updating rows too
                                        matrixBRowsValue = TextFieldValue(text, TextRange(text.length))
                                    }
                                    
                                    matrixBColumnsValue = textFieldValue
                                    val newRowSize = matrixBRows.toInt()
                                    val newColSize = text.toInt()
                                    matrixB = Array(newRowSize) { i ->
                                        DoubleArray(newColSize) { j ->
                                            if (i < matrixB.size && j < matrixB[0].size) matrixB[i][j] else 0.0
                                        }
                                    }
                                }
                            },
                            label = { Text("Columns") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            enabled = selectedOperation != "Divide" || matrixBColumns == matrixBRows,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    
                    if (selectedOperation == "Divide") {
                        Text(
                            "MUST BE SQUARE & NON-SINGULAR",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    MatrixInputGrid(
                        matrix = matrixB,
                        onValueChange = { row, col, value ->
                            val updatedMatrix = matrixB.clone()
                            updatedMatrix[row][col] = value
                            matrixB = updatedMatrix
                        }
                    )
                }
            }
            
            // Calculate Button
            Button(
                onClick = {
                    try {
                        errorMessage = ""
                        
                        // Input validation based on the operation
                        when (selectedOperation) {
                            "Add", "Subtract" -> {
                                if (matrixA.size != matrixB.size || matrixA[0].size != matrixB[0].size) {
                                    errorMessage = "Matrices must have the same dimensions for $selectedOperation"
                                    return@Button
                                }
                            }
                            "Multiply" -> {
                                if (matrixA[0].size != matrixB.size) {
                                    errorMessage = "For multiplication, columns of Matrix A must equal rows of Matrix B"
                                    return@Button
                                }
                            }
                            "Divide" -> {
                                if (matrixB.size != matrixB[0].size) {
                                    errorMessage = "Matrix B must be square for division"
                                    return@Button
                                }
                                
                                // Check for zero determinant (singular matrix)
                                var hasZeroRow = false
                                var hasZeroColumn = false
                                
                                // Simple check for obvious singular matrices
                                for (i in matrixB.indices) {
                                    var rowSum = 0.0
                                    var colSum = 0.0
                                    for (j in matrixB[i].indices) {
                                        rowSum += abs(matrixB[i][j])
                                        colSum += abs(matrixB[j][i])
                                    }
                                    if (rowSum == 0.0) hasZeroRow = true
                                    if (colSum == 0.0) hasZeroColumn = true
                                }
                                
                                if (hasZeroRow || hasZeroColumn) {
                                    errorMessage = "Matrix B appears to be singular (has zero row/column). Cannot perform division."
                                    return@Button
                                }
                            }
                        }
                        
                        // Perform matrix operation
                        resultMatrix = when (selectedOperation) {
                            "Add" -> matrixOperations.addMatrices(matrixA, matrixB)
                            "Subtract" -> matrixOperations.subtractMatrices(matrixA, matrixB)
                            "Multiply" -> matrixOperations.multiplyMatrices(matrixA, matrixB)
                            "Divide" -> matrixOperations.divideMatrices(matrixA, matrixB)
                            else -> null
                        }
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "An error occurred"
                        if (errorMessage.contains("singular") || errorMessage.contains("inverse")) {
                            errorMessage = "Cannot perform division: Matrix B is singular (not invertible)"
                        }
                        resultMatrix = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    "CALCULATE", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = 3.sp
                )
            }
            
            // Error message
            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = errorMessage.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Result
            if (resultMatrix != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "RESULT MATRIX",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        
                        MatrixResultGrid(matrix = resultMatrix!!)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    fun MatrixInputGrid(
        matrix: Array<DoubleArray>,
        onValueChange: (row: Int, col: Int, value: Double) -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(0.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in matrix.indices) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (j in matrix[i].indices) {
                            OutlinedTextField(
                                value = if (matrix[i][j] == 0.0) "" else matrix[i][j].toString(),
                                onValueChange = { value ->
                                    val doubleValue = value.toDoubleOrNull() ?: 0.0
                                    onValueChange(i, j, doubleValue)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                placeholder = { 
                                    Text(
                                        "0", 
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    ) 
                                },
                                shape = RoundedCornerShape(0.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    focusedTextColor = MaterialTheme.colorScheme.tertiary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                    cursorColor = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MatrixResultGrid(matrix: Array<DoubleArray>) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    RoundedCornerShape(0.dp)
                )
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(0.dp))
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in matrix.indices) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (j in matrix[i].indices) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(0.dp))
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(0.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%.2f", matrix[i][j]),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}