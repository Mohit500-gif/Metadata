package com.digi.uniprr.repository;

import com.digi.uniprr.model.UserLoggedInAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenCheckRepo extends JpaRepository<UserLoggedInAccess, Long> {


    @Query(value="select * from t_user_logged_in_access where access_token=:accessToken limit 1",nativeQuery = true)
    UserLoggedInAccess getAccessToken(String accessToken);
}
