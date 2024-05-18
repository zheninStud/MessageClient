package ru.stanley.messenger.Utils;

import java.util.HashMap;
import java.util.Map;

public class ControllerRegistry {
    private static final Map<String, Object> controllers = new HashMap<>();

    public static void registerController(String name, Object controller) {
        controllers.put(name, controller);
    }

    public static Object getController(String name) {
        return controllers.get(name);
    }
}
