package net.tenie.Sqlucky.sdk.component;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public abstract class SqluckyTitledPane extends TitledPane {
//    private String name = "";
    private Pane btnsBox;

    private Consumer<String> showCaller;
    private Consumer<String> hideCaller;

    public void showFinder(String str){
        if (showCaller != null){
            showCaller.accept(str);
        }
    }

    public void hideFinder(){
        if (hideCaller != null){
            hideCaller.accept("");
        }
    }

    public void setShowFinder(Consumer<String> caller){
        this.showCaller = caller;
    }
    public void setHideFinder(Consumer<String> caller){
        this.hideCaller = caller;
    }


//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public Pane getBtnsBox() {
        return btnsBox;
    }

    public void setBtnsBox(Pane btnsBox) {
        this.btnsBox = btnsBox;
    }
}
