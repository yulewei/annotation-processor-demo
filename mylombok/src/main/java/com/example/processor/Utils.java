package com.example.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

/**
 * @author yulewei
 */
public class Utils {

    public static String toGetterName(JCTree.JCVariableDecl field) {
        boolean isBoolean = field.vartype.toString().equals("boolean");
        String prefix = isBoolean ? "is" : "get";
        return prefix + toTitleCase(field.getName().toString());
    }

    public static String toSetterName(JCTree.JCVariableDecl field) {
        return "set" + toTitleCase(field.getName().toString());
    }

    private static String toTitleCase(String str) {
        char first = str.charAt(0);
        if (first >= 'a' && first <= 'z') {
            first -= 32;
        }
        return first + str.substring(1);
    }

    public static boolean methodExists(String methodName, JCTree.JCClassDecl classDecl) {
        for (JCTree def : classDecl.defs) {
            if (def instanceof JCTree.JCMethodDecl) {
                if (((JCTree.JCMethodDecl) def).name.contentEquals(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static JCTree.JCExpression chainDotsString(TreeMaker maker, Names names, String elemsStr) {
        String[] elems = elemsStr.split("\\.");
        JCTree.JCExpression e = maker.Ident(names.fromString(elems[0]));
        for (int i = 1; i < elems.length; i++) {
            e = maker.Select(e, names.fromString(elems[i]));
        }
        return e;
    }

}
