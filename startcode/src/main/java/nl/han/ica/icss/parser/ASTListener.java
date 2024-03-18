package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

    //TODO rest of methods, types, literals, operations

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        ast.root = stylesheet;
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.peek().addChild(stylerule);
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        currentContainer.pop();
    }

//    @Override
//    public void enterBody(ICSSParser.BodyContext ctx) {
//        super.exitBody(ctx);
//    }
//
//    @Override
//    public void exitBody(ICSSParser.BodyContext ctx) {
//        super.exitBody(ctx);
//    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        Selector selector = null;
        if(ctx.CLASS_IDENT() != null) {
            selector = new ClassSelector(ctx.CLASS_IDENT().getText());
        } else if(ctx.ID_IDENT() != null) {
            selector = new IdSelector(ctx.ID_IDENT().getText());
        } else if(ctx.LOWER_IDENT() != null) {
            selector = new TagSelector(ctx.LOWER_IDENT().getText());
        }
        currentContainer.peek().addChild(selector);
        currentContainer.push(selector);
    }

    @Override
    public void exitSelector(ICSSParser.SelectorContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration(ctx.getText());
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterPropColorValue(ICSSParser.PropColorValueContext ctx) {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
        currentContainer.peek().addChild(colorLiteral);
        currentContainer.push(colorLiteral);
    }

    @Override
    public void exitPropColorValue(ICSSParser.PropColorValueContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterPropValue(ICSSParser.PropValueContext ctx) {
        if(ctx.PIXELSIZE() != null) {
            Literal literal = new PixelLiteral(ctx.getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.PERCENTAGE() != null) {
            Literal literal = new PercentageLiteral(ctx.getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.CAPITAL_IDENT() != null) {
            VariableReference vr = new VariableReference(ctx.getText());
            currentContainer.peek().addChild(vr);
            currentContainer.push(vr);
        } else if(ctx.calc() != null) {
            //TODO: implement calc
        }
    }

    @Override
    public void exitPropValue(ICSSParser.PropValueContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterVarAssignment(ICSSParser.VarAssignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        variableAssignment.name = new VariableReference(ctx.CAPITAL_IDENT().getText());
        //TODO: implement variableAssignment.expression
        if(ctx.varValue() != null) {
            if(ctx.varValue().PIXELSIZE() != null) {
                variableAssignment.expression = new PixelLiteral(ctx.varValue().PIXELSIZE().getText());
            } else if(ctx.varValue().PERCENTAGE() != null) {
                variableAssignment.expression = new PercentageLiteral(ctx.varValue().PERCENTAGE().getText());
            } else if(ctx.varValue().COLOR() != null) {
                variableAssignment.expression = new ColorLiteral(ctx.varValue().COLOR().getText());
            } else if(ctx.varValue().CAPITAL_IDENT() != null) {
                variableAssignment.expression = new VariableReference(ctx.varValue().CAPITAL_IDENT().getText());
            } else if(ctx.varValue().TRUE() != null){
                variableAssignment.expression = new BoolLiteral(ctx.varValue().TRUE().getText());
            } else if(ctx.varValue().FALSE() != null){
                variableAssignment.expression = new BoolLiteral(ctx.varValue().FALSE().getText());
            } else if (ctx.varValue().SCALAR() != null) {
                variableAssignment.expression = new ScalarLiteral(ctx.varValue().SCALAR().getText());
            } else if(ctx.varValue().calc() != null) {
//                TODO implement calc
            }
        }

        currentContainer.peek().addChild(variableAssignment);
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVarAssignment(ICSSParser.VarAssignmentContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterVarValue(ICSSParser.VarValueContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.peek().addChild(variableReference);
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVarValue(ICSSParser.VarValueContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterIfStatement(ICSSParser.IfStatementContext ctx) {
        IfClause ifClause = new IfClause();
        currentContainer.peek().addChild(ifClause);
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIfStatement(ICSSParser.IfStatementContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterElseStatement(ICSSParser.ElseStatementContext ctx) {
        ElseClause elseClause = new ElseClause();
        currentContainer.peek().addChild(elseClause);
        currentContainer.push(elseClause);
    }

    @Override
    public void exitElseStatement(ICSSParser.ElseStatementContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterExpression(ICSSParser.ExpressionContext ctx) {
        super.enterExpression(ctx);
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        super.exitExpression(ctx);
    }

    @Override
    public void enterCalc(ICSSParser.CalcContext ctx) {
        super.enterCalc(ctx);
    }

    @Override
    public void exitCalc(ICSSParser.CalcContext ctx) {
        super.exitCalc(ctx);
    }

    @Override
    public void enterCalcPixel(ICSSParser.CalcPixelContext ctx) {
        super.enterCalcPixel(ctx);
    }

    @Override
    public void exitCalcPixel(ICSSParser.CalcPixelContext ctx) {
        super.exitCalcPixel(ctx);
    }

    @Override
    public void enterCalcPercent(ICSSParser.CalcPercentContext ctx) {
        super.enterCalcPercent(ctx);
    }

    @Override
    public void exitCalcPercent(ICSSParser.CalcPercentContext ctx) {
        super.exitCalcPercent(ctx);
    }

//    @Override
//    public void enterEveryRule(ParserRuleContext ctx) {
//        super.enterEveryRule(ctx);
//    }
//
//    @Override
//    public void exitEveryRule(ParserRuleContext ctx) {
//        super.exitEveryRule(ctx);
//    }

}