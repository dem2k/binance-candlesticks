package tester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Grid {

    private Utils utils;
    private List<Order> orders;
    private String boFormat, soFormat, prFormat;

    private Grid(List<Order> orders, Utils utils) {
        this.orders = orders;
        this.utils = utils;
        soFormat = "------- %10." + utils.tickSize() + "f";
        prFormat = " :>     %10." + utils.tickSize() + "f";
        boFormat = "+++++++ %10." + utils.tickSize() + "f";
    }

    public static GridBuilder builder(Utils utils, double priceFrom) {
        return new GridBuilder(utils, priceFrom);
    }

    public Order buyOrder(double price) {
        return orders.stream()
                .filter(order -> order.side() == OrderType.BUY && price <= order.price())
                .max(Comparator.comparing(Order::price))
                .orElse(null);
    }

    public Order sellOrder(double price) {
        return orders.stream()
                .filter(order -> order.side() == OrderType.SELL && price >= order.price())
                .min(Comparator.comparing(Order::price))
                .orElse(null);
    }

    public void remove(Order order) {
        orders.remove(order);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> orders() {
        return Collections.unmodifiableList(orders);
    }


    public static class GridBuilder {
        private List<Order> orders = new ArrayList<>();
        private Utils utils;
        private double priceFrom;

        public GridBuilder(Utils utils, double priceFrom) {
            this.utils = utils;
            this.priceFrom = priceFrom;
        }

        public GridBuilder add(double orderPrice) {
            if (orderPrice <= priceFrom) {
                orders.add(new Order(orderPrice, OrderType.BUY));
            }
            if (orderPrice > priceFrom) {
                orders.add(new Order(orderPrice, OrderType.SELL));
            }
            return this;
        }

        public Grid build() {
            return new Grid(orders, utils);
        }
    }

    public String summary(double price) {
        StringBuffer screen = new StringBuffer();
        orders().stream()
                .filter(order -> order.price() > price)
                .sorted(Comparator.comparing(Order::price).reversed())
                .forEach(order -> {
                    screen.append(String.format(soFormat, order.price()))
                            .append(Utils.ERASE_LINE).append("\n");
                });

        screen.append(String.format(prFormat, price))
                .append(Utils.ERASE_LINE).append("\n");

        orders().stream()
                .filter(order -> order.price() <= price)
                .sorted(Comparator.comparing(Order::price).reversed())
                .forEach(order -> {
                    screen.append(String.format(boFormat, order.price()))
                            .append(Utils.ERASE_LINE).append("\n");
                });
        return screen.toString();
    }

    @Override
    public String toString() {
        return "tester.Grid{" +
                "orders=" + orders +
                '}';
    }
}
