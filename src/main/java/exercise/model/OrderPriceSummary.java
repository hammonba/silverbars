package exercise.model;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Describes netted order quantity on a price
 */
public class OrderPriceSummary
{
    private final BuySell buysell;
    private final BigDecimal price;
    private final BigDecimal quantity;

    public OrderPriceSummary(
            BuySell buysell,
            BigDecimal price,
            BigDecimal quantity)
    {
        this.buysell = buysell;
        this.price = price;
        this.quantity = quantity;
    }

    public boolean isBuy()
    {
        return getBuysell() == BuySell.Buy;
    }

    public BuySell getBuysell()
    {
        return buysell;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public String summaryString()
    {
        return String.format("%s %.3f kg for £ %.2f",
                             buysell.toString().toUpperCase(),
                             quantity,
                             price);
    }

    public static class ComparatorByPrice implements Comparator<OrderPriceSummary>
    {
        @Override
        public int compare(
                OrderPriceSummary o1,
                OrderPriceSummary o2)
        {
            return o1.getPrice().compareTo(o2.getPrice());
        }
    }
}
