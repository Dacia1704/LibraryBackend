package com.example.library.repository;

import com.example.library.entity.RolePermission;
import com.example.library.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    void deleteAllByRoleId(Long roleId);
}
