# 🛒 Fawry Rise Journey Challenge - E-Commerce OOP System

## 👨‍💻 Developed by Abdel Hakim Gafer

This project is a solution to the **Fawry Rise Journey Challenge 2025**, implemented in Java using solid **Object-Oriented Programming (OOP)** principles.

---

## ✅ Features

- Define products with name, price, quantity.
- Products can:
  - Expire (e.g., Cheese, Biscuits)
  - Require shipping (e.g., Cheese, TV)
  - Be virtual (e.g., ScratchCard)
- Add items to cart with quantity validation.
- Full checkout process:
  - Subtotal calculation
  - Shipping cost calculation
  - Final amount deduction from customer's balance
- Detect and report:
  - Empty cart
  - Expired items
  - Insufficient stock
  - Insufficient balance
- Use `ShippingService` that accepts items implementing an interface with:
  ```java
  String getName();
  double getWeight();
