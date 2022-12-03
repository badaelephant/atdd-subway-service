package nextstep.subway.auth.acceptance;

import static nextstep.subway.member.MemberAcceptanceTest.회원_생성을_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.ErrorMessage;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.line.dto.ErrorResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.member.MemberAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AuthAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        회원_생성을_요청(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD, MemberAcceptanceTest.AGE);
    }

    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        ExtractableResponse<Response> 로그인_결과 = 로그인_요청(new TokenRequest("email@email.com", "password"));
        assertThat(로그인_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(로그인_결과.as(TokenResponse.class).getAccessToken()).isNotNull();
    }

    @DisplayName("Bearer Auth 로그인 실패")
    @Test
    void myInfoWithBadBearerAuth() {
        ExtractableResponse<Response> 로그인_결과 = 로그인_요청(new TokenRequest("email@email.com", "password2"));
        assertThat(로그인_결과.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(로그인_결과.as(ErrorResponse.class).getMessage()).isEqualTo(ErrorMessage.PASSWORD_NOT_MATCH);
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {
        ExtractableResponse<Response> 로그인_결과 = 로그인_요청(new TokenRequest("email@email.com", "password"));
        assertThat(로그인_결과.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(로그인_결과.as(ErrorResponse.class).getMessage()).isEqualTo(ErrorMessage.INVALID_TOKEN);
    }

    public static String 로그인_되어서_토큰_반환함(){
        회원_생성을_요청(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD, MemberAcceptanceTest.AGE);
        ExtractableResponse<Response> response = 로그인_요청(new TokenRequest(MemberAcceptanceTest.EMAIL, MemberAcceptanceTest.PASSWORD));
        String token = response.as(TokenResponse.class).getAccessToken();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(token).isNotNull();
        return token;
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
