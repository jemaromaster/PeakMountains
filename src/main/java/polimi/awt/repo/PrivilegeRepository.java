package polimi.awt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import polimi.awt.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    public Privilege findByName(String name);

}
