package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

import nl.han.ica.icss.ast.*;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private int scope = 0;
    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();

        walkTree(ast.root, scope);

        printAllVariables();
    }

    private void walkTree(ASTNode node, int depth) {
            // Create a new variable scope for stylesheets
            variableTypes.insert(depth, new HashMap<>());
        if (node instanceof VariableAssignment) {
            checkVariableAssignment(depth, (VariableAssignment) node);
        }
//        else if (node instanceof Declaration) {
//            checkDeclaration((Declaration) node);
//        } else if (node instanceof IfClause) {
//            checkIfStatement((IfClause) node);
//        } else if (node instanceof Operation) {
//            checkOperation((Operation) node);
//        }

        // Recursively check children nodes
        for (ASTNode child : node.getChildren()) {
//            System.out.println("Depth: " + depth + " - " + child.getClass().getSimpleName());
            walkTree(child, depth + 1);
        }

        // Remove the current variable scope
//        variableTypes.delete(depth);
    }


//TODO    Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn.

//TODO    Controleer of de operanden van de operaties plus en min van gelijk type zijn. Je mag geen pixels bij percentages optellen bijvoorbeeld.
//        Controleer dat bij vermenigvuldigen minimaal een operand een scalaire waarde is. Zo mag 20% * 3 en 4 * 5 wel, maar mag 2px * 3px niet.

//TODO    Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer).

//TODO    Controleer of bij declaraties het type van de value klopt met de property. Declaraties zoals width: #ff0000 of color: 12px zijn natuurlijk onzin.

//TODO    Controleer of de conditie bij een if-statement van het type boolean is (zowel bij een variabele-referentie als een boolean literal)

//TODO    Controleer of variabelen enkel binnen hun scope gebruikt worden

    private void checkVariableAssignment(int depth, VariableAssignment assignment) {
        String variableName = assignment.name.name;
        ExpressionType valueType = getType(assignment.expression);

        if(valueType == ExpressionType.UNDEFINED) {
            System.out.println("Error: Variable " + variableName + " has an undefined type");
        }

        // Check if the variable is already defined in the current scope
        if (variableTypes.get(depth).containsKey(variableName)) {
            // Variable already defined, update its type
            variableTypes.get(depth).put(variableName, valueType);
        } else {
            // Variable not defined, add it to the current scope
            variableTypes.get(depth).put(variableName, valueType);
        }
    }

    private ExpressionType getType(Expression expression) {
        if (expression.getClass() == PixelLiteral.class) {
            return ExpressionType.PIXEL;
        } else if (expression.getClass() == PercentageLiteral.class) {
            return ExpressionType.PERCENTAGE;
        } else if (expression.getClass() == ColorLiteral.class) {
            return ExpressionType.COLOR;
        } else if (expression.getClass() == ScalarLiteral.class) {
            return ExpressionType.SCALAR;
        } else if (expression.getClass() == BoolLiteral.class) {
            return ExpressionType.BOOL;
        } else if (expression instanceof VariableReference) {
            String variableName = ((VariableReference) expression).name;
            return getVariableType(variableName);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType getVariableType(String variableName) {
        for (int i = scope; i >= 0; i--) {
            if (variableTypes.get(i).containsKey(variableName)) {
                return variableTypes.get(i).get(variableName);
            }
        }
        return ExpressionType.UNDEFINED;
    }
}