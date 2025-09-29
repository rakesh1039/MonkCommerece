package monk.commerce.task.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class Cart {
    @NotEmpty(message = "Cart must contain at least one item")
    @Valid
    private List<CartItem> items;

    public Cart(List<CartItem> items) {
        this.items = items;
    }

    public Cart() {

    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
