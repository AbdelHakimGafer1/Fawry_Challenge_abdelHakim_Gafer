import java.time.LocalDate;
import java.util.*;

// واجهة الشحن
interface Shippable {
    String getName();
    double getWeight(); // بالغرام
}

// الكلاس الأساسي للمنتجات
abstract class Product {
    protected String name;
    protected double price;
    protected int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void reduceQuantity(int amount) {
        if (amount <= quantity) quantity -= amount;
    }

    public abstract boolean isExpired();
    public abstract boolean requiresShipping();
}

// الجبنة
class Cheese extends Product implements Shippable {
    private double weight;
    private LocalDate expiryDate;

    public Cheese(String name, double price, int quantity, double weight, LocalDate expiryDate) {
        super(name, price, quantity);
        this.weight = weight;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean requiresShipping() { return true; }
    public double getWeight() { return weight; }
}

// التلفزيون
class TV extends Product implements Shippable {
    private double weight;

    public TV(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    public boolean isExpired() { return false; }
    public boolean requiresShipping() { return true; }
    public double getWeight() { return weight; }
}

// كروت الشحن
class ScratchCard extends Product {
    public ScratchCard(String name, double price, int quantity) {
        super(name, price, quantity);
    }

    public boolean isExpired() { return false; }
    public boolean requiresShipping() { return false; }
}

// البسكويت (بينتهي صلاحية و بيتشحن)
class Biscuits extends Product implements Shippable {
    private double weight;
    private LocalDate expiryDate;

    public Biscuits(String name, double price, int quantity, double weight, LocalDate expiryDate) {
        super(name, price, quantity);
        this.weight = weight;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean requiresShipping() { return true; }
    public double getWeight() { return weight; }
}

// العنصر داخل الكارت
class CartItem {
    Product product;
    int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}

// الكارت نفسه
class Cart {
    List<CartItem> items = new ArrayList<>();

    public void add(Product product, int quantity) {
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock.");
        }
        items.add(new CartItem(product, quantity));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public List<Shippable> getShippableItems() {
        List<Shippable> shippables = new ArrayList<>();
        for (CartItem item : items) {
            if (item.product instanceof Shippable) {
                for (int i = 0; i < item.quantity; i++) {
                    shippables.add((Shippable) item.product);
                }
            }
        }
        return shippables;
    }
}

// العميل
class Customer {
    private String name;
    private double balance;

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public boolean canAfford(double amount) {
        return balance >= amount;
    }

    public void pay(double amount) {
        if (canAfford(amount)) {
            balance -= amount;
        } else {
            throw new IllegalStateException("Insufficient balance.");
        }
    }

    public double getBalance() {
        return balance;
    }
}

// خدمة الشحن
class ShippingService {
    public static void ship(List<Shippable> items) {
        System.out.println("\n- * Shipment notice **");

        double totalWeight = 0;
        Map<String, Integer> itemCount = new LinkedHashMap<>();

        for (Shippable item : items) {
            totalWeight += item.getWeight();
            itemCount.put(item.getName(), itemCount.getOrDefault(item.getName(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
            System.out.println(entry.getValue() + "x " + entry.getKey());
        }

        System.out.printf("Total package weight %.1fkg\n", totalWeight / 1000.0);
    }
}

// الدفع والطباعة
class Checkout {
    public static void process(Customer customer, Cart cart) {
        if (cart.isEmpty()) throw new IllegalStateException("Cart is empty.");

        for (CartItem item : cart.getItems()) {
            if (item.product.isExpired()) {
                throw new IllegalStateException(item.product.getName() + " is expired.");
            }
            if (item.quantity > item.product.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for " + item.product.getName());
            }
        }

        double subtotal = cart.getSubtotal();
        double shipping = cart.getShippableItems().isEmpty() ? 0 : 30;
        double total = subtotal + shipping;

        if (!customer.canAfford(total)) throw new IllegalStateException("Insufficient balance.");

        // الشحن
        if (shipping > 0) {
            ShippingService.ship(cart.getShippableItems());
        }

        // خصم الكمية
        for (CartItem item : cart.getItems()) {
            item.product.reduceQuantity(item.quantity);
        }

        customer.pay(total);

        // الفاتورة
        System.out.println("\n- * Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.quantity + "x " + item.product.getName() + "    " + item.getTotalPrice());
        }

        System.out.println("-----------------------");
        System.out.println("Subtotal        " + subtotal);
        System.out.println("Shipping        " + shipping);
        System.out.println("Amount Paid     " + total);
        System.out.println("Balance Left    " + customer.getBalance());
    }
}

// الكلاس الرئيسي لتشغيل كل حاجة
public class Fawry_Challenge_abdelHakim_Gafer {
    public static void main(String[] args) {
        Customer customer = new Customer("Hakim", 600);

        Cheese cheese = new Cheese("Cheese", 100, 5, 200, LocalDate.of(2025, 7, 20));
        TV tv = new TV("TV", 150, 3, 5000);
        ScratchCard scratch = new ScratchCard("ScratchCard", 50, 10);
        Biscuits biscuits = new Biscuits("Biscuits", 150, 2, 700, LocalDate.of(2025, 7, 20));

        Cart cart = new Cart();
        cart.add(cheese, 2);
        cart.add(tv, 1);
        cart.add(scratch, 1);
        cart.add(biscuits, 1);

        Checkout.process(customer, cart);
    }
}
