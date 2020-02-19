package exercise.controller;

import exercise.model.Order;
import exercise.model.OrderId;
import exercise.model.OrdersSummary;

import java.util.UUID;

/**
 * Live Order board to track and summmarize Orders in the Marketplace
 */
public interface LiveOrderBoard
{
    /**
     * Threadsafe method to place new order into Marketplace
     * @param o
     * @return true if the order already existed (and has not changed)
     * false if the order was newly added
     */
    boolean register(Order o);


    /**
     * remove order from the marketplace
     * @param id
     * @return boolean indicating whether order was cancelled or not
     */
    boolean cancel(OrderId id);

    /**
     * @return summary of netted buy and sell orders in the marketplace right now
     */
    OrdersSummary summarize();
}
