package org.oldvabik.userservice.repository;

import org.oldvabik.userservice.entity.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardInfo, Long> {

    Optional<CardInfo> findByNumber(String number);

    @Query("SELECT c FROM CardInfo c JOIN FETCH c.user WHERE c.id = :id")
    Optional<CardInfo> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT c FROM CardInfo c JOIN FETCH c.user u LEFT JOIN FETCH u.cards WHERE c.id = :id")
    Optional<CardInfo> findByIdWithUserWithCards(@Param("id") Long id);

    Page<CardInfo> findAll(Pageable pageable);
}