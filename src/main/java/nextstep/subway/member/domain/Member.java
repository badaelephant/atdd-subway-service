package nextstep.subway.member.domain;

import javax.persistence.Embedded;
import nextstep.subway.BaseEntity;
import nextstep.subway.ErrorMessage;
import nextstep.subway.auth.application.AuthorizationException;
import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.Favorites;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private Integer age;

    @Embedded
    private Favorites favorites;

    public Member() {
    }

    public Member(String email, String password, Integer age) {
        this.email = email;
        this.password = password;
        this.age = age;
        this.favorites = new Favorites();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }

    public void update(Member member) {
        this.email = member.email;
        this.password = member.password;
        this.age = member.age;
    }

    public void checkPassword(String password) {
        if (!StringUtils.equals(this.password, password)) {
            throw new AuthorizationException(ErrorMessage.PASSWORD_NOT_MATCH);
        }
    }

    public void removeFavorite(Favorite favorite) {
        favorites.remove(favorite);
    }

    public Favorites getFavorites() {
        return favorites;
    }

    public Double getDiscountRate() {
        if (this.age>=13 && this.age<19){
            return 0.2;
        }
        if(this.age>=6 && this.age<13){
            return 0.5;
        }
    }
}
