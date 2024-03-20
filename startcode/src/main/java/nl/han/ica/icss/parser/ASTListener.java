package nl.han.ica.icss.parser;

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
        Declaration declaration = null;
        if(ctx.COLOR_PROPERTYS() != null){
            declaration = new Declaration(ctx.COLOR_PROPERTYS().getText());
        } else if (ctx.WIDTH_HEIGHT_PROPERTYS() != null) {
            declaration = new Declaration(ctx.WIDTH_HEIGHT_PROPERTYS().getText());
        }
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterPropColorValue(ICSSParser.PropColorValueContext ctx) {
        if (ctx.COLOR() != null) {
            ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
            currentContainer.peek().addChild(colorLiteral);
            currentContainer.push(colorLiteral);
        } else if(ctx.CAPITAL_IDENT() != null) {
            VariableReference vr = new VariableReference(ctx.getText());
            currentContainer.peek().addChild(vr);
            currentContainer.push(vr);
        }
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
        } else if (ctx.calc() != null) {
            currentContainer.peek().addChild(currentContainer.peek());
            currentContainer.push(currentContainer.peek());
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
        currentContainer.peek().addChild(variableAssignment);
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVarAssignment(ICSSParser.VarAssignmentContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterVarValue(ICSSParser.VarValueContext ctx) {
        if(ctx.PIXELSIZE() != null) {
            Literal literal = new PixelLiteral(ctx.PIXELSIZE().getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.PERCENTAGE() != null) {
            Literal literal = new PercentageLiteral(ctx.PERCENTAGE().getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.COLOR() != null) {
            Literal literal = new ColorLiteral(ctx.COLOR().getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.CAPITAL_IDENT() != null) {
            VariableReference variableReference = new VariableReference(ctx.CAPITAL_IDENT().getText());
            currentContainer.peek().addChild(variableReference);
            currentContainer.push(variableReference);
        } else if(ctx.TRUE() != null){
            Literal literal = new BoolLiteral(ctx.TRUE().getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.FALSE() != null){
            Literal literal = new BoolLiteral(ctx.FALSE().getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if (ctx.SCALAR() != null) {
            Literal literal = new ScalarLiteral(ctx.SCALAR().getText());
            currentContainer.peek().addChild(literal);
            currentContainer.push(literal);
        } else if(ctx.calc() != null) {
            currentContainer.push(currentContainer.peek());
        }
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
        if(ctx.CAPITAL_IDENT() != null){
            VariableReference variableReference = new VariableReference(ctx.CAPITAL_IDENT().getText());
            currentContainer.peek().addChild(variableReference);
            currentContainer.push(variableReference);
        } else if(ctx.TRUE() != null){
            BoolLiteral boolLiteral = new BoolLiteral(ctx.TRUE().getText());
            currentContainer.peek().addChild(boolLiteral);
            currentContainer.push(boolLiteral);
        } else if(ctx.FALSE() != null) {
            BoolLiteral boolLiteral = new BoolLiteral(ctx.FALSE().getText());
            currentContainer.peek().addChild(boolLiteral);
            currentContainer.push(boolLiteral);
        }
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterCalc(ICSSParser.CalcContext ctx) {
        ASTNode exp = null;

        if (ctx.getChildCount() == 3) {
            char Op = ctx.getChild(1).getText().charAt(0);
            switch (Op) {
                case '*': exp = new MultiplyOperation(); break;
                case '+': exp = new AddOperation(); break;
                default: exp = new SubtractOperation(); break;
            }
            currentContainer.peek().addChild(exp);
            currentContainer.push(exp);
        }

        if (ctx.getChildCount() == 1) { // Leaf
            if(ctx.PIXELSIZE() != null) {
                exp = new PixelLiteral(ctx.getChild(0).getText());
            }else if(ctx.PERCENTAGE() != null) {
                exp = new PercentageLiteral(ctx.getChild(0).getText());
            } else if(ctx.CAPITAL_IDENT() != null) {
                exp = new VariableReference(ctx.getChild(0).getText());
            }
            currentContainer.peek().addChild(exp);
            currentContainer.push(exp);
        }
    }

    @Override
    public void exitCalc(ICSSParser.CalcContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterScalar(ICSSParser.ScalarContext ctx) {
        ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.peek().addChild(scalarLiteral);
        currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalar(ICSSParser.ScalarContext ctx) {
        currentContainer.pop();
    }

}