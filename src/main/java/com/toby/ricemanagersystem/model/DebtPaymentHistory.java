package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.RecordType;
import com.toby.ricemanagersystem.model.enums.SourceType;
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
@Table(name = "debt_payment_history")
public class DebtPaymentHistory extends Model{
    @ManyToOne
    @JoinColumn(name = "customer_id") // một khách hàng có nhiều lịch sử trả nợ
    private Customer customer;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RecordType type;    // loại hóa đơn

    @Column(name = "source_id")
    private Long sourceId;      // id tài nguyên

    @Column(name = "source_type")
    private SourceType sourceType;      // loại tài nguyên

    @Column(name = "amount")
    private Double amount;
    @Column(name = "record_date", columnDefinition = "DATETIME(0)")
    private LocalDateTime recordDate;
    @Column(name = "note")
    private String note;
}
