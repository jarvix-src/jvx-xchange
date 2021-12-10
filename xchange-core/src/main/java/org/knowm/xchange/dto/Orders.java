package org.knowm.xchange.dto;

import static org.knowm.xchange.dto.Orders.OrderSortType.SortByID;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** DTO representing a collection of orders */
public class Orders implements Serializable {

  private static final long serialVersionUID = 5790082783307641329L;

  private static final OrderIDComparator TRADE_ID_COMPARATOR = new OrderIDComparator();
  private static final OrderTimestampComparator TRADE_TIMESTAMP_COMPARATOR =
      new OrderTimestampComparator();

  private final List<Order> orders;
  private final long lastID;
  private final String nextPageCursor;
  private final OrderSortType orderSortType;

  /**
   * Constructor Default sort is SortByID
   *
   * @param orders List of orders
   */
  public Orders(List<Order> orders) {

    this(orders, 0L, SortByID);
  }

  /**
   * Constructor
   *
   * @param orders List of orders
   * @param orderSortType Order sort type
   */
  public Orders(List<Order> orders, OrderSortType orderSortType) {

    this(orders, 0L, orderSortType);
  }

  /**
   * Constructor
   *
   * @param orders A list of orders
   * @param lastID Last Unique ID
   * @param orderSortType Order sort type
   */
  public Orders(List<Order> orders, long lastID, OrderSortType orderSortType) {
    this(orders, lastID, orderSortType, null);
  }

  /**
   * Constructor
   *
   * @param orders A list of orders
   * @param lastID Last Unique ID
   * @param orderSortType Order sort type
   * @param nextPageCursor a marker that lets you receive the next page of orders using
   *     OrderHistoryParamNextPageCursor
   */
  public Orders(
      List<Order> orders, long lastID, OrderSortType orderSortType, String nextPageCursor) {

    this.orders = new ArrayList<>(orders);
    this.lastID = lastID;
    this.orderSortType = orderSortType;
    this.nextPageCursor = nextPageCursor;

    switch (orderSortType) {
      case SortByTimestamp:
        Collections.sort(this.orders, TRADE_TIMESTAMP_COMPARATOR);
        break;
      case SortByID:
        Collections.sort(this.orders, TRADE_ID_COMPARATOR);
        break;

      default:
        break;
    }
  }

  /** @return A list of orders ordered by id */
  public List<Order> getOrders() {

    return orders;
  }

  /** @return a Unique ID for the fetched orders */
  public long getlastID() {

    return lastID;
  }

  public OrderSortType getOrderSortType() {

    return orderSortType;
  }

  public String getNextPageCursor() {
    return nextPageCursor;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder("Orders\n");
    sb.append("lastID= ").append(lastID).append("\n");

    for (Order order : getOrders()) {
      sb.append("[order=").append(order.toString()).append("]\n");
    }
    sb.append("nextPageCursor= ").append(nextPageCursor).append("\n");
    return sb.toString();
  }

  public enum OrderSortType {
    SortByTimestamp,
    SortByID
  }

  public static class OrderTimestampComparator implements Comparator<Order> {

    @Override
    public int compare(Order order1, Order order2) {

      return order1.getTimestamp().compareTo(order2.getTimestamp());
    }
  }

  public static class OrderIDComparator implements Comparator<Order> {

    private static final int[] ALLOWED_RADIXES = {10, 16};

    @Override
    public int compare(Order order1, Order order2) {
      for (int radix : ALLOWED_RADIXES) {
        try {
          BigInteger id1 = new BigInteger(order1.getId(), radix);
          BigInteger id2 = new BigInteger(order2.getId(), radix);
          return id1.compareTo(id2);
        } catch (NumberFormatException ignored) {
        }
      }
      return order1.getId().compareTo(order2.getId());
    }
  }
}
