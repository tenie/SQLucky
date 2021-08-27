package net.tenie.fx.utility;

import impl.org.controlsfx.tableview2.filter.parser.Operation;
import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import org.controlsfx.control.tableview2.filter.parser.Parser;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static impl.org.controlsfx.i18n.Localization.asKey;
import static impl.org.controlsfx.i18n.Localization.localize;

/**
 * 
 * @author tenie
 *字符串作为数字来过滤, 对字符串<null>做了特殊处理, 参考NumberParser
 * @param <T>
 */
public class MyNumberParser<T> implements Parser<T> {

	private String errorString = "";

	@SuppressWarnings("unchecked")
	@Override
	public Predicate<T> parse(String text) {
		errorString = "";
		Predicate<T> aggregation = aggregate(text);
		if (aggregation == null) {
			Optional<NumberOperation> optionalOperation = Stream.of(NumberOperation.values())
					.filter(opr -> text.startsWith(opr.get())).filter(opr -> text.length() > opr.length()).findFirst();
			if (optionalOperation.isPresent()) {
				NumberOperation operation = optionalOperation.get();
				String numText = trim(text, operation.length());
				if (!isNumeric(numText)) {
					errorString = localize(asKey("parser.text.error.number.input"));
					return null;
				}
				return (Predicate<T>) operation.operate(numText);
			} else {
				errorString = localize(asKey("parser.text.error.start.operator"));
				return null;
			}
		}
		return aggregation;
	}

	@Override
	public List<String> operators() {
		return Stream
				.concat(Arrays.stream(NumberOperation.values()).map(Operation::get), AggregatorsParser.getStrings())
				.collect(Collectors.toList());
	}

	@Override
	public String getSymbol(String text) {
		return Arrays.stream(NumberOperation.values()).filter(op -> op.get().equals(text)).map(Operation::getSymbol)
				.findFirst().orElse(i18nString("symbol.default"));
	}

	@Override
	public boolean isValid(String text) {
		parse(text);
		return errorString.isEmpty();
	}

	@Override
	public String getErrorMessage() {
		return errorString;
	}

	private boolean isNumeric(String str) {
		return !str.isEmpty() && str.matches("-?\\d+(\\.\\d+)?");
	}

	private String trim(String text, int startIndex) {
		return text.substring(startIndex, text.length()).trim();
	}

    double convert(String numText) {
		return Double.parseDouble(numText);
	}

	private enum NumberOperation implements Operation<String, String> {

		EQUALS("text.equals", "symbol.equals") {
			@Override
			public Predicate<String> operate(String num) {
				return t -> t != null && !"<null>".equals(t) && Double.parseDouble(t) == Double.parseDouble(num);
			}
		},
		NOT_EQUALS("text.notequals", "symbol.notequals") {
			@Override
			public Predicate<String> operate(String num) {
				return t -> t != null && !"<null>".equals(t) && Double.parseDouble(t) != Double.parseDouble(num);
			}
		},
		GREATER_THAN_EQUALS("text.greaterthanequals", "symbol.greaterthanequals") {
			@Override
			public Predicate<String> operate(String num) {
				return t -> t != null && !"<null>".equals(t) && Double.parseDouble(t) >= Double.parseDouble(num);
			}
		},
		GREATER_THAN("text.greaterthan", "symbol.greaterthan") {
			@Override
			public Predicate<String> operate(String num) {
				return t -> t != null && !"<null>".equals(t) && Double.parseDouble(t) > Double.parseDouble(num);
			}
		},
		LESS_THAN_EQUALS("text.lessthanequals", "symbol.lessthanequals") {
			@Override
			public Predicate<String> operate(String num) {
				return t -> t != null && !"<null>".equals(t) && Double.parseDouble(t) <= Double.parseDouble(num);
			}
		},
		LESS_THAN("text.lessthan", "symbol.lessthan") {
			@Override
			public Predicate<String> operate(String num) {
				return t -> t != null && !"<null>".equals(t) && Double.parseDouble(t) < Double.parseDouble(num);
			}
		};

		private final String opr;
		private final String symbol;

		NumberOperation(String opr, String symbol) {
			this.opr = i18nString(opr);
			this.symbol = i18nString(symbol);
		}

		@Override
		public int length() {
			return opr.length();
		}

		@Override
		public String get() {
			return opr;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

	}

	private static String i18nString(String key) {
		return localize(asKey("parser.text.operator." + key));
	}
}
