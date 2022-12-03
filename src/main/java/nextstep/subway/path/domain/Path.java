package nextstep.subway.path.domain;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.station.domain.Station;

public class Path {
    private List<Station> stations;
    private Distance distance;

    private int price;

    private Path(List<Station> stations, int distance, int addPrice) {
        this.stations = stations;
        this.distance = Distance.from(distance);
        this.price = findPrice(distance, addPrice);
    }

    private int findPrice(int distance, int addPrice) {
        int price = 1250;
        if(distance>50){
            price+=((distance-50)%8*100)+1000;
        }
        price+=distance%5*100;
        return price+addPrice;
    }

    public static Path of(List<Station> stations, int distance, int addPrice) {
        return new Path(stations, distance, addPrice);
    }

    public List<Station> getStations() {
        return stations;
    }

    public Distance getDistance() {
        return distance;
    }

    public void discount(Double discountRate) {
        price*=(1-discountRate);
    }
}
