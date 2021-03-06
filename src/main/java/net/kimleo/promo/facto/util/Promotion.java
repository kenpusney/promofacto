package net.kimleo.promo.facto.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import net.kimleo.promo.facto.model.Product;

@Data
public class Promotion {

  public enum PromotionScope {
    ORDER, SINGLE, RANGE
  }

  public enum PromotionType {
    DISCOUNT, REDUCTION, FREE_SHIP, GIFT
  }

  PromotionScope scope;
  PromotionType type;

  Map<String, Object> promotionParameters;

  private Promotion(PromotionScope scope, PromotionType type, Map<String, Object> promotionParameters) {
    this.scope = scope;
    this.type = type;
    this.promotionParameters = promotionParameters;
  }

  public static Promotion singleDiscount(String id, double discount) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("PRODUCT_ID", id);
    map.put("DISCOUNT", discount);

    return new Promotion(PromotionScope.SINGLE, PromotionType.DISCOUNT, map);
  }

  public static Promotion orderReduction(double limit, double reduced) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("LIMIT", limit);
    map.put("REDUCED", reduced);

    return new Promotion(PromotionScope.ORDER, PromotionType.REDUCTION, map);
  }

  public static Promotion rangeDiscount(Set<String> products, double discount) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("PROMO_RANGE", products);
    map.put("DISCOUNT", discount);

    return new Promotion(PromotionScope.RANGE, PromotionType.DISCOUNT, map);
  }

  public double calculate(Product product) {
    double originalTotal = product.getAmount() * product.getPrice();
    if (scope == PromotionScope.ORDER) {
      return -1;
    }

    if (scope == PromotionScope.SINGLE) {
      switch (type) {
        case DISCOUNT:
          originalTotal *= (double) promotionParameters.get("DISCOUNT");
          break;
        case REDUCTION:
          if (originalTotal > (double) promotionParameters.get("LIMIT")) {
            originalTotal -= (double) promotionParameters.get("REDUCED");
          }
          break;
        default:
          break;
      }
    }

    if (scope == PromotionScope.RANGE) {
      if (((Set) promotionParameters.get("PROMO_RANGE")).contains(product.getId())) {
        switch (type) {
          case REDUCTION:
            if (originalTotal > (double) promotionParameters.get("LIMIT")) {
              originalTotal -= (double) promotionParameters.get("REDUCED");
            }
            break;
          case GIFT:
          case FREE_SHIP:
            break;
          case DISCOUNT:
            originalTotal *= (double) promotionParameters.get("DISCOUNT");
            break;
        }
      }

    }

    return originalTotal;
  }

  public List<Product> selectProducts(List<Product> products) {
    ArrayList<Product> products1 = new ArrayList<Product>();
    if (scope != PromotionScope.RANGE) {
      return products1;
    }

    for (Product p: products) {
      if (((Set) promotionParameters.get("PROMO_RANGE")).contains(p.getId())) {
        products1.add(p);
      }
    }

    return products1;
  }

  public boolean productIsMatch(String productId) {
    return productId.equals(promotionParameters.get("PRODUCT_ID"));
  }

  public double calculatorForMany(double total) {
    if (scope == PromotionScope.RANGE || scope == PromotionScope.ORDER) {
      switch (type) {
        case REDUCTION:
          if (total > (double) promotionParameters.get("LIMIT")) {
            total -= (double) promotionParameters.get("REDUCED");
          }
          break;
        case GIFT:
        case FREE_SHIP:
          break;
        case DISCOUNT:
          total *= (double) promotionParameters.get("DISCOUNT");
          break;
      }
    }
    return total;
  }
}
