package exercise.controller;

import exercise.model.BuySell;
import exercise.model.Order;
import exercise.model.OrderPriceSummary;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Enables the collecting of buy quantities and sell quantities seperately
 */
public class OrderAccumulator
{
    public final static OrderCollector SINGLETON_COLLECTOR = new OrderCollector();

    private BigDecimal price = null;
    private BigDecimal buyQty = BigDecimal.ZERO;
    private BigDecimal sellQty = BigDecimal.ZERO;

    public static class OrderCollector implements Collector<Order, OrderAccumulator, OrderPriceSummary>
    {
        @Override
        public Supplier<OrderAccumulator> supplier()
        {
            return OrderAccumulator::new;
        }

        /**
         * add the offer quantity into our accumulated quantity
         * @param acc
         * @param o
         */
        public void accumulate(OrderAccumulator acc, Order o)
        {
            if (acc.price == null)
            {
                acc.price = o.getPrice();
            }
            else if (! o.getPrice().equals(acc.price))
            {
                throw new IllegalArgumentException("conflicting price values");
            }

            if (o.getBuysell() == BuySell.Buy)
            {
                acc.buyQty = acc.buyQty.add(o.getQuantity());
            }
            else
            {
                acc.sellQty = acc.sellQty.add(o.getQuantity());
            }
        }

        @Override
        public BiConsumer<OrderAccumulator, Order> accumulator()
        {
            return this::accumulate;
        }

        /**
         * add the accumulators' quantites together
         *
         * @param acc1
         * @param acc2
         * @return
         */
        public OrderAccumulator combine(OrderAccumulator acc1, OrderAccumulator acc2)
        {
            if (! acc1.price.equals(acc2.price))
            {
                throw new IllegalArgumentException("conflicting prices in accumulator");
            }

            acc1.buyQty = acc1.buyQty.add(acc2.buyQty);
            acc1.sellQty = acc1.sellQty.add(acc2.sellQty);

            return acc1;
        }

        @Override
        public BinaryOperator<OrderAccumulator> combiner()
        {
            return this::combine;
        }

        /**
         * build an order price summary out of our accumulation.
         * return null if accumulators net to zero
         *
         * @param acc
         * @return
         */
        public OrderPriceSummary finish(OrderAccumulator acc)
        {
            BigDecimal netQty = acc.buyQty.subtract(acc.sellQty);

            BuySell bs = null;
            switch (netQty.signum())
            {
                case -1:
                    bs = BuySell.Sell;
                    netQty = netQty.abs();
                    break;
                case 0:
                    bs = null;
                    break;
                case 1:
                    bs = BuySell.Buy;
                    break;
            }

            if (bs != null)
                return new OrderPriceSummary(bs, acc.price, netQty);
            else
                return null;
        }

        @Override
        public Function<OrderAccumulator, OrderPriceSummary> finisher()
        {
            return this::finish;
        }

        private final Set<Characteristics> characteristics = Collections.singleton(Characteristics.UNORDERED);

        @Override
        public Set<Characteristics> characteristics()
        {
            return characteristics;
        }
    }

}
