package monk.commerce.task.Service;

import monk.commerce.task.Coupon.Coupon;
import monk.commerce.task.DTO.Cart;

import java.util.List;
import java.util.Map;

public interface CouponService {
    Coupon createCoupon(Coupon coupon);

    Object getCoupon();

    Coupon getCouponById(Long id);

    Coupon updateCoupon(Long id, Coupon coupon);

    void deleteCoupon(Long id);

    List<Map<String, Object>> getApplicableCoupons(Cart cart);

    Map<String, Object> applyCoupon(Long id, Cart cart);
}
