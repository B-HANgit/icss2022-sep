package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

	public String generate(AST ast) {
        String output = "";

        for(ASTNode node : ast.root.getChildren()){
            if(node instanceof Stylerule) {
                output += getTag((Stylerule) node) + "{\n";

                for(ASTNode child : node.getChildren()){
                    if(child instanceof Declaration){
                        Declaration declaration = (Declaration) child;
                        output += "  " + declaration.property.name + ": " + getExpression(declaration.expression) + ";\n";
                    }
                }
                output += "}\n";
            }
        }
        return output;
	}

    private String getExpression(Expression expression) {
        if(expression instanceof ColorLiteral){
            return ((ColorLiteral) expression).value;
        } else if(expression instanceof PercentageLiteral){
            return ((PercentageLiteral) expression).value + "%";
        } else if(expression instanceof PixelLiteral){
            return ((PixelLiteral) expression).value + "px";
        } else if(expression instanceof ScalarLiteral){
            return ((ScalarLiteral) expression).value + "";
        }
        return "";
    }

    private String getTag(Stylerule node){
        String tag = "";
        for(ASTNode selector : node.selectors){
            if (selector instanceof TagSelector) {
                tag += ((TagSelector) selector).tag + " ";
            } else if (selector instanceof ClassSelector) {
                tag += ((ClassSelector) selector).cls + " ";
            } else if (selector instanceof IdSelector) {
                tag += ((IdSelector) selector).id + " ";
            }
        }
        return tag;
    }
}
