package com.booking.football_pitch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.football_pitch.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Tìm quyền theo mã (VD: "USER", "ADMIN")
    Optional<Role> findByCode(String code);
}
