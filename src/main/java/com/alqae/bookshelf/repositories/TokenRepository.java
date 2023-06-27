package com.alqae.bookshelf.repositories;

import com.alqae.bookshelf.models.TokenEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String> {
    @Query("SELECT u FROM TokenEntity u WHERE u.token = ?1")
    Optional<TokenEntity> findByToken(String token);
}
