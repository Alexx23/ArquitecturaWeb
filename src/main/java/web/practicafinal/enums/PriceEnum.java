package web.practicafinal.enums;

import java.math.BigDecimal;

/**
 *
 * @author Alex
 */
public enum PriceEnum {
    NORMAL(1, 6.9f);
    
    private final int id;
    private final float price;

    private PriceEnum(int id, float price) {
        this.id = id;
        this.price = price;
    }

    public int getId() {
        return id;
    }
    
    public float getPrice() {
        return price;
    }
}
