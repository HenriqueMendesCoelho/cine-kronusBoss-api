package com.kronusboss.cine.user.adapter.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.kronusboss.cine.user.domain.User;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

	@Transactional(readOnly = true)
	@EntityGraph(attributePaths = { "roles", "statistics" })
	User findByEmail(String email);

	@Transactional(readOnly = true)
	@EntityGraph(attributePaths = { "roles", "statistics" })
	User findByRedefinePasswordKey(String redefinePasswordKey);

}
