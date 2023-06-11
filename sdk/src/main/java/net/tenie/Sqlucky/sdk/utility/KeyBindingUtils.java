package net.tenie.Sqlucky.sdk.utility;

import java.util.Map;
import java.util.function.Consumer;

public class KeyBindingUtils {

	public static Map<String, Consumer<String>> keyAction;

	// <keys, actionName>
	public static Map<String, String> bindingkeyVal;

	// <actionName, keys>
	public static Map<String, String> actionName;
}
