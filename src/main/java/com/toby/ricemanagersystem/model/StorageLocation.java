package com.toby.ricemanagersystem.model;

import com.toby.ricemanagersystem.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="storage_location")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class StorageLocation extends Model {
    @Column(name = "location_name")
    private String locationName;
    @Column(name = "description")
    private String description;
    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private Status status;

    @ManyToOne  // mot cua hang co the dat o nhieu vi tri
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne // mot san pham co the dat o nhieu vi tri
    @JoinColumn(name = "product_id")
    private Product product;
}
