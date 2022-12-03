package nextstep.subway.favorite.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Embeddable
public class Favorites {

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Favorite> favorites = new ArrayList<>();

    public void remove(Favorite favorite) {
        favorites.remove(favorite);
    }
    public void add(Favorite favorite){
        favorites.add(favorite);
    }

    public List<Favorite> getAll() {
        return favorites;
    }
}
