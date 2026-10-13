package scratch.model;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProgramIO {

    public static void save(List<Action> actions, File file) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Action action : actions) {
            lines.add(action.save());
        }

        Files.write(file.toPath(), lines);
    }

    public static List<Action> load(File file) throws IOException {
        List<Action> actions = new ArrayList<>();

        List<String> lines = Files.readAllLines(file.toPath());

        for (String line : lines) {
            if (line.trim().isEmpty()) continue; // ignore les lignes vides

            String[] parts = line.split(";");
            ActionType type = ActionType.valueOf(parts[0]);
            String param = null;
            String param2 = null;

            if (parts.length > 1) {
                param = parts[1];

                if (parts.length > 2) {
                    param2 = parts[2];
                }
            }
            Action action = Actions.builder(type, param, param2);

            actions.add(action);
        }
        return actions;
    }
}