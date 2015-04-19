package us.mcmagic.magicassistant.ridemanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 4/1/15
 */
public class Train {
    private List<Cart> carts = new ArrayList<>();

    public Train(Cart cart) {
        carts.add(cart);
    }

    public Train(List<Cart> carts) {
        this.carts.clear();
        for (Cart cart : carts) {
            this.carts.add(cart);
        }
    }

    public List<Cart> getCarts() {
        return new ArrayList<>(carts);
    }

    public void despawn() {
        for (Cart c : carts) {
            c.die();
        }
    }

    public void addCart(Cart cart) {
        carts.add(cart);
    }

    public void removeCart(Cart cart) {
        carts.remove(cart);
    }
}
