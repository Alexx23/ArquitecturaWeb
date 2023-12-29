package web.practicafinal.enums;

/**
 *
 * @author Alex
 */
public enum RoleEnum {
    CLIENT(1), ADMIN(2);
    
    private final int id;

    private RoleEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
