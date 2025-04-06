package controlledEntities.modeled.pets;

public enum PetType {
    MAPACHE, MINERO, SAKURA;

    public static PetType fromString(String input) {
        if (input == null) return null;
        try {
            return PetType.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // O lanza una excepción personalizada si prefieres
        }
    }
}
