package com.glimps.glimpsserver.perfume.infra;

import com.glimps.glimpsserver.perfume.domain.Perfume;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {

	Optional<Perfume> findByUuid(UUID uuid);

	@Query("select p from Perfume p "
		+ "join fetch p.brand b "
		+ "join fetch p.perfumeNotes pn "
		+ "join fetch pn.note "
		+ "where p.uuid = :uuid")
	Optional<Perfume> findPerfumeWithEntities(UUID uuid);

}
