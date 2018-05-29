package polimi.awt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import polimi.awt.model.UserPV;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "users", path = "people")
public interface UserRepository extends PagingAndSortingRepository<UserPV, Long> {

	UserPV findByUsername(@Param("username") String name);
	List<UserPV> findByEmail(@Param("email") String email);
	Page<UserPV> findAll(Pageable pageable);

}