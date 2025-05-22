package controlledEntities.modeled.pets;

public enum PetType {
    MAPACHE, MINERO, SAKURA, JOJO;

    public static PetType fromString(String input) {
        if (input == null) return null;
        try {
            return PetType.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
