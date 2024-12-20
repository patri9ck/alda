package dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RuntimeTest {

    private static final String FILE_NAME = "dtengl.txt";

    public static void main(String[] args) throws IOException {
        int n = 16000;
        Dictionary<String, String> dictionary = new BinaryTreeDictionary<>();

        Map<String, String> translations = getTranslations(n);

        double start = System.nanoTime();

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            dictionary.insert(entry.getKey(), entry.getValue());
        }

        double end = System.nanoTime();

        System.out.printf("Inserting: %f%n", (end - start) / 1000);

        String toFind = new ArrayList<>(translations.keySet()).get(ThreadLocalRandom.current().nextInt(translations.size()));

        start = System.nanoTime();

        dictionary.search(toFind);

        end = System.nanoTime();

        System.out.printf("Finding successfully: %f%n", (end - start) / 1000);

        start = System.nanoTime();

        dictionary.search("");

        end = System.nanoTime();

        System.out.printf("Finding unsuccessfully: %f%n", (end - start) / 1000);
    }

    private static Map<String, String> getTranslations(int n) throws IOException {
        try (Stream<String> translations = Files.lines(Paths.get(FILE_NAME))) {
            return translations.limit(n)
                    .map(translation -> translation.split("\\s"))
                    .filter(translation -> translation.length == 2)
                    .collect(Collectors.toMap(translation -> translation[0], translation -> translation[1]));
        }
    }
}
