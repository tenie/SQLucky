package net.tenie.fx.component.InfoTree.TreeItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tenie.Sqlucky.sdk.po.TablePo;

public class TreeObjCache {
	public static  Map<String, List<TablePo> > tableCache  = new HashMap<>();

	public static  Map<String, List<TablePo> > viewCache  = new HashMap<>();
}
