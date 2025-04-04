package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user_profile")
public class UserProfile extends Model {
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(TemporalType.DATE) //xác định cách thức lưu trữ và quản lý giá trị kiểu dữ liệu java.util.Date hoặc java.util.Calendar trong cơ sở dữ liệu
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "identity_number", unique = true)
    private String identityNumber;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false) // một user có 1 profile
    private User user;
}
