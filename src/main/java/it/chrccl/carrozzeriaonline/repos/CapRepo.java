package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.entities.CAP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CapRepo  extends JpaRepository<CAP, String> {

    // Metodo per recuperare il CAP con valore numerico più vicino
    @Query(value = "SELECT * FROM cap " +
            "ORDER BY ABS(CAST(cap AS SIGNED) - :targetVal) " +
            "LIMIT 1",
            nativeQuery = true)
    CAP findClosestCAPByNumericalOrder(@Param("targetVal") int targetVal);

}
