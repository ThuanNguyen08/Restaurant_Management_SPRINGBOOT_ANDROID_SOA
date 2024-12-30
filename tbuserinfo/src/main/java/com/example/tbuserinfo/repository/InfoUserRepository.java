package com.example.tbuserinfo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tbuserinfo.entities.InfoUser;


@Repository
public interface InfoUserRepository extends JpaRepository<InfoUser, Integer> {
	Optional<InfoUser> findByAccountId(int accountId);

}
