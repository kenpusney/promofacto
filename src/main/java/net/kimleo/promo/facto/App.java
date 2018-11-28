package net.kimleo.promo.facto;

import static net.kimleo.promo.facto.util.Promotion.orderReduction;
import static net.kimleo.promo.facto.util.Promotion.rangeDiscount;
import static net.kimleo.promo.facto.util.Promotion.singleDiscount;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.kimleo.promo.facto.model.Product;
import net.kimleo.promo.facto.util.Promotion;
import net.kimleo.promo.facto.util.PromotionCalculator;

public class App {

  public static void main(String[] args) {
    System.out.printf("Hello promo facto!");

    List<Product> products = Arrays.asList(
        product("first", "1", 10, 10), // 100
        product("second", "2", 100, 5), // 500
        product("third", "3", 30, 6),   // 180
        product("forth", "4", 47, 9)    // 423
    );

    List<Promotion> promotions = Arrays.asList(
        singleDiscount("1", 0.5),  // -50
        singleDiscount("3", 0.8),  // -36
        orderReduction(300, 100), // -100
        rangeDiscount(setOf("2", "4"), 0.3)  //-646.1
    );

    PromotionCalculator calculator = new PromotionCalculator(products, promotions);

    System.out.println(calculator.calculateTotal()); // 370.9
    System.out.println(calculator.calculatePromoted()); // 832.1
  }



  public static Product product(String name, String id, double price, int amount) {
    Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setPrice(price);
    product.setAmount(amount);
    return product;
  }


  public static <T> Set<T> setOf(T... ts) {
    return Arrays.stream(ts).collect(Collectors.toSet());
  }
}
