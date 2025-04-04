package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.DebtType;
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
@Table(name = "personal_debt_history")
public class PersonalDebtHistory extends Model {
    @ManyToOne
    @JoinColumn(name = "personal_debt_id", nullable = false)
    private PersonalDebt personalDebt;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DebtType type;
    @Column(name = "note")
    private String note;
}
