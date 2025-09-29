package monk.commerce.task.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import monk.commerce.task.Coupon.Coupon;
import monk.commerce.task.DTO.Cart;
import monk.commerce.task.DTO.CartItem;
import monk.commerce.task.Exception.CouponAPIException;
import monk.commerce.task.Repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponServiceImpl implements CouponService{
    @Autowired
    private CouponRepository couponRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public Object getCoupon() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(()-> new CouponAPIException(HttpStatus.NOT_FOUND.value(), "Coupon not found with id: " + id));
    }

    @Override
    public Coupon updateCoupon(Long id, Coupon coupon) {
        Coupon existingCoupon = getCouponById(id);
        existingCoupon.setType(coupon.getType());
        existingCoupon.setDetails(coupon.getDetails());
        existingCoupon.setExpiryDate(coupon.getExpiryDate());
        couponRepository.save(existingCoupon);
        return existingCoupon;
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon existingCoupon = getCouponById(id);
        couponRepository.deleteById(existingCoupon.getId());
    }

    @Override
    public List<Map<String, Object>> getApplicableCoupons(Cart cart) {
        List<Map<String,Object>> result = new ArrayList<>();

        for (Coupon coupon : couponRepository.findAll()) {
            double discount = calculateDiscount(coupon, cart);
            System.out.println(discount);
            if (discount > 0) {
                Map<String,Object> data = new HashMap<>();
                data.put("coupon_id", coupon.getId());
                data.put("type", coupon.getType());
                data.put("discount", discount);
                result.add(data);
            }
        }
        System.out.println(result);
        return result;
    }

    @Override
    public Map<String, Object> applyCoupon(Long id, Cart cart) {
        Coupon coupon = getCouponById(id);
        double discount = calculateDiscount(coupon, cart);

        Map<String,Object> response = new HashMap<>();
        double totalPrice = cart.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * i.getPrice())
                .sum();

        response.put("updated_cart", cart);
        response.put("total_price", totalPrice);
        response.put("total_discount", discount);
        response.put("final_price", totalPrice - discount);

        return response;
    }

    private double calculateDiscount(Coupon coupon, Cart cart) {
        if (coupon == null) {
            throw new CouponAPIException(HttpStatus.NOT_FOUND.value(), "Coupon not found");
        }
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CouponAPIException(HttpStatus.BAD_REQUEST.value(), "Cart cannot be empty");
        }

        try {
            Map<String,Object> details = coupon.getDetails();

            if (details == null || details.isEmpty()) {
                throw new CouponAPIException(HttpStatus.BAD_REQUEST.value(),
                        "Coupon details missing for coupon id: " + coupon.getId());
            }

            switch (coupon.getType()) {
                case CART_WISE:
                    if (!details.containsKey("threshold") || !details.containsKey("discount")) {
                        throw new CouponAPIException(HttpStatus.BAD_REQUEST.value(),
                                "Cart-wise coupon is missing 'threshold' or 'discount'");
                    }
                    double threshold = ((Number) details.get("threshold")).doubleValue();
                    double discountPercent = ((Number) details.get("discount")).doubleValue();
                    double cartTotal = cart.getItems().stream()
                            .mapToDouble(i -> i.getQuantity() * i.getPrice()).sum();
                    return cartTotal > threshold ? (cartTotal * discountPercent / 100) : 0;

                case PRODUCT_WISE:
                    if (!details.containsKey("product_id") || !details.containsKey("discount")) {
                        throw new CouponAPIException(HttpStatus.BAD_REQUEST.value(),
                                "Product-wise coupon is missing 'product_id' or 'discount'");
                    }
                    Long productId = ((Number) details.get("product_id")).longValue();
                    double productDiscount = ((Number) details.get("discount")).doubleValue();
                    return cart.getItems().stream()
                            .filter(i -> i.getProductId().equals(productId))
                            .mapToDouble(i -> i.getQuantity() * i.getPrice() * productDiscount / 100)
                            .sum();

                case BXGY:
                    if (!details.containsKey("buy_products") ||
                            !details.containsKey("get_products") ||
                            !details.containsKey("repition_limit")) {
                        throw new CouponAPIException(HttpStatus.BAD_REQUEST.value(),
                                "BxGy coupon is missing 'buy_products', 'get_products', or 'repition_limit'");
                    }

                    List<Map<String,Object>> buyProducts = (List<Map<String,Object>>) details.get("buy_products");
                    List<Map<String,Object>> getProducts = (List<Map<String,Object>>) details.get("get_products");
                    int repetitionLimit = (Integer) details.get("repition_limit");

                    // Count how many times buy-condition is satisfied
                    int possibleRepeats = Integer.MAX_VALUE;
                    for (Map<String,Object> buy : buyProducts) {
                        Long pid = ((Number) buy.get("product_id")).longValue();
                        int requiredQty = (Integer) buy.get("quantity");
                        int presentQty = cart.getItems().stream()
                                .filter(i -> i.getProductId().equals(pid))
                                .mapToInt(CartItem::getQuantity)
                                .sum();
                        possibleRepeats = Math.min(possibleRepeats, presentQty / requiredQty);
                    }
                    int timesApplicable = Math.min(possibleRepeats, repetitionLimit);

                    // Calculate discount (free products)
                    double discount = 0;
                    for (Map<String,Object> free : getProducts) {
                        Long freePid = ((Number) free.get("product_id")).longValue();
                        int freeQty = (Integer) free.get("quantity");
                        double price = cart.getItems().stream()
                                .filter(i -> i.getProductId().equals(freePid))
                                .mapToDouble(CartItem::getPrice)
                                .findFirst().orElse(0.0);
                        discount += price * freeQty * timesApplicable;
                    }
                    return discount;

                default:
                    throw new CouponAPIException(HttpStatus.BAD_REQUEST.value(),
                            "Unsupported coupon type: " + coupon.getType());
            }

        } catch (CouponAPIException e) {
            throw e;
        } catch (Exception e) {
            throw new CouponAPIException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to calculate discount: " + e.getMessage());
        }
    }

}
