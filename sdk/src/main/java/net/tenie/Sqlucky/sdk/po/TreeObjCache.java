package net.tenie.Sqlucky.sdk.po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tenie.Sqlucky.sdk.po.db.TablePo;

public class TreeObjCache {
	public static  Map<String, List<TablePo> > tableCache  = new HashMap<>();

	public static  Map<String, List<TablePo> > viewCache  = new HashMap<>();
}
