package bai1;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Ứng dụng quản lý sản phẩm (console) – chạy trong Eclipse.
 * Đáp ứng:
 * (1) Cập nhật sản phẩm (giá, mô tả, tồn kho...)
 * (2) Liệt kê theo giá / theo danh mục
 * (3) Tổng giá trị tồn kho theo danh mục
 * (4) Giảm giá theo %
 * (5) Đặt hàng: chọn số lượng, trừ kho, tính tổng tiền
 */
public class StoreApp {

    // ========= Entry point =========
    public static void main(String[] args) {
        Inventory inventory = new Inventory();

        // Seed dữ liệu mẫu
        inventory.addProduct(new Product(1001, "iPhone 15", Category.ELECTRONICS,
                bd("22990000"), "Điện thoại Apple", 15));
        inventory.addProduct(new Product(1002, "Samsung Galaxy S24", Category.ELECTRONICS,
                bd("19990000"), "Điện thoại Samsung", 12));
        inventory.addProduct(new Product(2001, "Áo thun Unisex", Category.FASHION,
                bd("199000"), "Cotton 100%", 120));
        inventory.addProduct(new Product(2002, "Quần jeans", Category.FASHION,
                bd("499000"), "Slim fit", 60));
        inventory.addProduct(new Product(3001, "Gạo ST25 5kg", Category.GROCERY,
                bd("189000"), "Đặc sản Sóc Trăng", 200));
        inventory.addProduct(new Product(3002, "Cà phê hạt 1kg", Category.GROCERY,
                bd("159000"), "Robusta rang mộc", 80));

        runMenu(inventory);
    }

    private static BigDecimal bd(String v) {
        return new BigDecimal(v).setScale(0, RoundingMode.HALF_UP);
    }

