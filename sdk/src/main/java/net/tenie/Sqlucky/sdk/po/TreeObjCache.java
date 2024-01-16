package net.tenie.Sqlucky.sdk.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tenie.Sqlucky.sdk.po.db.TablePo;

public class TreeObjCache {
	public static  Map<String, List<TablePo> > tableCache  = new HashMap<>();

	public static  Map<String, List<TablePo> > viewCache  = new HashMap<>();


	public  static List<TablePo>  getTable(String key){
		List<TablePo>  ls =  tableCache.get(key);
		if (ls == null){
			ls = new ArrayList<>();
		}
		return ls;
	}

	public static  List<TablePo>  getView(String key){
		List<TablePo>  ls =  viewCache.get(key);
		if (ls == null){
			ls = new ArrayList<>();
		}
		return ls;
	}
}
