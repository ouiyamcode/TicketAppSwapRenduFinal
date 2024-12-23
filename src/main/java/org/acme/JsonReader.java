package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonReader {

    public <T> List<T> getTicketFromJsonFile(Class<T> cls, String filepath) {
        List<T> elements = new ArrayList<>();
        try {
            // Charger le fichier JSON depuis le classpath
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filepath);
            if (inputStream == null) {
                System.err.println("Erreur : Fichier introuvable : " + filepath);
                return elements;
            }

            // Lire le contenu brut du fichier JSON
            String contenuJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Contenu du fichier JSON : " + contenuJson);

            // Convertir le JSON en liste d'objets
            ObjectMapper mapper = new ObjectMapper();
            elements = Arrays.asList(mapper.readValue(contenuJson, mapper.getTypeFactory().constructArrayType(cls)));

            System.out.println("Nombre d'éléments lus : " + elements.size());
        } catch (Exception ex) {
            System.err.println("Erreur lors du chargement du fichier JSON : " + ex.getMessage());
            ex.printStackTrace();
        }
        return elements;
    }
}
