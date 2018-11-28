package net.kimleo.promo.facto.util;

import java.util.ArrayList;
import java.util.List;
import net.kimleo.promo.facto.model.Product;

public class PromotionCalculator {

  List<Product> products;
  List<Promotion> promotions;

  double rangePromoted = 0;

  double orderPromoted = 0;

  public double calculateTotal() {

    double total = 0;

    List<Promotion> productPromotion = new ArrayList<>();
    List<Promotion> rangePromotion = new ArrayList<>();
    List<Promotion> orderPromotions = new ArrayList<>();

    for (Promotion p: promotions) {
      switch(p.getScope()) {
        case SINGLE:
          productPromotion.add(p);
          break;
        case RANGE:
          rangePromotion.add(p);
          break;
        case ORDER:
          orderPromotions.add(p);
          break;
      }
    }

    for (Promotion pPromo : productPromotion) {
      for (Product pProduct : products) {
        if (pPromo.productIsMatch(pProduct.getId())) {
          pProduct.setTotalAmount(pPromo.calculate(pProduct));
        }
      }
    }

    for (Promotion rPromo : rangePromotion) {
      List<Product> rProducts = rPromo.selectProducts(this.products);
      double rTotal = 0;
      for (Product rProduct : rProducts) {
        if (rProduct.getTotalAmount() == 0) {
          rProduct.setTotalAmount(rProduct.getPrice() * rProduct.getAmount());
          rTotal += rProduct.getTotalAmount();
        } else {
          rTotal += rProduct.getTotalAmount();
        }
      }

      rangePromoted += (rTotal - rPromo.calculatorForMany(rTotal));
    }

    for (Promotion oPromo : orderPromotions) {
      double oTotal = 0;
      for (Product oProduct : products) {
        if (oProduct.getAmount() == 0) {
          oProduct.setTotalAmount(oProduct.getPrice() * oProduct.getAmount());
          oTotal += oProduct.getTotalAmount();
        } else {
          oTotal += oProduct.getTotalAmount();
        }
      }

      orderPromoted += (oTotal - oPromo.calculatorForMany(oTotal));
    }

    for (Product product : products) {
      total += product.getTotalAmount();
    }

    return total - rangePromoted - orderPromoted;
  }

  public double calculatePromoted() {
    double singlePromoted = 0;

    for (Product product : products) {
      singlePromoted += (product.getPrice() * product.getAmount() - product.getTotalAmount());
    }


    return singlePromoted + rangePromoted + orderPromoted;
  }
}
