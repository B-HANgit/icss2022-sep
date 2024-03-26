package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;
    private int scope = 0;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues.insert(scope, new HashMap<>());
        walkTree(null, ast.root);
    }

    private void walkTree(ASTNode parent, ASTNode node) {
        if (node instanceof VariableAssignment) {
            evaluateVariableAssignment(scope, (VariableAssignment) node);
            childWalkTree(node);

            printVariables(scope);
        }
        else if (node instanceof IfClause) {
            //TODO check first then scope or reverse?
            evaluateIfStatement(parent, (IfClause) node);
            createNewScope();
            childWalkTree(node);
            deleteScope();
        } else if (node instanceof Stylerule) {
            createNewScope();
            childWalkTree(node);
            deleteScope();
        }
        else if (node instanceof Declaration) {
            evaluateDeclaration((Declaration) node);
            childWalkTree(node);
        }
        else if (node instanceof Operation) {
            evaluateOperation((Operation) node);
            childWalkTree(node);
        }
        else{
            childWalkTree(node);
        }
    }

    private void childWalkTree(ASTNode node){
        for (ASTNode child : node.getChildren()) {
            walkTree(node, child);
        }
    }

    private void evaluateDeclaration(Declaration node) {
        //nieuwe waarde toekennen aan property (want kan een var zijn in dit geval).
        node.expression = getValue(node.expression);
    }

    private void evaluateVariableAssignment(int depth, VariableAssignment assignment) {
        String variableName = assignment.name.name;
        Literal literal = getValue(assignment.expression);

        if(literal == null) {
            assignment.setError("Variable " + variableName + " has no value.");
        }

        assignment.expression = literal;

        //check if variable is already defined in current and all underlying scopes
        Literal value = getVariableValue(variableName);
        if(value != null){
            System.out.println("Variable " + variableName + " is already defined in scope " + getVariableScope(variableName));
            //update the value of the variable
            variableValues.get(depth).put(variableName, literal);
            //TODO versie die laagste scope bijwerkt
//            variableValues.get(getVariableScope(variableName)).put(variableName, literal);

        } else {
            // Variable not defined, add it to the current scope
            variableValues.get(depth).put(variableName, literal);
        }

//        // Check if the variable is already defined in the current scope
//        if (variableValues.get(depth).containsKey(variableName)) {
//            // Variable already defined, update its type
//            variableValues.get(depth).put(variableName, literal);
//        } else {
//            // Variable not defined, add it to the current scope
//            variableValues.get(depth).put(variableName, literal);
//        }
    }

    private int getVariableScope(String variableName) {
        for (int i = scope; i >= 0; i--) {
            if (variableValues.get(i).containsKey(variableName)) {
                return i;
            }
        }
        return -1;
    }

    private Literal getValue(Expression expression) {
        if(expression instanceof BoolLiteral) {
            return (BoolLiteral) expression;
        } else if(expression instanceof PixelLiteral) {
            return (PixelLiteral) expression;
        } else if(expression instanceof ColorLiteral){
            return (ColorLiteral) expression;
        } else if(expression instanceof PercentageLiteral) {
            return (PercentageLiteral) expression;
        } else if(expression instanceof ScalarLiteral) {
            return (ScalarLiteral) expression;
        } else if(expression instanceof VariableReference) {
            return getVariableValue(((VariableReference) expression).name);
        } else if(expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }
        return null;
    }

    private Literal getVariableValue(String variableName) {
        for (int i = scope; i >= 0; i--) {
            if (variableValues.get(i).containsKey(variableName)) {
                return variableValues.get(i).get(variableName);
            }
        }
        return null;
    }

    private Literal evaluateOperation(Operation operation) {
        Literal left = getValue(operation.lhs);
        Literal right = getValue(operation.rhs);
        if (operation instanceof AddOperation) {
            return add(left, right);
        } else if (operation instanceof SubtractOperation) {
            return subtract(left, right);
        } else if (operation instanceof MultiplyOperation) {
            return multiply(left, right);
        }
        return null;
    }

    private Literal multiply(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {                                   //px*sc
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {                            //sc*px
                return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {                       //pr*sc
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {                       //sc*pr
            return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {                           //sc*sc
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        }
        return null;
    }

    private Literal add(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal subtract(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }
        return null;
    }

    //TODO conditional stays false even if variable is defined TRUE in a lower scope
    private void evaluateIfStatement(ASTNode parent, IfClause node) {
        ArrayList<ASTNode> body = null;
        Literal condition = getValue(node.conditionalExpression);
        if (condition instanceof BoolLiteral) {
            if (((BoolLiteral) condition).value) {
                body = node.body;
            } else {
                if (node.elseClause != null) {
                    body = node.elseClause.body;
                }
            }
        }

        parent.removeChild(node);
        if(body != null){
            for(ASTNode child : body){
                if(child instanceof IfClause){
                    evaluateIfStatement(parent, (IfClause) child);
                } else {
                    parent.addChild(child);
                }
            }
        }
    }

    private void createNewScope() {
        scope++;
        variableValues.insert(scope, new HashMap<>());
    }

    private void deleteScope() {
        variableValues.delete(scope);
        scope--;
    }

    private void printVariables(int scope) {
        System.out.println("Scope " + scope + ": " + variableValues.get(scope).toString());
    }
}
