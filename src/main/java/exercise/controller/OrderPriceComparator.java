package exercise.controller;

import exercise.model.Order;

import java.util.Comparator;

/**
 * Compare Orders by price, then by unique id
 */
public class OrderPriceComparator implements Comparator<Order>
{
    @Override
    public int compare(Order o1, Order o2)
    {
        int icmp = 0;
        if (icmp == 0) icmp = o1.getPrice().compareTo(o2.getPrice());
        if (icmp == 0) icmp = o1.getId().compareTo(o2.getId());
        return icmp;
    }
}
