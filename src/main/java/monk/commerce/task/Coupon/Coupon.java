package monk.commerce.task.Coupon;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import monk.commerce.task.Model.CouponType;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Coupon type cannot be null")
    @Enumerated(EnumType.STRING)
    private CouponType type;

    @NotNull(message = "Coupon details cannot be null")
    @Convert(converter = JsonToMapConverter.class)
    private Map<String, Object> details;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    public Coupon(Long id, CouponType type, Map<String, Object> details, LocalDate expiryDate) {
        this.id = id;
        this.type = type;
        this.details = details;
        this.expiryDate = expiryDate;
    }

    public Coupon() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
