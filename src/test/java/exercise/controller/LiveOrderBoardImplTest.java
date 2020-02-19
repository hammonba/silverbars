package exercise.controller;

import exercise.model.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LiveOrderBoardImplTest
{
    @Test
    public void testSummaryString()
    {
        OrderPriceSummary ops = new OrderPriceSummary(BuySell.Buy, new BigDecimal("3.141592654"), new BigDecimal("922.71828"));
        assertEquals("BUY 922.718 kg for £ 3.14", ops.summaryString());

        ops = new OrderPriceSummary(BuySell.Sell, new BigDecimal("3.141592654"), new BigDecimal("2.71828"));
        assertEquals("SELL 2.718 kg for £ 3.14", ops.summaryString());
    }

    @Test
    public void testRegister()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Buy, BigDecimal.ONE, BigDecimal.TEN, "testUser1"));
        lob.register(Order.create(BuySell.Buy, BigDecimal
                .valueOf(2), BigDecimal.TEN, "testUser2"));
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(2), "testUser3"));

        final OrdersSummary sumry = lob.summarize();

        assertNotNull(sumry);
    }

    protected void testEntry(
            OrderPriceSummary ops,
            long price,
            long qty,
            BuySell buysell)
    {
        assertEquals(BigDecimal.valueOf(price), ops.getPrice());
        assertEquals(BigDecimal.valueOf(qty), ops.getQuantity());
        assertEquals(buysell, ops.getBuysell());
    }

    protected int countIterable(Iterable itb)
    {
        int counter = 0;
        for (Object o : itb) ++counter;
        return counter;
    }

    public void testNone()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        final OrdersSummary sumry = lob.summarize();
        assertFalse(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testSingleBuy()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1"));
        final OrdersSummary sumry = lob.summarize();

        Iterator<OrderPriceSummary> it = sumry.getBuys().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 1000, BuySell.Buy);
        assertFalse(it.hasNext());

        assertFalse(sumry.getSells().iterator().hasNext());

    }

    @Test
    public void testSingleSell()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1000), "testUser1"));
        final OrdersSummary sumry = lob.summarize();

        assertFalse(sumry.getBuys().iterator().hasNext());

        Iterator<OrderPriceSummary> it = sumry.getSells().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 1000, BuySell.Sell);
        assertFalse(it.hasNext());
    }

    @Test
    public void testTwoBuysSamePrice()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1"));
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1234), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        Iterator<OrderPriceSummary> it = sumry.getBuys().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 2234, BuySell.Buy);
        assertFalse(it.hasNext());

        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testTwoBuysDifferentPrice()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1"));
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(3), BigDecimal
                .valueOf(1001), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        Iterator<OrderPriceSummary> it = sumry.getBuys().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 3, 1001, BuySell.Buy);
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 1000, BuySell.Buy);
        assertFalse(it.hasNext());

        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testTwoSellsSamePrice()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1000), "testUser1"));
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1234), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        assertFalse(sumry.getBuys().iterator().hasNext());

        Iterator<OrderPriceSummary> it = sumry.getSells().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 2234, BuySell.Sell);
        assertFalse(it.hasNext());
    }

    @Test
    public void testTwoSellsDifferentPrice()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1000), "testUser1"));
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(3), BigDecimal.valueOf(1234), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        assertFalse(sumry.getBuys().iterator().hasNext());

        Iterator<OrderPriceSummary> it = sumry.getSells().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 1000, BuySell.Sell);
        assertTrue(it.hasNext());
        testEntry(it.next(), 3, 1234, BuySell.Sell);
        assertFalse(it.hasNext());
    }

    @Test
    public void testBuySellSamePriceNetFlat()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1000), "testUser1"));
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        assertFalse(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testBuySellSamePriceNetBuy()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1001), "testUser1"));
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1000), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        Iterator<OrderPriceSummary> it = sumry.getBuys().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 1, BuySell.Buy);

        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testBuySellSamePriceNetSell()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        lob.register(Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1"));
        lob.register(Order.create(BuySell.Sell, BigDecimal
                .valueOf(2), BigDecimal.valueOf(1001), "testUser2"));
        final OrdersSummary sumry = lob.summarize();

        assertFalse(sumry.getBuys().iterator().hasNext());

        Iterator<OrderPriceSummary> it = sumry.getSells().iterator();
        assertTrue(it.hasNext());
        testEntry(it.next(), 2, 1, BuySell.Sell);
    }

    @Test
    public void testNegativeQuantity()
    {
        try
        {
            LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
            lob.register(Order.create(BuySell.Buy, BigDecimal
                    .valueOf(2), BigDecimal.valueOf(-1000), "testUser1"));
            fail("Negative quantity should throw");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testNegativePrice()
    {
        try
        {
            LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
            lob.register(Order.create(BuySell.Buy, BigDecimal
                    .valueOf(-2), BigDecimal.valueOf(1000), "testUser1"));
            fail("Negative price should throw");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testZeroQuantity()
    {
        try
        {
            LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
            lob.register(Order.create(BuySell.Buy, BigDecimal
                    .valueOf(2), new BigDecimal("0"), "testUser1"));
            fail("Zero quantity should throw");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testZeroPrice()
    {
        try
        {
            LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
            lob.register(Order.create(BuySell.Buy, new BigDecimal("0"), BigDecimal
                    .valueOf(2000), "testUser1"));
            fail("Zero price should throw");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testNanQuantity()
    {
        try
        {
            LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
            lob.register(Order.create(BuySell.Buy, BigDecimal
                    .valueOf(2), new BigDecimal(Double.NaN), "testUser1"));
            fail("NaN quantity should throw");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testNanPrice()
    {
        try
        {
            LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
            lob.register(Order.create(BuySell.Buy, BigDecimal
                    .valueOf(Double.NaN), new BigDecimal(10), "testUser1"));
            fail("NaN price should throw");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testSolitaryOrderCancelled()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        Order o = Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1");
        assertTrue(lob.register(o));

        OrdersSummary sumry = lob.summarize();
        assertTrue(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());

        assertTrue(lob.cancel(o.getId()));
        sumry = lob.summarize();
        assertFalse(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testOrderCancelledMultipleTimes()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        Order o = Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1");
        assertTrue(lob.register(o));

        OrdersSummary sumry = lob.summarize();
        assertTrue(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());

        assertTrue(lob.cancel(o.getId()));
        assertFalse(lob.cancel(o.getId()));
        sumry = lob.summarize();
        assertFalse(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void testMultipleOrdersCancelled()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        Order o1 = Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1");
        assertTrue(lob.register(o1));
        Order o2 = Order.create(BuySell.Sell, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1001), "testUser2");
        assertTrue(lob.register(o2));
        Order o3 = Order.create(BuySell.Buy, BigDecimal.valueOf(3), BigDecimal
                .valueOf(1002), "testUser3");
        assertTrue(lob.register(o3));
        Order o4 = Order.create(BuySell.Sell, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1001), "testUser4");
        assertTrue(lob.register(o4));
        Order o5 = Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser5");
        assertTrue(lob.register(o5));

        OrdersSummary sumry = lob.summarize();
        testEntry(sumry.getBuys().iterator().next(), 3, 1002, BuySell.Buy);
        testEntry(sumry.getSells().iterator().next(), 2, 2, BuySell.Sell);

        assertTrue(lob.cancel(o4.getId()));
        sumry = lob.summarize();
        Iterator<OrderPriceSummary> it = sumry.getBuys().iterator();
        testEntry(it.next(), 3, 1002, BuySell.Buy);
        testEntry(it.next(), 2, 999, BuySell.Buy);
        assertFalse(sumry.getSells().iterator().hasNext());

        assertTrue(lob.cancel(o5.getId()));
        sumry = lob.summarize();
        testEntry(sumry.getBuys().iterator().next(), 3, 1002, BuySell.Buy);
        testEntry(sumry.getSells().iterator().next(), 2, 1, BuySell.Sell);

        assertTrue(lob.cancel(o1.getId()));
        sumry = lob.summarize();
        testEntry(sumry.getBuys().iterator().next(), 3, 1002, BuySell.Buy);
        testEntry(sumry.getSells().iterator().next(), 2, 1001, BuySell.Sell);

        assertTrue(lob.cancel(o3.getId()));
        sumry = lob.summarize();
        assertFalse(sumry.getBuys().iterator().hasNext());
        testEntry(sumry.getSells().iterator().next(), 2, 1001, BuySell.Sell);

        assertTrue(lob.cancel(o2.getId()));
        sumry = lob.summarize();
        assertFalse(sumry.getBuys().iterator().hasNext());
        assertFalse(sumry.getSells().iterator().hasNext());
    }

    @Test
    public void generativeTest()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();

        SplittableRandom rnd = new SplittableRandom();
        Set<OrderId> orderIdsExtant = new HashSet<>();

        PrimitiveIterator.OfInt qtys = rnd.ints(1, 10000).iterator();
        PrimitiveIterator.OfInt prices = rnd.ints(1, 100).iterator();
        PrimitiveIterator.OfInt userIds = rnd.ints(1, 10).iterator();

        rnd.ints(1000, 0, 3).forEach(
                a ->
                {
                    switch (a)
                    {
                        case 0:
                            Order o = Order
                                    .create(rnd.nextBoolean() ? BuySell.Buy : BuySell.Sell,
                                            BigDecimal
                                                    .valueOf(prices.nextInt() / 10d),
                                            BigDecimal.valueOf(qtys.nextInt()),
                                            "userId" + userIds.nextInt());
                            orderIdsExtant.add(o.getId());
                            assertTrue(lob.register(o));
                            break;

                        case 1:
                            if (orderIdsExtant.iterator().hasNext())
                            {
                                OrderId oid = orderIdsExtant.iterator().next();
                                orderIdsExtant.remove(oid);
                                assertTrue(lob.cancel(oid));
                            }
                            break;

                        case 2:
                            OrdersSummary summ = lob.summarize();
                            OrderPriceSummary prev = null;
                            for (OrderPriceSummary ops : summ.getBuys())
                            {
                                if (prev != null)
                                {
                                    assertEquals(BuySell.Buy, ops.getBuysell());
                                    assertEquals(1, ops.getQuantity().signum());
                                    assertTrue("Buys should be descending price: "
                                                       + prev.getPrice()
                                                             .toString()
                                                       + "-->"
                                                       + ops.getPrice()
                                                            .toString(),
                                               ops.getPrice()
                                                  .compareTo(prev.getPrice()) < 0);
                                }
                                prev = ops;
                            }

                            prev = null;
                            for (OrderPriceSummary ops : summ.getSells())
                            {
                                if (prev != null)
                                {
                                    assertEquals(BuySell.Sell, ops
                                            .getBuysell());
                                    assertEquals(1, ops.getQuantity().signum());
                                    assertTrue("Sells should be ascending by price"
                                                       + prev.getPrice()
                                                             .toString()
                                                       + "-->"
                                                       + ops.getPrice()
                                                            .toString(),
                                               ops.getPrice()
                                                  .compareTo(prev.getPrice()) > 0);
                                }
                                prev = ops;
                            }
                            break;
                    }
                }
                                    );
        assertTrue(lob.summarize().buildReport().length() > 0);
    }

    /**
     * Make sure that summaries do not get modified by subsequent changes
     */
    @Test
    public void testReportsSurviveSubsequentChanges()
    {
        LiveOrderBoardImpl lob = new LiveOrderBoardImpl();
        Order o1 = Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser1");
        assertTrue(lob.register(o1));
        OrdersSummary os1 = lob.summarize();
        String osr1a = os1.buildReport();

        Order o2 = Order.create(BuySell.Sell, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1001), "testUser2");
        assertTrue(lob.register(o2));

        Order o3 = Order.create(BuySell.Buy, BigDecimal.valueOf(3), BigDecimal
                .valueOf(1002), "testUser3");
        assertTrue(lob.register(o3));

        OrdersSummary os3 = lob.summarize();
        String osr3a = os3.buildReport();

        Order o4 = Order.create(BuySell.Sell, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1001), "testUser4");
        assertTrue(lob.register(o4));
        OrdersSummary os4 = lob.summarize();
        String osr4a = os4.buildReport();

        Order o5 = Order.create(BuySell.Buy, BigDecimal.valueOf(2), BigDecimal
                .valueOf(1000), "testUser5");
        assertTrue(lob.register(o5));
        OrdersSummary os5 = lob.summarize();
        String osr5a = os5.buildReport();

        assertTrue(lob.cancel(o4.getId()));


        assertTrue(lob.cancel(o5.getId()));


        assertTrue(lob.cancel(o1.getId()));


        assertTrue(lob.cancel(o3.getId()));
        OrdersSummary os9 = lob.summarize();
        String osr9a = os9.buildReport();

        assertTrue(lob.cancel(o2.getId()));

        assertEquals(osr1a, os1.buildReport());
        assertEquals(osr5a, os5.buildReport());
        assertEquals(osr9a, os9.buildReport());

    }
}
