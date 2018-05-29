package polimi.awt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import polimi.awt.model.Campaign;
import polimi.awt.model.UserPV;

//@RepositoryRestResource(collectionResourceRel = "campaigns", path = "campaigns")
public interface CampaignRepository extends PagingAndSortingRepository<Campaign, Long> {
    Page<Campaign> findAll(Pageable pageable);
    Campaign findCampaignById(Long id);
    Page<Campaign> findCampaignByUsrManager(UserPV manager, Pageable pageable);
}