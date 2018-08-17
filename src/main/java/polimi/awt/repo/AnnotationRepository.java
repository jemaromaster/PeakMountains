package polimi.awt.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import polimi.awt.model.Annotation;
import polimi.awt.model.Peak;
import polimi.awt.model.UserPV;

import java.util.List;

public interface AnnotationRepository extends PagingAndSortingRepository<Annotation, Long> {
    Page<Annotation> findAll(Pageable pageable);
    Annotation findAnnotationById(Long id);
    Page<Annotation> findAnnotationByPeak(Peak peak, Pageable pageable);

    List<Annotation> findAnnotationByPeakAndUserPV(Peak peak, UserPV user);

    Long countAnnotationByPeakAndStatusEquals(Peak peak, String status);
    Long countAnnotationByPeak(Peak peak);

}