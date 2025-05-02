package com.ssafy.sotory.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold

@Composable
fun SettingsTermsScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    val appbarTitle = "약관"

    val termsContent = listOf(
        "제1장 서비스 이용 약관 (Terms of Service)",
        "[제1조 (목적)]\n본 약관은 소비내역 기반 일기 작성 애플리케이션 Sotory(이하 \"서비스\")의 이용과 관련하여 회사와 사용자 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.",
        "[제2조 (서비스 개요)]\nSotory는 사용자가 소비내역을 기록하고 이를 바탕으로 일기를 작성할 수 있도록 지원하는 기능을 제공합니다.",
        "[제3조 (사용자의 책임)]\n1. 사용자는 본인의 소비내역 및 일기 내용을 정확하게 입력해야 하며, 허위 정보 또는 타인 명의로 입력한 데이터에 대한 책임은 사용자에게 있습니다.\n2. 사용자는 다음 각 호의 행위를 하지 않아야 합니다.\n  1) 법령에 위반되는 행위\n  2) 타인의 개인정보를 침해하는 행위\n  3) 서비스의 정상적인 운영을 방해하는 행위\n  4) 서비스의 안정성을 저해하는 행위 (해킹, 시스템 조작, 데이터 위조 등)\n  5) 타인의 계정을 도용하는 행위\n  6) 불법적이거나 음란한 콘텐츠를 게시하는 행위\n  7) 기타 사회질서를 해치는 행위",
        "[제4조 (서비스 변경 및 종료)]\n1. 회사는 서비스의 내용을 변경하거나 운영을 중단할 수 있으며, 이 경우 사용자에게 사전 공지합니다.\n2. 서비스 중단 시 사용자 데이터의 백업 및 관리에 대한 책임은 사용자에게 있습니다.",
        "[제5조 (면책 조항)]\n1. 회사는 서비스의 정확성, 신뢰성, 완전성을 보장하지 않습니다.\n2. 사용자 데이터의 유실, 시스템 장애 등에 대해 회사는 책임을 지지 않습니다.\n3. 서비스 이용 중 불가항력에 발생한 문제에 대해서는 회사는 책임을 지지 않습니다.",
        "제2장 개인정보 처리방침 (Privacy Policy)",
        "[제1조 (개인정보 수집 항목 및 목적)]\n1. 회사는 원활한 서비스 제공을 위해, 이름 소비내역 기반 일기 작성 기능 및 맞춤형 서비스를 제공에 활용합니다.\n2. 수집 항목은 다음과 같습니다.\n  - 계정 정보 (이메일, 닉네임 등)\n  - 소비내역 데이터 (사용자가 직접 입력한 정보)\n  - 작성한 일기 데이터\n  - 앱 사용 로그 및 기기 정보",
        "[제2조 (개인정보 보호 및 삭제)]\n1. 사용자는 언제든지 자신의 데이터를 확인, 수정, 삭제할 수 있습니다.\n2. 사용자가 계정을 삭제할 경우, 모든 개인 데이터는 즉시 또는 보관 법령이 만료된 후 완전 삭제됩니다.\n  - 관련 법령에 따라 일정 기간 동안 데이터를 보관할 수 있습니다.",
        "[제3조 (개인정보 제3자 제공 여부)]\n1. 회사는 사용자의 동의 없이 개인정보를 제3자에게 제공하지 않습니다.",
        "[제4조 (보안 및 책임)]\n1. 단, 법적 요구나 서비스 운용을 위해 제3자(예: 클라우드 제공업체 등)와 최소한의 정보를 공유할 수 있으며, 이 경우 반드시 사용자 동의를 받습니다.\n2. 회사는 사용자의 데이터를 철저히 보호하기 위해 암호화 및 보안 시스템을 적용합니다.\n3. 개인정보 유출 또는 사고 발생 시 즉각 대응하며, 필요한 경우 사용자에게 이를 알립니다.",
        "[제5조 (문의처)]\n서비스 이용 중 개인정보 관련 사항은 아래 연락처로 문의해 주세요.\n이메일: support@sotory.com",
        "본 약관 및 개인정보 처리방침은 [2025년 04월 11일]부터 적용됩니다."
    )

    Scaffold(
        topBar = {
            DefaultAppBar(
                title = appbarTitle,
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Text("Sotory 이용 약관 및 개인정보 처리 방침", style = Heading_M_SemiBold)
            }

            items(termsContent) { section ->
                if (section.startsWith("제") && section.contains("장")) {
                    Text(text = section, style = Heading_S_SemiBold)
                } else {
                    Text(text = section, style = Body_L_Medium)
                }
            }
        }
    }
}
