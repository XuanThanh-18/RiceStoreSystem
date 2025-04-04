package com.toby.ricemanagersystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_record")
public class PaymentRecord extends Model{
    @ManyToOne
    @JoinColumn(name = "customer_id") //một khách hàng có nhiều hóa đơn thanh toán
    private Customer customer;
    @Column(name = "payment_amount")
    private Double paymentAmount; // Số tiền được thanh toán
    @Column(name = "record_date", columnDefinition = "DATETIME(0)")
    private LocalDateTime recordDate; //ngay lap phieu
    @Column(name = "note")
    private String note;
}
