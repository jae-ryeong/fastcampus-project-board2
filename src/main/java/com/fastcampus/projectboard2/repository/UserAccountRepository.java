package com.fastcampus.projectboard2.repository;

import com.fastcampus.projectboard2.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
