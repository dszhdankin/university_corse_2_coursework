package com.code_generator_server.repository;

import com.code_generator_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select user from User user where user.username = ?1")
    User findByUsername(String username);

    @Transactional
    @Modifying
    @Query("update User user set user.layers = ?2 where user.username = ?1")
    void updateLayers(String username, String layers);

    @Transactional
    @Modifying
    @Query("update User user set user.hyperparameters = ?2 where user.username = ?1")
    void updateHyperParameters(String username, String hyperParameters);
}