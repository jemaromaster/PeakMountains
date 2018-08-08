package polimi.awt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import polimi.awt.model.Campaign;
import polimi.awt.model.UserPV;

import java.util.List;
import java.util.Set;

//@RepositoryRestResource(collectionResourceRel = "campaigns", path = "campaigns")
public interface CampaignRepository extends PagingAndSortingRepository<Campaign, Long> {
    Page<Campaign> findAll(Pageable pageable);

    Page<Campaign> findCampaignsByStatusEqualsAndWorkersJoinedIsNotContaining(String status, Set<UserPV> userNotContaining, Pageable pageable);
    Campaign findCampaignById(Long id);
    Page<Campaign> findCampaignByUsrManager(UserPV manager, Pageable pageable);
    Page<Campaign> findCampaignByWorkersJoinedOrderById(List<UserPV> workers , Pageable pageable);

//    @Query("SELECT c FROM Campaign c JOIN FETCH Annotation a ON a.campaign.id = c.id WHERE a.id = (:id)")
//    Campaign findCampaignByAnnotationId(@Param("id") Long idAnnotation);
}