package web.practicafinal.enums;

/**
 *
 * @author Alex
 */
public enum AgeClassificationEnum {
    
    // Datos obtenidos del ICAA (Instituto de la Cinematografía y de las Artes Audiovisuales)
    APTO(1, "Apto para todos los públicos", (short) 0), 
    SIETE(2, "Mayores de 7 años", (short) 7),
    DOCE(3, "Mayores de 12 años", (short) 12),
    DIECISEIS(4, "Mayores de 16 años", (short) 16),
    DIECIOCHO(5, "Mayores de 18 años", (short) 18),
    PRONOGRAFIA(6, "Pornografía", (short) 18);
    
    private final int id;
    private final String name;
    private final short age;

    private AgeClassificationEnum(int id, String name, short age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public short getAge() {
        return age;
    }
    
}
