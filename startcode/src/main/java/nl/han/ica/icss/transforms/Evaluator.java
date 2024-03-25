package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;
    private int scope = 0;

//TODO hoe verwijder je een node uit de AST waar je al voorbij bemt (als je in ifclause zit  kun je niet de body routen naar een level hoer en dan de eiegn node verwijderen?
// idee: previoud parent bijhouden en wanneer over de childWalker dan een aanroep om de body voor if en expression voor rest als kind toevoegen aan die parent en die node te verwijderen???


//    Evalueer expressies. Schrijf een transformatie in Evaluator die alle Expression knopen in de AST door een Literal knoop met de berekende waarde vervangt.
//
//    Evalueer if/else expressies.
//    Schrijf een transformatie in Evaluator die alle IfClauses uit de AST verwijdert.
//    Wanneer de conditie van de IfClause TRUE is wordt deze vervangen door de body van het if-statement.
//    Als de conditie FALSE is dan vervang je de IfClause door de body van de ElseClause.
//    Als er geen ElseClause is bij een negatieve conditie dan verwijder je de IfClause volledig uit de AST.


    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
//        variableValues = new HANLinkedList<>();
        variableValues.insert(scope, new HashMap<>());
        walkTree(ast.root);
    }

    private void walkTree(ASTNode node) {
        if (node instanceof VariableAssignment) {
            evaluateVariableAssignment(scope, (VariableAssignment) node);
            childWalkTree(node);
            printVariables(scope);
        }
        else if (node instanceof IfClause) {
            createNewScope();
            evaluateIfStatement((IfClause) node);
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
            walkTree(child);
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


        // Check if the variable is already defined in the current scope
        if (variableValues.get(depth).containsKey(variableName)) {
            // Variable already defined, update its type
            variableValues.get(depth).put(variableName, literal);
        } else {
            // Variable not defined, add it to the current scope
            variableValues.get(depth).put(variableName, literal);
        }
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



    private void evaluateIfStatement(IfClause node) {
        Literal condition = getValue(node.conditionalExpression);
        if (condition instanceof BoolLiteral) {
            if (((BoolLiteral) condition).value) {
                node.removeChild(node.conditionalExpression);
                node.removeChild(node.elseClause);
            } else {
                node.removeChild(node.conditionalExpression);
                for(ASTNode child : node.body){
                    node.removeChild(child);
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
