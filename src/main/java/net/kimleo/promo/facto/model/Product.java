package net.kimleo.promo.facto.model;

import lombok.Data;


@Data
public class Product {
  String name;
  String id;
  double price;

  int amount;

  double totalAmount;
}
