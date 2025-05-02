import android.content.Context
import android.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.random.Random


object CardSizeUtil {
    data class CardSize(val width: Dp, val height: Dp)

    // 화면 크기에 따라 카드 사이즈 계산
    fun getMonthlyCardSize(screenWidth: Float, screenHeight: Float): CardSize {
        return CardSize(
            width = (screenWidth * 0.39f).dp,
            height = (screenHeight * 0.312f).dp
        )
    }

    fun getDailyCardSize(screenWidth: Float, screenHeight: Float): CardSize {
        return CardSize(
            width = (screenWidth * 0.6f).dp,
            height = (screenHeight * 0.48f).dp
        )
    }

    fun getDetailCardSize(screenWidth: Float): CardSize {
        return CardSize(
            width = (screenWidth * 2 / 3).dp,
            height = ((screenWidth * 2 / 3) * 1.7f).dp
        )
    }
}


/**
 * 인덱스에 따라 다른 범위의 랜덤 Float 값을 생성하는 함수
 * - 홀수 인덱스: 1f에서 20f 사이의 랜덤 값
 * - 짝수 인덱스: -1f에서 -20f 사이의 랜덤 값
 *
 * @param count 생성할 랜덤 값의 개수
 * @return 랜덤 Float 값의 리스트
 */
fun generateRandomFloatsByParity(count: Int): List<Float> {
    val random = Random.Default
    return List(count) { index ->
        if (index % 2 == 1) {
            // 홀수 인덱스: 1f ~ 20f
            1f + random.nextFloat() * 19f
        } else {
            // 짝수 인덱스: -1f ~ -20f
            -1f - random.nextFloat() * 19f
        }
    }
}

/**
 * 카드 정보를 담는 데이터 클래스
 */
data class Card(
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float,
    val rotation: Float = 0f,  // 회전 각도 (도)
)

/**
 * 카드를 이미지처럼 겹쳐 배치하는 함수
 *
 * @param cardWidth 카드 너비
 * @param cardHeight 카드 높이
 * @param firstCardX 첫 번째 카드의 X 좌표
 * @param firstCardY 첫 번째 카드의 Y 좌표
 * @param cardCount 총 카드 수
 * @param angleIncrement 카드 간 각도 증분 (이미지에서는 약 5도)
 * @param xOffset 카드 간 X축 오프셋 (양수: 오른쪽으로 이동)
 * @return 각 카드의 정보 목록
 */
fun calculateStackedCards(
    cardWidth: Float,
    cardHeight: Float,
    firstCardX: Float,
    firstCardY: Float,
    cardCount: Int,
    angleIncrement: Float = 5f,
    xOffset: Float = cardWidth * 0.1f,  // 카드 너비의 10% 정도 오프셋
): List<Card> {
    val cards = mutableListOf<Card>()

    // 첫 번째 카드는 기본 정보로 추가
    cards.add(Card(cardWidth, cardHeight, firstCardX, firstCardY, 0f))

    // 기준 위치 설정 (다음 카드부터는 이전 카드 대비 위치)
    var lastX = firstCardX
    var lastY = firstCardY
    var lastRotation = 0f

    for (i in 1 until cardCount) {
        // 현재 카드의 회전 각도
        val rotation = lastRotation + angleIncrement

        // 오프셋 계산 (X축)
        val currentXOffset = xOffset

        // 새 카드 위치 계산
        val newX = lastX + currentXOffset
        val newY = lastY

        // 카드 추가
        cards.add(Card(cardWidth, cardHeight, newX, newY, rotation))

        // 현재 카드 정보를 다음 카드의 기준으로 업데이트
        lastX = newX
        lastY = newY
        lastRotation = rotation
    }

    return cards
}

/**
 * 이미지와 더 유사한 배치를 위한 함수
 */
