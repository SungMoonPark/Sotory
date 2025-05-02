package com.sotory.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponseDTO {
    private String id;
    private KakaoAccount kakao_account;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private Profile profile;
        private String gender;     // "male", "female"
        private String birthday;   // "MMdd"
        private String email;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;
        }
    }
}