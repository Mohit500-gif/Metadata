package com.digi.uniprr.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.UserLoginStatus;

@Repository
@Transactional
public interface UserLoginStatusRepo extends JpaRepository<UserLoginStatus, Integer>{

}
