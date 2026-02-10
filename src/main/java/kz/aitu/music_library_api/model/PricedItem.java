package kz.aitu.music_library_api.model;

public interface PricedItem {

    double getPrice();

    void setPrice(double price);

    double calculateTotalPrice(int quantity);

    default double applyDiscount(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        return getPrice() * (1 - discountPercent / 100.0);
    }

    static int comparePrice(PricedItem item1, PricedItem item2) {
        return Double.compare(item1.getPrice(), item2.getPrice());
    }
}