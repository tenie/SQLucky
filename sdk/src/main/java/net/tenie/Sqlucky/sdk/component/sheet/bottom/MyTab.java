package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import javafx.scene.control.Tab;

import java.util.Map;

public class MyTab  extends Tab {
    private Map<String, String> customParam;
    public MyTab() {
        super();
    }

    public MyTab(String text) {
        super(text);
    }

    public Map<String, String> getCustomParam() {
        return customParam;
    }

    public void setCustomParam(Map<String, String> customParam) {
        this.customParam = customParam;
    }

    public void addCustomParam(String key, String value) {
        if (customParam == null) {
            customParam = new java.util.HashMap<>();
        }
        customParam.put(key, value);
    }

    public String getCustomParam(String key) {
        if (customParam == null) {
            return null;
        }
        return customParam.get(key);
    }
}
