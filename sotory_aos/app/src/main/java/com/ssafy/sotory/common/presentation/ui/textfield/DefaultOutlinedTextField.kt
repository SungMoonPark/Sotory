package com.ssafy.sotory.common.presentation.ui.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.ui.theme.Black200
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.WhiteTextColor
import java.text.DecimalFormat

@Composable
fun DefaultOutlinedTextField(
    onValueChange: (TextFieldValue) -> Unit,
    value: TextFieldValue,
    label: String? = null,
    placeholder: String,
    limits: Int? = null,
    singleLine: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {


    val shape: Shape = RoundedCornerShape(8.dp)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = WhiteTextColor,
        unfocusedBorderColor = WhiteTextColor,
        focusedTextColor = WhiteTextColor,
        unfocusedTextColor = WhiteTextColor,
        cursorColor = WhiteTextColor
    )
    val supportingText: @Composable (() -> Unit)? = if (limits == null) null else {
        {
            Text(
                text = "${value.text.length} / $limits 자",
                style = Body_L_Medium.copy(color = Black200),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    Column {
        if (label != null) Text(
            label,
            modifier = Modifier.padding(bottom = 8.dp),
            style = Body_L_Medium.copy(color = WhiteTextColor)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .heightIn(min = if (singleLine) 48.dp else 100.dp, max = 200.dp),
            value = value,
            onValueChange = onValueChange,
            textStyle = Body_L_Medium,
            placeholder = {
                Text(placeholder, style = Body_L_Medium.copy(color = Black200))
            },
            singleLine = singleLine,
            shape = shape,
            colors = colors,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            supportingText = supportingText
        )
    }


}


class CurrencyVisualTransformation : VisualTransformation {
    private val decimalFormatter = DecimalFormat("#,###")

    override fun filter(text: AnnotatedString): TransformedText {
        // 입력된 텍스트가 비어있으면 그대로 반환
        if (text.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // 숫자 부분만 추출
        val digits = text.text.filter { it.isDigit() }

        // 숫자가 없으면 그대로 반환
        if (digits.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // 금액 포맷팅 (1000 -> "1,000")
        val formattedAmount = try {
            decimalFormatter.format(digits.toLong())
        } catch (e: Exception) {
            digits // 변환에 실패하면 원래 숫자 그대로 사용
        }

        // "원" 추가
        val formattedText = "$formattedAmount 원"
        val resultText = AnnotatedString(formattedText)

        // 오프셋 매핑 정의 - 원본 텍스트 위치와 변환된 텍스트 위치의 관계 정의
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // 원본 텍스트에서 오프셋을 계산할 때 콤마 추가를 고려
                if (offset == 0) return 0

                // 원본 텍스트에서 위치의 숫자 부분에 해당하는 변환된 텍스트에서의 위치 계산
                val numCommas = formattedAmount.count { it == ',' }
                val digitsBeforeOffset = digits.substring(0, minOf(offset, digits.length)).length

                // 커서 위치 앞에 있는 콤마 개수 계산
                val commasBeforeOffset = if (digitsBeforeOffset > 0) {
                    val digitsPart = digits.substring(0, digitsBeforeOffset).toLong()
                    decimalFormatter.format(digitsPart).count { it == ',' }
                } else 0

                return minOf(digitsBeforeOffset + commasBeforeOffset, formattedAmount.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // 변환된 텍스트에서 오프셋의 원본 텍스트 위치 계산
                if (offset <= 0) return 0
                if (offset >= formattedText.length) return digits.length

                // 변환된 텍스트에서 오프셋 앞의 콤마 개수 계산
                val commasBefore = formattedText.substring(0, offset).count { it == ',' }

                // 원본 텍스트에서의 위치는 변환된 텍스트 위치에서 콤마 수를 뺀 값
                return minOf(offset - commasBefore, digits.length)
            }
        }

        return TransformedText(resultText, offsetMapping)
    }
}
