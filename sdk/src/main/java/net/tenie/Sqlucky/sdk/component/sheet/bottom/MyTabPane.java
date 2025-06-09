package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;

public class MyTabPane extends TabPane {
    public MyTabPane(){
        super();
    }



    public List<MyTab> getMyTabs() {
        ObservableList<Tab>  tbs =  super.getTabs();
        List<MyTab> myTabList = new ArrayList<>();
        for(Tab tb: tbs){
            myTabList.add((MyTab)tb);
        }
        return myTabList;
    }

    public void addMyTab(MyTab tab) {
        super.getTabs().add(tab);
    }
}
