package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.Role;
import com.toby.ricemanagersystem.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user")
public class User  extends Model{
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
}
