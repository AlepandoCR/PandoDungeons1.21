package pandoQuests.narrative;

import java.util.Random;

public class MissionNarrative {

    public static String generateGenericNarrative(String playerName, String missionDestination, String missionObjective) {
        String[] intros = {
                "¡Saludos, valiente aventurero!",
                "¡Ah, justo el héroe que necesitamos!",
                "Los vientos hablan de peligro, y tú eres nuestra única esperanza.",
                "El reino te llama, guerrero. Es hora de actuar.",
                "Una sombra oscura se cierne sobre nosotros, y solo tú puedes disiparla."
        };

        String[] bodies = {
                "Tu misión es simple pero crucial. Viaja a %s y asegúrate de %s.",
                "En las profundidades de %s yace un gran desafío: %s. ¿Responderás al llamado?",
                "Los informes desde %s sugieren que hay problemas en aumento. Tu objetivo es claro: %s.",
                "Los habitantes de %s necesitan ayuda urgente. Tu misión es %s.",
                "Se han avistado fuerzas enemigas cerca de %s. Tu tarea es %s antes de que sea tarde."
        };

        String[] endings = {
                "Que la fortuna favorezca tu viaje.",
                "Regresa pronto, héroe, el tiempo es esencial.",
                "El reino confía en ti.",
                "Solo los valientes prosperan en tiempos de peligro. Buena suerte.",
                "Que los dioses guíen tu camino, valiente aventurero."
        };

        Random random = new Random();
        String intro = intros[random.nextInt(intros.length)];
        String body = String.format(bodies[random.nextInt(bodies.length)], missionDestination, missionObjective);
        String ending = endings[random.nextInt(endings.length)];

        return intro + " " + body + " " + ending;
    }
}