    // ========= Console menu =========
    private static void runMenu(Inventory inventory) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== QUẢN LÝ SẢN PHẨM CỬA HÀNG =====");
            System.out.println("1) Hiển thị tất cả sản phẩm");
            System.out.println("2) Hiển thị theo GIÁ (tăng/giảm)");
            System.out.println("3) Hiển thị theo DANH MỤC");
            System.out.println("4) Cập nhật thông tin sản phẩm");
            System.out.println("5) Áp dụng GIẢM GIÁ (%) cho sản phẩm");
            System.out.println("6) Tổng GIÁ TRỊ tồn kho theo DANH MỤC");
            System.out.println("7) ĐẶT HÀNG");
            System.out.println("0) Thoát");
            System.out.print("Chọn: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        viewAll(inventory);
                        break;
                    case "2":
                        viewByPrice(sc, inventory);
                        break;
                    case "3":
                        viewByCategory(sc, inventory);
                        break;
                    case "4":
                        updateProductMenu(sc, inventory);
                        break;
                    case "5":
                        discountMenu(sc, inventory);
                        break;
                    case "6":
                        showInventoryValueByCategory(inventory);
                        break;
                    case "7":
                        placeOrderMenu(sc, inventory);
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            } catch (Exception ex) {
                System.out.println("=> Lỗi: " + ex.getMessage());
            }
        }

        sc.close();
        System.out.println("Đã thoát ứng dụng.");
    }

    private static void viewAll(Inventory inventory) {
        printProducts(inventory.listAll());
    }

    private static void viewByPrice(Scanner sc, Inventory inventory) {
        System.out.print("Sắp xếp theo giá (asc/desc): ");
        String order = sc.nextLine().trim().toLowerCase();
        boolean asc = !order.equals("desc");
        printProducts(inventory.listByPrice(asc));
    }

    private static void viewByCategory(Scanner sc, Inventory inventory) {
        System.out.println("Chọn danh mục: " + Arrays.toString(Category.values()));
        System.out.print("Nhập danh mục: ");
        String raw = sc.nextLine().trim().toUpperCase();
        Category cat = Category.valueOf(raw);
        printProducts(inventory.listByCategory(cat));
    }

    private static void updateProductMenu(Scanner sc, Inventory inventory) {
        System.out.print("Nhập Product ID cần cập nhật: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Product p = inventory.getById(id);
        if (p == null) {
            System.out.println("Không tìm thấy sản phẩm ID " + id);
            return;
        }
        System.out.println("Sản phẩm hiện tại: " + p);

        System.out.print("Cập nhật tên (Enter để bỏ qua): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) p.setName(name);

        System.out.print("Cập nhật mô tả (Enter để bỏ qua): ");
        String desc = sc.nextLine().trim();
        if (!desc.isEmpty()) p.setDescription(desc);

        System.out.print("Cập nhật giá (Enter để bỏ qua): ");
        String priceStr = sc.nextLine().trim();
        if (!priceStr.isEmpty()) p.setPrice(new BigDecimal(priceStr));

        System.out.print("Cập nhật tồn kho (Enter để bỏ qua): ");
        String stockStr = sc.nextLine().trim();
        if (!stockStr.isEmpty()) p.setStock(Integer.parseInt(stockStr));

        System.out.print("Cập nhật danh mục ("
                + Arrays.toString(Category.values()) + ", Enter bỏ qua): ");
        String catStr = sc.nextLine().trim();
        if (!catStr.isEmpty()) p.setCategory(Category.valueOf(catStr.toUpperCase()));

        System.out.println("=> ĐÃ CẬP NHẬT: " + p);
    }

    private static void discountMenu(Scanner sc, Inventory inventory) {
        System.out.print("Nhập Product ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Nhập % giảm giá (0–100): ");
        BigDecimal percent = new BigDecimal(sc.nextLine().trim());
        inventory.applyDiscountPercent(id, percent);
        Product p = inventory.getById(id);
        System.out.println("=> ĐÃ ÁP DỤNG GIẢM GIÁ. Giá sau giảm: "
                + currency(p.getPriceAfterDiscount()));
    }

    private static void showInventoryValueByCategory(Inventory inventory) {
        Map<Category, BigDecimal> map = inventory.inventoryValueByCategory();
        System.out.println("\nTỔNG GIÁ TRỊ TỒN KHO THEO DANH MỤC:");
        for (Category c : Category.values()) {
            BigDecimal v = map.getOrDefault(c, BigDecimal.ZERO);
            System.out.printf("- %-12s : %s%n", c.name(), currency(v));
        }
    }

    private static void placeOrderMenu(Scanner sc, Inventory inventory) {
        System.out.println("Nhập danh sách (productId:quantity), ví dụ: 1001:2,2001:3");
        System.out.print("Chuỗi: ");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("Không có mục nào.");
            return;
        }

        Map<Integer, Integer> items = new LinkedHashMap<>();
        for (String token : line.split(",")) {
            String[] kv = token.trim().split(":");
            int id = Integer.parseInt(kv[0].trim());
            int qty = Integer.parseInt(kv[1].trim());
            items.put(id, qty);
        }

        Order order = inventory.placeOrder(items);
        System.out.println("\n===== HÓA ĐƠN =====");
        for (OrderItem it : order.getItems()) {
            System.out.printf("%-6d %-24s x%-3d  | đơn giá: %s  | thành tiền: %s%n",
                    it.getProduct().getId(),
                    truncate(it.getProduct().getName(), 24),
                    it.getQuantity(),
                    currency(it.getUnitPrice()),
                    currency(it.getLineTotal()));
        }
        System.out.println("-----------------------------------------------");
        System.out.println("TỔNG CỘNG: " + currency(order.getTotal()));
        System.out.println("=> Đặt hàng thành công. Tồn kho đã được cập nhật.");
    }

    private static String truncate(String s, int n) {
        if (s.length() <= n) return s;
        return s.substring(0, n - 1) + "…";
    }

    private static void printProducts(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("(Danh sách trống)");
            return;
        }
        // Header: độ rộng cột mô tả 40 ký tự
        System.out.printf("%-6s %-24s %-12s %-12s %-8s %-8s %-40s%n",
                "ID", "Tên", "Danh mục", "Giá", "Giảm(%)", "Tồn", "Mô tả");

        for (Product p : list) {
            System.out.printf("%-6d %-24s %-12s %-12s %-8s %-8d %-40s%n",
                    p.getId(),
                    truncate(p.getName(), 24),
                    p.getCategory().name(),
                    currency(p.getPrice()),
                    p.getDiscountPercent().setScale(0, RoundingMode.HALF_UP) + "%",
                    p.getStock(),
                    truncate(p.getDescription(), 40));
        }
    }

    private static String currency(BigDecimal v) {
        return v.setScale(0, RoundingMode.HALF_UP).toPlainString() + "đ";
    }
}


