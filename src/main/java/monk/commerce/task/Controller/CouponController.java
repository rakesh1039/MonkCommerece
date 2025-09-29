package monk.commerce.task.Controller;

import jakarta.validation.Valid;
import monk.commerce.task.Coupon.Coupon;
import monk.commerce.task.DTO.Cart;
import monk.commerce.task.Service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping()
    public ResponseEntity<Coupon> create(@Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @GetMapping()
    public List<Coupon> getAllCoupon() {
        return (List<Coupon>) couponService.getCoupon();
    }

    @GetMapping("/{id}")
    public Coupon getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> update(@PathVariable Long id, @Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.updateCoupon(id, coupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Coupon with id " + id +" has been deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/applicable-coupons")
    public Map<String, Object> applicableCoupons(@Valid @RequestBody Cart cart) {
        List<Map<String, Object>> coupons = couponService.getApplicableCoupons(cart);
        return Map.of("applicaable_coupon", coupons);
    }

    @PostMapping("/apply-coupon/{id}")
    public Map<String,Object> apply(@PathVariable Long id,@Valid @RequestBody Cart cart) {
        return couponService.applyCoupon(id, cart);
    }
}
