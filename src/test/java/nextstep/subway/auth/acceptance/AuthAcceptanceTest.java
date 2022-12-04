package nextstep.subway.auth.acceptance;

import static nextstep.subway.member.acceptance.MemberAcceptanceTest.나의_회원_정보_조회_요청;
import static nextstep.subway.member.acceptance.MemberAcceptanceTest.회원_생성을_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.member.acceptance.MemberAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AuthAcceptanceTest extends AcceptanceTest {

    private String EMAIL = "email@email.com";
    private String PASSWORD = "password";
    private int AGE = 10;
    @BeforeEach
    public void setUp() {
        super.setUp();
        회원_생성을_요청(EMAIL, PASSWORD, AGE);
    }

    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        // given
        TokenRequest tokenRequest = new TokenRequest(EMAIL,PASSWORD );
        // when
        ExtractableResponse<Response> 로그인_결과 = 로그인_요청(tokenRequest);
        // then
        로그인이_정상처리됨(로그인_결과);
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {
        // given
        String token = 정상_로그인_토큰_반환(EMAIL,PASSWORD);
        // when
        String wrongToken = token + "wrong";
        // then
        assertThat(나의_회원_정보_조회_요청(wrongToken).statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private void 로그인이_정상처리됨(ExtractableResponse<Response> response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(TokenResponse.class).getAccessToken()).isNotNull();
    }
    public static String 정상_로그인_토큰_반환(String EMAIL, String PASSWORD){
        return 로그인_요청(new TokenRequest(EMAIL, PASSWORD)).as(TokenResponse.class).getAccessToken();
    }

    public static ExtractableResponse<Response> 로그인_요청(TokenRequest tokenRequest) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .extract();
    }

}
