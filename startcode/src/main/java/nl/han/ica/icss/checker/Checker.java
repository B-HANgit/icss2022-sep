package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

import nl.han.ica.icss.ast.*;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private int scope = 0;
    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variableTypes.insert(scope, new HashMap<>());
        walkTree(ast.root);
    }

    private void walkTree(ASTNode node) {
        if (node instanceof VariableAssignment) {
            checkVariableAssignment(scope, (VariableAssignment) node);
            childWalkTree(node);

//            printVariables(scope);
        }
        else if (node instanceof IfClause) {
            //TODO check first then scope or reverse?
            checkIfStatement((IfClause) node);
            createNewScope();
            childWalkTree(node);
            deleteScope();
        } else if (node instanceof Stylerule) {
            createNewScope();
            childWalkTree(node);
            deleteScope();
        }
        else if (node instanceof Declaration) {
            checkDeclaration((Declaration) node);
            childWalkTree(node);
        }
        else if (node instanceof Operation) {
            checkOperation((Operation) node);
            childWalkTree(node);
        }
        else{
            childWalkTree(node);
        }
    }

    private void checkDeclaration(Declaration node) {
        ExpressionType valueType = getType(node.expression);
        String propertyName = node.property.name.toLowerCase();
//        System.out.println("Property: " + propertyName + " Value: " + valueType);
        if(valueType == ExpressionType.UNDEFINED){
            node.setError("Value of property " + propertyName + " is undefined");
        }

        if(propertyName.equals("color") || propertyName.equals("background-color")){
            if(valueType != ExpressionType.COLOR){
                node.setError("Value of property " + propertyName + " should be of type color");
            }
        }
        else if(propertyName.equals("width") || propertyName.equals("height")){
            if(valueType != ExpressionType.PIXEL && valueType != ExpressionType.PERCENTAGE){
                node.setError("Value of property " + propertyName + " should not be of type " + valueType);
            }
        }
    }

    private void checkOperation(Operation node) {
        ExpressionType leftType = getType(node.lhs);
        ExpressionType rightType = getType(node.rhs);

        if (leftType == ExpressionType.UNDEFINED) {
            node.setError("Left-hand side of operation is undefined");
        }
        if (rightType == ExpressionType.UNDEFINED) {
            node.setError("Right-hand side of operation is undefined");
        }
        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
            node.setError("Cannot perform operation with color");
        }
        if (leftType == ExpressionType.BOOL || rightType == ExpressionType.BOOL) {
            node.setError("Cannot perform operation with boolean");
        }

        if (node instanceof MultiplyOperation) {
            if (leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) {
                node.setError("At least one operand of multiplication should be a scalar");
            }
        } else {
            if(leftType == ExpressionType.SCALAR || rightType == ExpressionType.SCALAR){
                node.setError("Cannot perform operation (plus/min) with scalar");
            }

            if (leftType != rightType) {
                node.setError("Operands of operation are not of the same type");
            }
        }
    }


    private void childWalkTree(ASTNode node){
        for (ASTNode child : node.getChildren()) {
            walkTree(child);
        }
    }

    private void checkVariableAssignment(int depth, VariableAssignment assignment) {
        String variableName = assignment.name.name;
        ExpressionType valueType = getType(assignment.expression);

        if(valueType == ExpressionType.UNDEFINED) {
            assignment.setError("Variable " + variableName + " has an undefined type");
        }

        //check if variable is already defined in current and all underlying scopes
        ExpressionType expressionType = getVariableType(variableName);
        if(expressionType != ExpressionType.UNDEFINED){
            if(expressionType != valueType){
                assignment.setError("Variable " + variableName + " is already defined with a different type");
            }
//            System.out.println("Variable " + variableName + " is already defined with type " + expressionType + " in scope " + getVariableScope(variableName));
            //update the value of the variable
            variableTypes.get(depth).put(variableName, valueType);

            //versie die laagste scope bijwerkt
//            variableTypes.get(getVariableScope(variableName)).put(variableName, valueType);
        } else {
            // Variable not defined, add it to the current scope
            variableTypes.get(depth).put(variableName, valueType);
        }

//        // Check if the variable is already defined in the current scope
//        if (variableTypes.get(depth).containsKey(variableName)) {
//            // Variable already defined, update its type
//            variableTypes.get(depth).put(variableName, valueType);
//        } else {
//            // Variable not defined, add it to the current scope
//            variableTypes.get(depth).put(variableName, valueType);
//        }
    }

    private void checkIfStatement(IfClause node) {
        ExpressionType conditionType = getType(node.conditionalExpression);

        if(conditionType == ExpressionType.UNDEFINED) {
            node.setError("Condition in if-statement is undefined");
        }
        if (conditionType != ExpressionType.BOOL) {
            node.setError("If statement condition is not of type boolean");
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
            ExpressionType expressionType = getVariableType(variableName);
            if(expressionType == ExpressionType.UNDEFINED){
                expression.setError("Variable " + variableName + " is not defined (at this scope)");
            }
            return expressionType;
        } else if(expression instanceof Operation){
            ExpressionType expressionType = getOperationType((Operation) expression);
            if(expressionType == ExpressionType.UNDEFINED){
                //TODO fix error message
                expression.setError("Opperation " + expression + " is not defined");
            }
            return expressionType;
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType getOperationType(Operation expression) {
        ExpressionType leftType = getType(expression.lhs);
        ExpressionType rightType = getType(expression.rhs);

        if(leftType == ExpressionType.UNDEFINED || rightType == ExpressionType.UNDEFINED){
            return ExpressionType.UNDEFINED;
        }

        if(leftType == ExpressionType.SCALAR && rightType == ExpressionType.SCALAR){
            return ExpressionType.SCALAR;
        } else if(leftType == ExpressionType.SCALAR){
            return rightType;
        } else if(rightType == ExpressionType.SCALAR) {
            return leftType;
        } else if(leftType == rightType){
            return leftType;
        } else {
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType getVariableType(String variableName) {
        for (int i = scope; i >= 0; i--) {
            if (variableTypes.get(i).containsKey(variableName)) {
                return variableTypes.get(i).get(variableName);
            }
        }
        return ExpressionType.UNDEFINED;
    }

    private int getVariableScope(String variableName) {
        for (int i = scope; i >= 0; i--) {
            if (variableTypes.get(i).containsKey(variableName)) {
                return i;
            }
        }
        return -1;
    }

    private void createNewScope() {
        scope++;
        variableTypes.insert(scope, new HashMap<>());
    }

    private void deleteScope() {
        variableTypes.delete(scope);
        scope--;
    }

    private void printVariables(int scope) {
        System.out.println("Scope " + scope + ": " + variableTypes.get(scope).toString());
    }
}