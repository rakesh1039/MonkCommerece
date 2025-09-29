# MonkCommerce
# This project is a Spring Boot and MySQL implementation of a Coupon Management System. It supports multiple coupon types, validates payloads, and applies discounts to shopping carts.

# Features Implemented

* Create, read, update, delete coupons (CRUD APIs).
* Support for three coupon types:
  <br>CART_WISE <br> PRODUCT_WISE <br> BXGY
* Validation for coupon creation, updating, and cart payloads.
* Centralized error handling with clear error messages.

# API Endpoints

Coupon CRUD
* POST /coupons → Create a coupon
* GET /coupons → Get all coupons
* GET /coupons/{id} → Get coupon by ID
* PUT /coupons/{id} → Update coupon
* DELETE /coupons/{id} → Delete coupon

<br>Cart Coupons
* POST /coupons/applicable-coupons → Fetch all applicable coupons for a given cart.
* POST /coupons/apply-coupon/{id} → Apply a specific coupon to a cart and return updated totals.

# Implemented Cases
Cart-wise Coupons
  * Scenario A: 10% off on carts above ₹100
  * Scenario B: 15% off on carts above ₹500
  * Edge Case: Cart total exactly equals the threshold → No discount applied.

Product-wise Coupons
  * Scenario A: 20% off on Product A (id = 1).
  * Scenario B: 50% off on Product C (id = 3).
  * Edge Cases: Cart contains no such product → No discount.
  * Cart contains product multiple times → Discount applies proportionally to total quantity.

BxGy Coupons
  * Scenario A: Buy 2 of Product A + 2 of Product B → Get 1 Product C free.
  * Scenario B: Repetition limit set to 2 → If cart has 4 Product A, 4 Product B, and 2 Product C → 2 Products C are free.
  * Edge Cases:Cart does not satisfy buy condition → No discount.
  * Cart satisfies buy condition but doesn’t have free product in cart → Discount = 0 (since free product not present).
  * Free product present but less than repetition limit → Discount applied only for available items.

# Error Handling
Example Responses

  * Coupon Not Found
    ```json
    {
      "status": "error",
      "code": 404,
      "message": "Coupon not found with id: 99"
    }

  * Invalid Coupon Payload
    ```json
    {
      "status": "error",
      "code": 400,
      "message": "Product-wise coupon must contain 'product_id' and 'discount'"
    }

  * Empty Cart
    ```json
    {
      "status": "error",
      "code": 400,
      "message": "Cart must contain at least one item"
    }
    
  * Invalid Cart Item
    ```json
    {
      "status": "error",
      "code": 400,
      "message": "Validation failed",
      "errors": [
        "quantity: Quantity must be at least 1",
        "price: Price must be greater than 0"
      ]
    }
    
# Validations
  Coupon DTO
  * Type cannot be null.
  * Details cannot be null/empty.
  * Expiry date required.
  * Type-specific detail checks:
      * Cart-wise → must include threshold and discount.
      * Product-wise → must include product_id and discount.
      * BxGy → must include buy_products, get_products, repition_limit.

  Cart DTO
  * Must contain at least one item.
  * Each item must have:
  * productId (not null).
  * quantity >= 1.
  * price > 0.

# Unimplemented / Future Cases
  * Coupon stacking → Combining multiple coupons and applying the best one automatically.
  * Coupon priority rules → Some coupons may override others.
  * Expiry check → Currently expiry date is stored but not enforced.
  * Specific-user → Coupons limited to certain users.

# Limitations
  * No support for best coupon selection
  * Expired coupons are not filtered automatically.











