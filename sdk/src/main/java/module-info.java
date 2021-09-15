open module SQLucky.sdk {
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires javafx.swing;  
    requires org.slf4j;
	requires org.apache.logging.log4j;	
    requires java.sql;
    
    
    exports  net.tenie.Sqlucky.sdk.utility;
    exports  net.tenie.Sqlucky.sdk;
}
