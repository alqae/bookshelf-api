package com.alqae.bookshelf.repositories;

import com.alqae.bookshelf.models.ERole;
import com.alqae.bookshelf.models.RoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, String> {
    @Query("SELECT r FROM RoleEntity r WHERE r.name = ?1")
    Optional<RoleEntity> findByName(ERole name);

}
