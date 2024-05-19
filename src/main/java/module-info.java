module ru.stanley.messenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.sql.rowset;
    requires org.json;
    requires org.bouncycastle.provider;
    requires java.desktop;


    opens ru.stanley.messenger to javafx.fxml;
    exports ru.stanley.messenger;
    opens ru.stanley.messenger.Controllers to javafx.fxml;
    exports ru.stanley.messenger.Controllers;
    exports ru.stanley.messenger.Handler;
}