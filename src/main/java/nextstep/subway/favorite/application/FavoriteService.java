package nextstep.subway.favorite.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.FavoriteRepository;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.member.application.MemberService;
import nextstep.subway.member.domain.Member;
import nextstep.subway.member.domain.MemberRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FavoriteService {
    private final MemberService memberService;
    private final StationService stationService;
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(StationService stationService, FavoriteRepository favoriteRepository, MemberService memberService) {
        this.stationService = stationService;
        this.favoriteRepository = favoriteRepository;
        this.memberService = memberService;
    }
    @Transactional
    public FavoriteResponse create(Long memberId, FavoriteRequest favoriteRequest) {
        Station source = stationService.findStationById(favoriteRequest.getSource());
        Station target = stationService.findStationById(favoriteRequest.getTarget());
        Member member = memberService.findMemberById(memberId);
        Favorite favorite = favoriteRepository.save(Favorite.of(member, source, target));
        return FavoriteResponse.of(favorite);
    }
    public List<FavoriteResponse> show(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return FavoriteResponse.ofFavorites(member.getFavorites());
    }
    @Transactional
    public void delete(Long memberId, Long favoriteId) {
        Member member = memberService.findMemberById(memberId);
        Favorite favorite = favoriteRepository.findById(favoriteId).orElseThrow(()-> new EntityNotFoundException("Favorite", favoriteId));
        member.removeFavorite(favorite);
    }
}