fun calculatePreciseStackedCards(
    cardWidth: Float,
    cardHeight: Float,
    firstCardX: Float,
    firstCardY: Float,
    cardCount: Int,
    angleIncrement: Float = 5f,
): List<Card> {
    val cards = mutableListOf<Card>()

    // 이미지와 유사한 효과를 위한 오프셋 조정
    val xOffset = cardWidth * 0.08f  // 카드 너비의 8% 정도
    val yOffset = cardHeight * 0.01f  // 카드 높이의 1% 정도 (미세한 Y축 이동)

    // 첫 번째 카드 추가
    cards.add(Card(cardWidth, cardHeight, firstCardX, firstCardY, 0f))

    for (i in 1 until cardCount) {
        val prevCard = cards[i - 1]

        // 회전 각도
        val rotation = prevCard.rotation + angleIncrement

        // 간단한 오프셋 적용 (겹침 효과를 위해)
        val newX = prevCard.x + xOffset
        val newY = prevCard.y + yOffset

        cards.add(Card(cardWidth, cardHeight, newX, newY, rotation))
    }

    return cards
}

/**
 * 이미지에 보이는 것처럼 카드가 3D 공간에서 회전하는 듯한 효과를 구현하는 함수
 */
fun calculate3DStackedCards(
    cardWidth: Float,
    cardHeight: Float,
    firstCardX: Float,
    firstCardY: Float,
    cardCount: Int,
    angleIncrement: Float = 5f,
): List<Card> {
    val cards = mutableListOf<Card>()

    // 3D 효과를 위한 설정
    val xOffsetPerCard = cardWidth * 0.08f
    val zDepthEffect = 0.015f  // 깊이에 따른 Y축 이동 효과

    // 첫 번째 카드 추가
    cards.add(Card(cardWidth, cardHeight, firstCardX, firstCardY, 0f))

    for (i in 1 until cardCount) {
        val rotation = i * angleIncrement

        // 회전에 따른 X, Y 오프셋 계산 (3D 효과 시뮬레이션)
        val radians = rotation * PI.toFloat() / 180f
        val depthEffect = i * zDepthEffect * cardHeight
        val newX = firstCardX + i * xOffsetPerCard

        // Y 오프셋은 회전 각도가 커질수록 미세하게 감소 (3D 효과)
        val newY = firstCardY - depthEffect * sin(radians)

        cards.add(Card(cardWidth, cardHeight, newX, newY, rotation))
    }

    return cards
}

/**
 * 안드로이드에서 사용할 경우 예시 코드 (주석 처리됨)
 */

fun setupStackedCardsInAndroid(context: Context): List<Card> {
    // 화면 크기 구하기
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // 카드 크기 설정 (화면 크기에 맞게 조정)
    val cardWidth = screenWidth * 0.7f
    val cardHeight = cardWidth * 1.5f  // 일반적인 카드 비율

    // 첫 번째 카드 위치 (화면 중앙에 배치)
    val firstCardX = (screenWidth - cardWidth) / 2
    val firstCardY = (screenHeight - cardHeight) / 2

    // 카드 배치 계산
    return calculate3DStackedCards(
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        firstCardX = firstCardX,
        firstCardY = firstCardY,
        cardCount = 3,  // 이미지처럼 3장
        angleIncrement = 5f
    )
}

// 이미지와 유사한 카드 색상 반환
private fun getCardColor(index: Int): Int {
    return when (index) {
        0 -> Color.parseColor("#D6F0FF")  // 맨 앞 카드 (하늘색)
        1 -> Color.parseColor("#D6F0FF")  // 중간 카드 (하늘색)
        2 -> Color.parseColor("#D6F0FF")  // 뒤 카드 (하늘색)
        else -> Color.WHITE
    }
}

// 이미지와 유사한 테두리 색상 반환
private fun getCardBorderColor(index: Int): Int {
    return when (index) {
        0 -> Color.parseColor("#B090E0")  // 보라색 테두리
        1 -> Color.BLACK                   // 검은색 테두리
        2 -> Color.RED                     // 빨간색 테두리
        else -> Color.GRAY
    }
}


