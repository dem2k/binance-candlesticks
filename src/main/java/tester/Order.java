package tester;

import java.util.Objects;

public class Order {
    
    private double price;
    private OrderType side;

    public Order(double price, OrderType side) {
        this.price = price;
        this.side = side;
    }

    public double price() {
        return price;
    }

    public OrderType side() {
        return side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order order)) {
            return false;
        }
        return Double.compare(order.price, price) == 0 && side == order.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, side);
    }

    @Override
    public String toString() {
        return "tester.Order{" +
                "price=" + price +
                ", side=" + side +
                '}';
    }
}
