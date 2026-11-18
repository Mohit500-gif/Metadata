package com.digi.uniprr.serviceImpl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.ManuscriptAuthorCountEntity;
import com.digi.uniprr.repository.ManuscriptAuthorCountRepository;
import com.digi.uniprr.service.ManuscriptAuthorCountService;

@Service
public class ManuscriptAuthorCountServiceImpl implements ManuscriptAuthorCountService {

    @Autowired
    private ManuscriptAuthorCountRepository repository;

    @Transactional
    public void saveOrUpdateAuthorCount(Long manuscriptId, Integer authorCount) {

        ManuscriptAuthorCountEntity entity =
            repository.findByManuscriptId(manuscriptId)
                .orElse(new ManuscriptAuthorCountEntity());

        entity.setManuscriptId(manuscriptId);
        entity.setAuthorCount(authorCount);

        repository.save(entity);
    }

    public Integer getAuthorCount(Long manuscriptId) {
        return repository.findByManuscriptId(manuscriptId)
                .map(ManuscriptAuthorCountEntity::getAuthorCount)
                .orElse(0);
    }
}

