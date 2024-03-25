package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;


//    Evalueer expressies. Schrijf een transformatie in Evaluator die alle Expression knopen in de AST door een Literal knoop met de berekende waarde vervangt.
//
//    Evalueer if/else expressies.
//    Schrijf een transformatie in Evaluator die alle IfClauses uit de AST verwijdert.
//    Wanneer de conditie van de IfClause TRUE is wordt deze vervangen door de body van het if-statement.
//    Als de conditie FALSE is dan vervang je de IfClause door de body van de ElseClause.
//    Als er geen ElseClause is bij een negatieve conditie dan verwijder je de IfClause volledig uit de AST.


    public Evaluator() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();

    }

    //    private Literal evaluateOperation(Operation operation) {
//        Literal left = evaluateExpression(operation.lhs);
//        Literal right = evaluateExpression(operation.rhs);
//        if (operation instanceof AddOperation) {
//            return add(left, right);
//        } else if (operation instanceof SubtractOperation) {
//            return subtract(left, right);
//        } else if (operation instanceof MultiplyOperation) {
//            return multiply(left, right);
//        }
//        return null;
//    }

    
}
