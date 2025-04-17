package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairCenterRepo extends JpaRepository<RepairCenter, Long> {

    Optional<RepairCenter> findRepairCentersByCompanyNameIsLikeIgnoreCase(String companyName);

    List<RepairCenter> findRepairCentersByPartner(Partner partner);

    @Query(value = "SELECT r.*, " +
            "  (6371 * acos( " +
            "       cos(radians(:lat)) * cos(radians(c.lat)) * cos(radians(c.lon) - radians(:lon)) + " +
            "       sin(radians(:lat)) * sin(radians(c.lat))" +
            "  )) AS distance " +
            "FROM repair_center r " +
            "JOIN cap c ON r.cap = c.cap " +
            "ORDER BY distance ASC",
            nativeQuery = true)
    List<RepairCenter> findRepairCentersOrderedByDistance(@Param("lat") Double lat,
                                                          @Param("lon") Double lon);

}
