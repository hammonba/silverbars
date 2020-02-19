package exercise.model;

import java.util.UUID;

/**
 * Abstraction of an OrderId.
 * Currently delegates to UUID
 */
public class OrderId implements Comparable<OrderId>
{
    private final UUID uuid;

    protected OrderId(UUID uuid)
    {
        this.uuid = uuid;
    }

    public static OrderId createNew()
    {
        return new OrderId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        OrderId orderId = (OrderId) o;

        return uuid.equals(orderId.uuid);
    }

    @Override
    public int hashCode()
    {
        return uuid.hashCode();
    }

    @Override
    public int compareTo(OrderId o)
    {
        return uuid.compareTo(o.uuid);
    }

}
