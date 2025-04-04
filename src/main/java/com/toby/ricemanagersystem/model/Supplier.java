package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supplier")
public class Supplier extends Model { // nhà cung cấp
    @Column(name = "supplier_name")
    private String supplierName;
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "address")
    private String address;
    @Column(name = "note")
    private String note;
    @Column(name = "total_debt") //còn nợ bao tiền
    private Double totalDebt;
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
