package com.ssafy.sotory.common.presentation.ui.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.ui.theme.Black200
import com.ssafy.sotory.ui.theme.Body_M_Regular
import com.ssafy.sotory.ui.theme.WhiteTextColor

@Composable // only number
fun DefaultUnderlinedTextNumberField(
    onValueChange: (TextFieldValue) -> Unit,
    textValue: TextFieldValue,
    placeholder: String = "",
    isReadOnly: Boolean = false,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = Body_M_Regular.copy(color = WhiteTextColor)
) {
    val shape: Shape = RoundedCornerShape(8.dp)
    val colors = TextFieldDefaults.colors(
//        focusedBorderColor = WhiteTextColor,
//        unfocusedBorderColor = WhiteTextColor,
//        focusedTextColor = WhiteTextColor,
//        unfocusedTextColor = WhiteTextColor,
//        cursorColor = WhiteTextColor
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedTextColor = WhiteTextColor,
        unfocusedTextColor = WhiteTextColor,
        cursorColor = WhiteTextColor,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

    TextField(
        value = textValue,
        onValueChange = { newValue ->
            // 숫자만 필터링
            val filteredText = newValue.text.filter { it.isDigit() }
            onValueChange(TextFieldValue(filteredText, newValue.selection))
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        readOnly = isReadOnly,
        singleLine = true,
        shape = shape,
        placeholder = { Text(text = placeholder, color = Black200) },
        modifier = modifier.fillMaxWidth(),
        colors = colors,
        textStyle = textStyle
    )
}

@Composable
fun DefaultUnderlinedTextField(
    onValueChange: (TextFieldValue) -> Unit,
    textValue: TextFieldValue,
    placeholder: String = "",
    isReadOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape: Shape = RoundedCornerShape(8.dp)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = WhiteTextColor,
        unfocusedBorderColor = WhiteTextColor,
        focusedTextColor = WhiteTextColor,
        unfocusedTextColor = WhiteTextColor,
        cursorColor = WhiteTextColor
    )

    TextField(
        value = textValue,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        readOnly = isReadOnly,
        singleLine = true,
        shape = shape,
        placeholder = { Text(text = placeholder, color = Black200) },
        modifier = modifier.fillMaxWidth(),
        colors = colors,
        textStyle = Body_M_Regular.copy(color = WhiteTextColor)
    )
}