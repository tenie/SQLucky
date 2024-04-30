package net.tenie.Sqlucky.sdk.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tenie
 */
public class CodeAreaHighLightingHelperForJava {
    public static CodeAreaHighLightingHelper createHelper() {
        List<String> keywords = new ArrayList<>(Arrays.asList(
                "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", "CLASS", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", "IMPLEMENTS", "IMPORT", "INT", "INTERFACE", "INSTANCEOF", "LONG", "NATIVE", "NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "TRUE", "FALSE", "NULL"
        ));

        CodeAreaHighLightingHelper helper = new CodeAreaHighLightingHelper(keywords);
        return helper;
    }
}
