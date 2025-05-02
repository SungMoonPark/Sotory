package com.ssafy.sotory.data.diary

import android.util.Log
import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.data.dto.diary.DiarySingleRequest
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.domain.diary.BaseDiaryModel
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.domain.diary.DiaryMonthModel
import com.ssafy.sotory.domain.diary.DiaryRepository
import com.ssafy.sotory.domain.diary.EmptyDiaryModel
import com.ssafy.sotory.domain.diary.OtherDiaryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryService: DiaryService,
) : DiaryRepository {
    // 년도 별로 맵
    // 년도 안에 12개의 빈 리스트
    // 리스트 안에 일별 일기 카드 리스트
    private val _dateDiaries =
        MutableStateFlow<Map<Int, MutableList<List<BaseDiaryModel>>>>(mapOf())
    override val dateDiaries = _dateDiaries.asStateFlow()

    private val _monthDiaries = MutableStateFlow<List<DiaryMonthModel>>(emptyList())
    override val monthDiaries = _monthDiaries.asStateFlow()

    private val _otherDiaries = MutableStateFlow(OtherDiaryModel(mutableListOf()))
    override val otherDiaries = _otherDiaries.asStateFlow()


    override fun postDiary(): Flow<DiaryModel> {
        return flow {
            val response = diaryService.postDiary().data?.toDomain() ?: throw Exception(
                EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    override fun patchDiary(
        paymentDiaryId: String,
        request: DiaryPaymentUpdateRequest,
    ): Flow<Unit> {
        return flow {
            val response = diaryService.patchDiary(paymentDiaryId, request)
            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteDiary(paymentDiaryId: String): Flow<Unit> {
        return flow {
            val response = diaryService.deleteDiary(paymentDiaryId)
            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchDateDiaries(year: Int, month: Int): Flow<List<DiaryModel>> {
        return flow {
            val response = diaryService.getDiaries(year, month)
            val diaryModels = response.data?.map { it.toDomain() } ?: emptyList()

            _dateDiaries.update { currentMap ->
                // 현재 Map의 복사본을 생성
                val updatedMap = currentMap.toMutableMap()

//                val yearData = MutableList<List<BaseDiaryModel>>(12) { emptyList() }
//
                updatedMap[year] = updatedMap[year] ?: MutableList(12) { emptyList() }

                updatedMap[year]!![month - 1] = diaryModels
                updatedMap
            }

            val dateSize = _dateDiaries.value[year]!![month - 1].size
            Log.d("fetchDateDiaries", "dateSize = $dateSize")


            emit(diaryModels)
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchMonthDiaries(year: Int): Flow<List<DiaryMonthModel>> {
        return flow {
            val response = diaryService.getMonthDiaries(year)
            val diaryMonthModels = response.data?.map { it.toDomain() } ?: emptyList()
            // 여기서 일별 일기 카드 리스트 새로 바꿔치기
            _monthDiaries.value = diaryMonthModels




            if (!_dateDiaries.value.containsKey(year)) {
                // 새로운 연도 데이터 생성
                _dateDiaries.update { currentMap ->
                    val yearData = MutableList<List<BaseDiaryModel>>(12) { emptyList() }

                    // 각 월별 데이터 설정
                    for (i in 0..11) {
                        val monthDiaries = diaryMonthModels[i]
                        val emptyCardCount = monthDiaries.count - monthDiaries.diaries.size
                        val emptyCards = List(emptyCardCount) { EmptyDiaryModel() }
                        val existCards = monthDiaries.diaries

                        yearData[i] = emptyCards + existCards
                    }

                    // 새 연도 데이터를 맵에 추가
                    currentMap + (year to yearData)
                }
            } else {

            }

            emit(diaryMonthModels)
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchOtherDiaries(): Flow<OtherDiaryModel> {
        return flow {
            val response = diaryService.getOtherDiaries()
            val diaryModel = response.toDomain()
            _otherDiaries.update { diaryModel }
            emit(diaryModel)
        }.flowOn(Dispatchers.IO)
    }

    override fun getSingleDiary(request: DiarySingleRequest): Flow<DiaryModel> {
        return flow {
            val response = diaryService.getSingleDiary(
                year = request.year,
                month = request.month,
                day = request.day,
            )
            val diaryModel =
                response.data?.toDomain() ?: throw Exception(EXCEPTION_NETWORK_ERROR_MESSAGE)
            emit(diaryModel)
        }.flowOn(Dispatchers.IO)
    }


    // 캐시된 데이터를 관찰하기 위한 메서드 추가
    override fun observeMonthDiaries(): Flow<List<DiaryMonthModel>> {
        return monthDiaries
    }

    override fun observeOtherDiaries(): Flow<OtherDiaryModel> {
        return otherDiaries
    }

    override fun observeDateDiaries(): Flow<Map<Int, List<List<BaseDiaryModel>>>> {
        return dateDiaries
    }

    companion object {
        private const val EXCEPTION_NETWORK_ERROR_MESSAGE =
            "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
    }

}