/* ===================== DƯỚI ĐÂY LÀ CÁC LỚP PHỤ ===================== */

enum Category {
    ELECTRONICS, FASHION, GROCERY, HOME, BEAUTY, OTHER
}

class Product {
    private int id;
    private String name;
    private Category category;
    private BigDecimal price;              // giá gốc
    private String description;
    private int stock;
    private BigDecimal discountPercent;    // 0–100

    public Product(int id, String name, Category category, BigDecimal price, String description, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.discountPercent = BigDecimal.ZERO;
    }

    public BigDecimal getPriceAfterDiscount() {
        BigDecimal factor = BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        if (factor.compareTo(BigDecimal.ZERO) < 0) factor = BigDecimal.ZERO;
        return price.multiply(factor).setScale(0, RoundingMode.HALF_UP);
    }

    // Getters/Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public int getStock() { return stock; }
    public BigDecimal getDiscountPercent() { return discountPercent; }

    public void setName(String name) { this.name = name; }
    public void setCategory(Category category) { this.category = category; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setStock(int stock) { this.stock = stock; }
    public void setDiscountPercent(BigDecimal discountPercent) {
        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải trong khoảng 0–100.");
        }
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', cat=" + category +
                ", price=" + price + ", discount%=" + discountPercent +
                ", stock=" + stock + ", desc='" + description + "'}";
    }
}

class OrderItem {
    private final Product product;
    private final int quantity;
    private final BigDecimal unitPrice;  // đã tính giảm
    private final BigDecimal lineTotal;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPriceAfterDiscount();
        this.lineTotal = unitPrice.multiply(new BigDecimal(quantity));
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
}

class Order {
    private final List<OrderItem> items;
    private final BigDecimal total;

    public Order(List<OrderItem> items) {
        this.items = items;
        this.total = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderItem> getItems() { return items; }
    public BigDecimal getTotal() { return total; }
}

class Inventory {
    private final Map<Integer, Product> products = new LinkedHashMap<>();

    public void addProduct(Product p) {
        if (products.containsKey(p.getId()))
            throw new IllegalArgumentException("Trùng Product ID: " + p.getId());
        products.put(p.getId(), p);
    }

    public Product getById(int id) {
        return products.get(id);
    }

    public List<Product> listAll() {
        return new ArrayList<>(products.values());
    }

    public List<Product> listByPrice(boolean ascending) {
        return products.values().stream()
                .sorted((a, b) -> ascending ? a.getPrice().compareTo(b.getPrice())
                        : b.getPrice().compareTo(a.getPrice()))
                .collect(Collectors.toList());
    }

    public List<Product> listByCategory(Category category) {
        return products.values().stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Map<Category, BigDecimal> inventoryValueByCategory() {
        Map<Category, BigDecimal> map = new EnumMap<>(Category.class);
        for (Product p : products.values()) {
            BigDecimal value = p.getPriceAfterDiscount().multiply(new BigDecimal(p.getStock()));
            map.merge(p.getCategory(), value, BigDecimal::add);
        }
        return map;
    }

    public void applyDiscountPercent(int productId, BigDecimal percent) {
        Product p = getRequired(productId);
        p.setDiscountPercent(percent);
    }

    public Order placeOrder(Map<Integer, Integer> idQty) {
        // Kiểm tra tồn kho trước
        for (Map.Entry<Integer, Integer> e : idQty.entrySet()) {
            Product p = getRequired(e.getKey());
            int qty = e.getValue();
            if (qty <= 0) throw new IllegalArgumentException("Số lượng phải > 0 cho sản phẩm " + p.getId());
            if (p.getStock() < qty) {
                throw new IllegalArgumentException("Không đủ tồn kho cho ID " + p.getId()
                        + " (còn " + p.getStock() + ", yêu cầu " + qty + ")");
            }
        }
        // Trừ kho + tạo OrderItem
        List<OrderItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : idQty.entrySet()) {
            Product p = getRequired(e.getKey());
            int qty = e.getValue();
            p.setStock(p.getStock() - qty);
            items.add(new OrderItem(p, qty));
        }
        return new Order(items);
    }

    private Product getRequired(int id) {
        Product p = getById(id);
        if (p == null) throw new NoSuchElementException("Không tìm thấy sản phẩm ID " + id);
        return p;
    }
}