package net.tenie.fx.Action;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.parser.Parser;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;

import java.util.List;

/*   @author tenie */
// 字符串作为数字来过滤, 对字符串<null>做了特殊处理, 参考NumberParser
public class MyPopupNumberFilter<S, T extends String> extends PopupFilter<S, T> {

	private final MyNumberParser<T> numberParser;

	public MyPopupNumberFilter(FilteredTableColumn<S, T> tableColumn) {
		super(tableColumn);
		numberParser = new MyNumberParser<>();

		text.addListener((obs, ov, nv) -> {
			if (nv == null || nv.isEmpty()) {
				tableColumn.setPredicate(null);
			} else {
				tableColumn.setPredicate(getParser().parse(nv));
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getOperations() {
		return numberParser.operators();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Parser<T> getParser() {
		return numberParser;
	}
}
