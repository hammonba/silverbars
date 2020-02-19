package exercise.model;

import java.util.Date;

/**
 * Describes all of the netted-quantities-by-price
 * Overall Buys and Overall Sells are held sepately
 *
 * and should be considered to have useful ordering
 */
public class OrdersSummary
{
    private final Date timeGenerated;
    private final Iterable<OrderPriceSummary> buys;
    private final Iterable<OrderPriceSummary> sells;

    public OrdersSummary(
            Iterable<OrderPriceSummary> buys,
            Iterable<OrderPriceSummary> sells)
    {
        this.buys = buys;
        this.sells = sells;
        timeGenerated = new Date();
    }

    public Iterable<OrderPriceSummary> getBuys()
    {
        return buys;
    }

    public Iterable<OrderPriceSummary> getSells()
    {
        return sells;
    }

    public String buildReport()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Live Order Board Generated at ");
        sb.append(String.format("%tc", timeGenerated));
        sb.append("\r\n\t--- NETTED BUYS --- \r\n");

        for (OrderPriceSummary ops: getBuys())
        {
            sb.append(ops.summaryString());
            sb.append("\r\n");
        }

        sb.append("\r\n\t--- NETTED SELLS --- \r\n");
        for (OrderPriceSummary ops: getSells())
        {
            sb.append(ops.summaryString());
            sb.append("\r\n");
        }

        sb.append("\r\n\t--- END --- \r\n");
        return sb.toString();
    }
}
