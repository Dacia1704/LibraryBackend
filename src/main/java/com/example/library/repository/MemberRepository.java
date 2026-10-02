package com.example.library.repository;

import com.example.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberCode(String memberCode);

    Optional<Member> findByUserId(Long userId);

    Optional<Member> findByIdAndIsDeletedFalse(Long id);

    List<Member> findAllByIsDeletedFalse();
}