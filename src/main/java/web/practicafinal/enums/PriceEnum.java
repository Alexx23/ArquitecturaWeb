package web.practicafinal.enums;

/**
 *
 * @author Alex
 */
public enum PriceEnum {
    NORMAL(1, 7);
    
    private final int id;
    private final int price;

    private PriceEnum(int id, int price) {
        this.id = id;
        this.price = price;
    }

    public int getId() {
        return id;
    }
    
    public int getPrice() {
        return price;
    }
}
