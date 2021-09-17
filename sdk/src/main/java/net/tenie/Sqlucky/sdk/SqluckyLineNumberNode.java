package net.tenie.Sqlucky.sdk;

import java.util.List;
import java.util.function.IntFunction;

import javafx.scene.Node;

public interface SqluckyLineNumberNode extends IntFunction<Node> {
	public   void nextBookmark( boolean isNext);
	public List<String> getLineNoList();
	
}
