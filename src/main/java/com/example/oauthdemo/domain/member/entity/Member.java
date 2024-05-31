package com.example.oauthdemo.domain.member.entity;

import com.example.oauthdemo.domain.member.constant.MemberType;
import com.example.oauthdemo.domain.member.constant.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private String name;

    private String profileUrl;

    private MemberType memberType;

    private Role role;
}
