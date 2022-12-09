package nextstep.subway.path.unit.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import nextstep.subway.ErrorMessage;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.path.DijkstraShortestPathFinder;
import nextstep.subway.path.domain.path.KShortestPathFinder;
import nextstep.subway.path.domain.path.PathFinder;
import nextstep.subway.path.domain.path.StationGraph;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathFinderTest {

    private final Line 이호선 = Line.ofNameAndColorAndSurCharge("2호선", "초록색", 400);
    private final Line 신분당선 = Line.ofNameAndColorAndSurCharge("신분당선", "빨간색", 300);
    private final Line 삼호선 = Line.ofNameAndColorAndSurCharge("3호선", "주황색", 200);
    private final Station 강남역 = new Station("강남역");
    private final Station 양재역 = new Station("양재역");
    private final Station 교대역 = new Station("교대역");
    private final Station 남부터미널역 = new Station("남부터미널역");
    private final Station 수원역 = new Station("수원역");
    private final Section 강남역_양재역 = Section.of(신분당선, 강남역, 양재역, 10);
    private final Section 교대역_강남역 = Section.of(이호선, 교대역, 강남역, 10);
    private final Section 교대역_남부터미널역 = Section.of(삼호선, 교대역, 남부터미널역, 7);
    private final Section 남부터미널역_양재역 = Section.of(삼호선, 남부터미널역, 양재역, 5);


    @DisplayName("최단 경로를 구한다.")
    @Test
    void findShortestPath() {
        // given
        StationGraph stationGraph = StationGraph.of(Arrays.asList(강남역_양재역, 교대역_강남역, 교대역_남부터미널역, 남부터미널역_양재역));

        PathFinder pathFinder = DijkstraShortestPathFinder.of(stationGraph);

        // when
        GraphPath graphPath = pathFinder.findPath(강남역, 남부터미널역);

        // then
        assertThat(graphPath.getVertexList()).containsExactly(강남역, 양재역, 남부터미널역);
        assertThat(graphPath.getWeight()).isEqualTo(15);
    }

    @DisplayName("최단 경로를 구할때 두개 역이 같으면 IllegalStateException 발생한다.")
    @Test
    void findShortestPath_exception_same_station() {
        // given
        StationGraph stationGraph = StationGraph.of(Arrays.asList(강남역_양재역, 교대역_강남역, 교대역_남부터미널역, 남부터미널역_양재역));
        PathFinder pathFinder = DijkstraShortestPathFinder.of(stationGraph);

        // then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 강남역)).isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.FIND_PATH_SAME_STATION);
    }

    @DisplayName("최단 경로를 구할때 두개 역중 하나라도 경로에 없으면 IllegalStateException 발생한다.")
    @Test
    void findShortestPath_exception_station_not_on_graph() {
        // given
        StationGraph stationGraph = StationGraph.of(Arrays.asList(강남역_양재역, 교대역_강남역, 교대역_남부터미널역, 남부터미널역_양재역));
        PathFinder pathFinder = DijkstraShortestPathFinder.of(stationGraph);

        // then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 수원역)).isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.FIND_PATH_OF_STATION_NOT_ON_GRAPH);
    }

    @DisplayName("최단 경로를 구할때 두개 역이 연결되어 있지 않으면 IllegalStateException 발생한다.")
    @Test
    void findShortestPath_exception_not_connected() {
        // given
        StationGraph stationGraph = StationGraph.of(Arrays.asList(강남역_양재역, 교대역_남부터미널역));
        PathFinder pathFinder = DijkstraShortestPathFinder.of(stationGraph);
        // then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 남부터미널역)).isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.FIND_PATH_NOT_CONNECTED);
    }


    @DisplayName("경로전략 KShortestPaths 로 바꿔서도 정상동작한다.")
    @Test
    void findPath_newStrategy() {
        // given
        StationGraph stationGraph = StationGraph.of(Arrays.asList(강남역_양재역, 교대역_강남역, 교대역_남부터미널역, 남부터미널역_양재역));
        PathFinder pathFinder = KShortestPathFinder.of(stationGraph);
        // when
        GraphPath graphPath = pathFinder.findPath(강남역, 남부터미널역);
        // then
        assertAll(
                () -> assertThat(graphPath.getVertexList()).containsExactly(강남역, 양재역, 남부터미널역),
                () -> assertThat(graphPath.getWeight()).isEqualTo(15)
        );
    }


}