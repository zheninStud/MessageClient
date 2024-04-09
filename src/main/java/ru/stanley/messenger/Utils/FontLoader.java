package ru.stanley.messenger.Utils;

import javafx.scene.text.Font;
import ru.stanley.messenger.Messenger;

public class FontLoader {

    private static final Messenger application = Messenger.getInstance();

    public static void loadFonts() {
        loadFont("Montserrat-Black.ttf", 14);
        loadFont("Montserrat-Light.ttf", 14);
    }

    private static void loadFont(String fontFileName, int size) {
        try {
            Font.loadFont(application.getClass().getResourceAsStream("fonts/" + fontFileName), size);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить шрифт: " + fontFileName);
            e.printStackTrace();
        }
    }
}
