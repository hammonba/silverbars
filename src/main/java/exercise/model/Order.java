package exercise.model;

import java.math.BigDecimal;

/**
 * Models an individual order for silver bars
 */
public class Order
{
    private final BuySell buysell;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private final Object userId;
    private final OrderId id;

    public static Order createIdOnly(OrderId id)
    {
        return new Order(id);
    }

    public static Order create(
            BuySell buysell,
            BigDecimal price,
            BigDecimal quantity,
            Object userId)
    {
        return new Order(buysell, price, quantity, userId);
    }

    private Order(OrderId id)
    {
        this.buysell = null;
        this.price = null;
        this.quantity = null;
        this.userId = null;
        this.id = id;
    }

    /**
     * We expect out big decimals to be positive, rational AND NOT NULL
     * @param bd
     */
    static void checkBigDecimal(String desc, BigDecimal bd)
    {
        if (bd == null) throw new IllegalArgumentException(desc + " may not be null");
        if (bd.signum() < 1) throw new IllegalArgumentException(desc + " must be positive");
    }

    public Order(
            BuySell buysell,
            BigDecimal price,
            BigDecimal quantity,
            Object userId)
    {
        checkBigDecimal("price", price);
        checkBigDecimal("quantity", quantity);
        if (buysell == null) throw new IllegalArgumentException("BuySell must be specified");

        this.buysell = buysell;
        this.price = price;
        this.quantity = quantity;
        this.userId = userId;
        this.id = OrderId.createNew();
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

    public Object getUserId()
    {
        return userId;
    }

    public OrderId getId()
    {
        return id;
    }

}
