package exercise.controller;

import exercise.model.Order;
import exercise.model.OrderId;
import exercise.model.OrderPriceSummary;
import exercise.model.OrdersSummary;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LiveOrderBoardImpl implements LiveOrderBoard
{
    private final Collection<Order> orders = new ConcurrentSkipListSet<>(new OrderIdComparator());

    @Override
    public boolean register(Order o)
    {
        return orders.add(o);
    }

    @Override
    public boolean cancel(OrderId id)
    {
        return orders.remove(Order.createIdOnly(id));
    }

    /**
     * group by price
     * accumulate buyquantities and sell quantities
     * return collection of buy and sell quantities
     */
    private final Collector<Order, ?, Collection<OrderPriceSummary>>
            summaryCollector =
            Collectors.collectingAndThen(
                    Collectors.groupingBy(Order::getPrice,
                                          OrderAccumulator.SINGLETON_COLLECTOR),
                    Map::values);

    protected Collection<OrderPriceSummary> summarizeByPrice()
    {
        return orders.stream().collect(summaryCollector);
    }

    public static boolean notNull (Object obj)
    {
        return obj != null;
    }

    /**
     * takes the summmarized-by-price collection
     * seperates into overall-buys and overall-sells
     * applies different sorting to buys vs sells
     *
     * @return overallbuys and overall sells as seperate ordered collections
     */
    @Override
    public OrdersSummary summarize()
    {
        Map<Boolean, List<OrderPriceSummary>> ss =
                summarizeByPrice()
                        .stream()
                        .filter(LiveOrderBoardImpl::notNull)
                        .collect(Collectors.partitioningBy(OrderPriceSummary::isBuy));

        List<OrderPriceSummary> buys = ss.get(Boolean.TRUE);
        List<OrderPriceSummary> sells = ss.get(Boolean.FALSE);

        Comparator<OrderPriceSummary> cmp = new OrderPriceSummary.ComparatorByPrice();
        Collections.sort(buys, cmp.reversed());
        Collections.sort(sells, cmp);

        return new OrdersSummary(buys, sells);
    }

}
