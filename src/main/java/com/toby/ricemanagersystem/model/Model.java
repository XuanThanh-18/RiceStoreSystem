package com.toby.ricemanagersystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass  // lớp cha (superclass) không phải là một entity riêng lẻ nhưng có thể chia sẻ các thuộc tính cho các entity con.
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at", columnDefinition = "DATETIME(0)")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
//    @PrePersist //đánh dấu một phương thức trong entity sẽ được gọi trước khi entity được lưu vào cơ sở dữ liệu lần đầu tiên.
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
}
