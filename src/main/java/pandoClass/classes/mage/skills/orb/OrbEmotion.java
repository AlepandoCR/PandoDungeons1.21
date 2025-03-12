package pandoClass.classes.mage.skills.orb;

public enum OrbEmotion {
    NEUTRAL, // Estado normal
    HAPPY, // Se eleva más rápido y brilla
    SAD, // Baja un poco y se inclina
    ANGRY, // Tiembla y se mueve rápidamente
    CURIOUS, // Se inclina hacia el jugador
    SCARED; // Se aleja ligeramente

    // Método para obtener un nombre amigable si se necesita
    public String getDisplayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
