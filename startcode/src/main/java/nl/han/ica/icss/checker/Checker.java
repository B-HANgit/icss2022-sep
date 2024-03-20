package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();

        walkTree(ast);
    }

    private void walkTree(AST ast) {
        int depth = 0;
        for(ASTNode node : ast.root.getChildren()) {
            readNode(node, depth);
        }
    }

    private void readNode(ASTNode node, int depth) {
        for(ASTNode child : node.getChildren()) {
            readNode(child, depth + 1);
        }
    }
}
