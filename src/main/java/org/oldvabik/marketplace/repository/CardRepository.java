package org.oldvabik.marketplace.repository;

import org.oldvabik.marketplace.entity.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardInfo, Long> {
    Optional<CardInfo> findByNumber(String number);

    Page<CardInfo> findAll(Pageable pageable);
}
