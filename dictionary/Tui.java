package dictionary;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class Tui {

    private static final String FILE_NAME = "dtengl.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Dictionary<String, String> dictionary = null;

        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split("\\s+");

            String german;
            String english;

            switch (line[0]) {
                case "create":
                    if (line.length == 1) {
                        dictionary = new SortedArrayDictionary<>();
                        System.out.println("SortedArrayDictionary wurde als Implementierung gewählt.");
                        break;
                    }

                    if (line.length == 2) {
                        String implementation = line[1];

                        switch (implementation) {
                            case "sortedarray":
                                dictionary = new SortedArrayDictionary<>();
                                System.out.println("SortedArrayDictionary wurde als Implementierung gewählt.");
                                break;
                            case "hash":
                                dictionary = new HashDictionary<>();
                                System.out.println("HashDictionary wurde als Implementierung gewählt.");
                                break;
                            case "binarytree":
                                System.out.println("BinaryTreeDictionary wurde als Implementierung gewählt.");
                                dictionary = new BinaryTreeDictionary<>();
                                break;
                            default:
                                System.out.println("Unbekannte Implementierung: " + implementation);
                        }

                        break;
                    }

                    System.out.println("Nutzung: create Implementierung");
                    break;
                case "r":
                    if (!isCreated(dictionary)) {
                        break;
                    }

                    try {
                        if (line.length == 1) {
                            try (Stream<String> translations = Files.lines(Paths.get(FILE_NAME))) {
                                insert(dictionary, translations.map(translation -> translation.split("\\s")).filter(translation -> translation.length == 2));
                            }

                            break;
                        }

                        if (line.length == 2 || line.length == 3) {
                            try {
                                int n = Integer.parseInt(line[1]);

                                if (n < 1) {
                                    System.out.println("n muss eine Zahl größer als 0 sein.");
                                    break;
                                }

                                Path path;

                                if (line.length == 3) {
                                    path = Paths.get(line[2]);
                                } else {
                                    JFileChooser chooser = new JFileChooser();

                                    if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                                        System.out.println("Es wurde keine Datei ausgewählt.");
                                        break;
                                    }

                                    path = Paths.get(chooser.getSelectedFile().getPath());
                                }

                                try (Stream<String> translations = Files.lines(path)) {
                                    insert(dictionary, translations.limit(n).map(translation -> translation.split("\\s")).filter(translation -> translation.length == 2));
                                }

                                System.out.println(path + " wurde eingelesen.");
                            } catch (NumberFormatException exception) {
                                System.out.println("n ist keine Zahl sein.");
                            }

                            break;
                        }
                    } catch (IOException exception) {
                        System.out.println("Von " + FILE_NAME + " konnte nicht gelesen werden: " + exception.getMessage());
                    }

                    System.out.println("Nutzung: r n Dateiname");

                    break;
                case "p":
                    if (!isCreated(dictionary)) {
                        break;
                    }

                    if (dictionary.size() == 0) {
                        System.out.println("Es wurden noch keine Übersetzungen gespeichert.");
                        break;
                    }

                    for (Dictionary.Entry<String, String> entry : dictionary) {
                        System.out.println(entry.getKey() + " - " + entry.getValue());
                    }

                    if (dictionary instanceof BinaryTreeDictionary<String, String>) {
                        ((BinaryTreeDictionary<String, String>) dictionary).prettyPrint();
                    }

                    break;
                case "s":
                    if (!isCreated(dictionary)) {
                        break;
                    }

                    if (line.length != 2) {
                        System.out.println("Nutzung: s deutsch");
                        break;
                    }

                    german = line[1];
                    english = dictionary.search(german);

                    if (english == null) {
                        System.out.println("Die Übersetzung für " + german + " existiert nicht");
                        break;
                    }

                    System.out.println("Die Übersetzung für " + german + " lautet " + english + ".");

                    break;
                case "i":
                    if (!isCreated(dictionary)) {
                        break;
                    }

                    if (line.length != 3) {
                        System.out.println("Nutzung: i deutsch englisch");
                        break;
                    }

                    german = line[1];
                    english = line[2];

                    dictionary.insert(german, english);

                    System.out.println("Übersetzung von " + german + " zu " + english + " wurde hinzugefügt");

                    break;
                case "d":
                    if (!isCreated(dictionary)) {
                        break;
                    }

                    if (line.length != 2) {
                        System.out.println("Nutzung: d deutsch");
                        break;
                    }

                    german = line[1];
                    english = dictionary.remove(german);

                    if (english == null) {
                        System.out.println("Übersetzung für " + german + " ist nicht gespeichert.");
                        break;
                    }

                    System.out.println("Übersetzung von " + german + " zu " + english + " wurde gelöscht");

                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Unbekannter Befehl");
            }
        }
    }

    private static void insert(Dictionary<String, String> dictionary, Stream<String[]> translations) {
        translations.forEach(translation -> dictionary.insert(translation[0], translation[1]));
    }

    private static boolean isCreated(Dictionary<String, String> dictionary) {
        if (dictionary == null) {
            System.out.println("Implementierung wurde noch nicht gewählt.");
            return false;
        }

        return true;
    }
}
