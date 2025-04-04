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
@Table(name = "import_product_invoice_detail")
public class ImportProductInvoiceDetail extends Model {
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;  // một sản phẩm có nhiều hóa đơn nhập

    @ManyToOne
    @JoinColumn(name = "import_product_invoice_id", referencedColumnName = "id", nullable = false)
    private ImportProductInvoice importProductInvoice; // một hóa đơn nhập có nhiều hóa đơn chi tiết

    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "importPrice") //đơn gía
    private Double importPrice;
    @Column(name = "total_price") // tong tien cua mot san pham = quantity * importPrice
    private Double totalPrice;
    @Column(name = "product_details_at_time_of_import", columnDefinition = "json")
    private String productDetailsAtTimeOfImport;
}
