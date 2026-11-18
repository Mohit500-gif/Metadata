package com.digi.uniprr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptAuthorCountEntity;

@Repository
public interface ManuscriptAuthorCountRepository
        extends JpaRepository<ManuscriptAuthorCountEntity, Long> {

    Optional<ManuscriptAuthorCountEntity>
        findByManuscriptId(Long manuscriptId);
}

