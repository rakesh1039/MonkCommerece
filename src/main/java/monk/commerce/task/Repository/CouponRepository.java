package monk.commerce.task.Repository;

import monk.commerce.task.Coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository <Coupon, Long>{
}
