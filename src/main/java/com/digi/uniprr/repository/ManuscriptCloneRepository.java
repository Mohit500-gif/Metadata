package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digi.uniprr.utils.ManuscriptClone;

import java.util.List;

import org.springframework.stereotype.Repository;


@Repository
public interface ManuscriptCloneRepository extends JpaRepository<ManuscriptClone, Long> {
    List<ManuscriptClone> findBySyncWithManuscriptFalse();
}

