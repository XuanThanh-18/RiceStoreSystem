package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="store")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Store extends Model {
    @Column(name = "store_name")
    private String storeName;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "status", columnDefinition = "VARCHAR(30) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
}
