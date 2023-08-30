package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Long> {
    Set<Profession> findAllByIdNotIn(List<Long> professionIds);

    boolean existsProfessionByName(String profession);
}
