package nextstep.subway.path.domain.path;

import java.util.List;
import java.util.Objects;
import nextstep.subway.ErrorMessage;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.WeightedMultigraph;

public class KShortestPathFinder implements PathFinder {

    private final KShortestPaths path;

    private final WeightedMultigraph<Station, SectionEdge> graph;

    private static int PATH_COMPUTE_COUNT = 100;
    private static int FASTEST_INDEX = 0;

    private KShortestPathFinder(StationGraph stationGraph) {
        this.graph = stationGraph.getGraph();
        this.path = new KShortestPaths(stationGraph.getGraph(), PATH_COMPUTE_COUNT);
    }

    public static KShortestPathFinder of(StationGraph stationGraph) {
        return new KShortestPathFinder(stationGraph);
    }


    @Override
    public GraphPath findPath(Station source, Station target) {
        return getGraphPath(source, target);
    }

    private GraphPath getGraphPath(Station source, Station target) {
        validatePathFindRequest(source, target);
        List<GraphPath> paths = path.getPaths(source, target);
        GraphPath graphPath = paths.get(FASTEST_INDEX);
        validatePathFindResult(graphPath);
        return graphPath;
    }

    private void validatePathFindResult(GraphPath graphPath) {
        if (Objects.isNull(graphPath)) {
            throw new IllegalStateException(ErrorMessage.FIND_PATH_NOT_CONNECTED);
        }
    }

    private void validatePathFindRequest(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalStateException(ErrorMessage.FIND_PATH_SAME_STATION);
        }
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalStateException(ErrorMessage.FIND_PATH_OF_STATION_NOT_ON_GRAPH);
        }
    }


}