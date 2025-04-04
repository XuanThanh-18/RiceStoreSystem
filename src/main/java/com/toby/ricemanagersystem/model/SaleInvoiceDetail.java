package com.toby.ricemanagersystem.model;

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
@Table(name="sale_invoice_detail")
public class SaleInvoiceDetail extends Model {
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "sale_invoice_id", referencedColumnName = "id", nullable = false)
    private SaleInvoice saleInvoice;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "unit_price") //đơn gía
    private Double unitPrice;
    @Column(name = "total_price") // tong tien cua mot san pham = quantity * unit price
    private Double totalPrice;
    @Column(name = "product_details_at_time_of_buy", columnDefinition = "json")
    private String productDetailsAtTimeOfBuy;
}
