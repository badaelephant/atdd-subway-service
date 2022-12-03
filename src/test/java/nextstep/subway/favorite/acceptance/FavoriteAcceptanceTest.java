package nextstep.subway.favorite.acceptance;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.로그인_되어서_토큰_반환함;
import static nextstep.subway.line.acceptance.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("즐겨찾기 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    private LineResponse 분당선;
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 오리역;
    private StationResponse 수내역;
    private String accessToken;
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        오리역 = 지하철역_등록되어_있음("오리역").as(StationResponse.class);
        수내역 = 지하철역_등록되어_있음("수내역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음(LineRequest.from("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(LineRequest.from("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10)).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(LineRequest.from("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5)).as(LineResponse.class);
        분당선 = 지하철_노선_등록되어_있음(LineRequest.from("분당선", "bg-yellow-600", 오리역.getId(), 수내역.getId(), 10)).as(LineResponse.class);

        accessToken = 로그인_되어서_토큰_반환함();
    }

    @DisplayName("즐겨찾기 생성")
    @Test
    void create() {
        ExtractableResponse<Response> response = 즐겨찾기_생성(accessToken, new FavoriteRequest(강남역.getId(), 양재역.getId()));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.as(FavoriteResponse.class).getId()).isNotNull();
    }
    @DisplayName("즐겨찾기 목록 조회")
    @Test
    void getAll() {
        즐겨찾기_생성(accessToken, new FavoriteRequest(강남역.getId(), 양재역.getId()));
        즐겨찾기_생성(accessToken, new FavoriteRequest(교대역.getId(), 남부터미널역.getId()));
        ExtractableResponse<Response> response = 즐겨찾기_조회(accessToken);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList(".", FavoriteResponse.class).stream().map(it->it.getSource().getId())).containsExactly(강남역.getId(),교대역.getId());
        assertThat(response.body().jsonPath().getList(".", FavoriteResponse.class).stream().map(it->it.getTarget().getId())).containsExactly(양재역.getId(),남부터미널역.getId());
    }
    @DisplayName("즐겨찾기 삭제")
    @Test
    void delete() {
        Long id = 즐겨찾기_생성(accessToken, new FavoriteRequest(강남역.getId(), 양재역.getId())).as(FavoriteResponse.class).getId();

        ExtractableResponse<Response> response = 즐겨찾기_삭제(accessToken, id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 즐겨찾기_생성(String accessToken, FavoriteRequest favoriteRequest) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(favoriteRequest)
                .auth().oauth2(accessToken)
                .when().post("/favorites")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_조회(String accessToken) {

        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/favorites")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_삭제(String accessToken, Long id) {

        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/favorites/{id}", id)
                .then().log().all()
                .extract();
    }

}