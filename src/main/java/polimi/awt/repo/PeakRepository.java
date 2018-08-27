package polimi.awt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import polimi.awt.model.Campaign;
import polimi.awt.model.Peak;

//@RepositoryRestResource(collectionResourceRel = "peaks", path = "peaks")
public interface PeakRepository extends PagingAndSortingRepository<Peak, Long> {
    Page<Peak> findAll(Pageable pageable);
    Peak findPeakByIdEquals(Long id);
    Page<Peak> findPeakByCampaign(Campaign campaign, Pageable pageable);
    Page<Peak> findPeakByCampaignAndAltitudeIsNotNullOrderByAltitudeDesc(Campaign campaign, Pageable pageable);
    Long countPeakByCampaign(Campaign campaign);
    Long countPeakByCampaignAndColorLike(Campaign campaign, String color);
    Long countPeakByCampaignAndColor(Campaign campaign, String color);
    Long countPeakByCampaignAndConflicts(Campaign campaign, Boolean bool);
    Page<Peak> findPeakByCampaignAndColor(Campaign campaign, String color, Pageable pageable);
    Page<Peak> findPeakByCampaignAndConflicts(Campaign campaign, Boolean bool, Pageable pageable);

}