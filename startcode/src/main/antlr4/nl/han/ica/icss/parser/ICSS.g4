grammar ICSS;

//--- LEXER: ---

//COLOR_PROPERTYS: 'color' | 'background-color';
//WIDTH_HEIGHT_PROPERTYS: 'width' | 'height';

PROPERTYS: 'color' | 'background-color' | 'width' | 'height';

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';


//--- PARSER: ---

stylesheet: (stylerule|varAssignment)* EOF;
stylerule: selector OPEN_BRACE body* CLOSE_BRACE;
body: ifStatement | declaration | varAssignment; //aanname dat een body leeg mag zijn (+)
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;

//declaration: COLOR_PROPERTYS COLON propColorValue SEMICOLON
//    | WIDTH_HEIGHT_PROPERTYS COLON propValue SEMICOLON;
//propColorValue: CAPITAL_IDENT | COLOR;
//propValue: CAPITAL_IDENT | PIXELSIZE | PERCENTAGE | calc;

declaration: PROPERTYS COLON propValue SEMICOLON;
propValue: CAPITAL_IDENT | PIXELSIZE | PERCENTAGE | COLOR | calc;


varAssignment: CAPITAL_IDENT ASSIGNMENT_OPERATOR varValue SEMICOLON;
varValue: CAPITAL_IDENT | COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE | calc;

ifStatement: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE body* CLOSE_BRACE (elseStatement)?;
elseStatement: ELSE OPEN_BRACE body* CLOSE_BRACE;
expression: CAPITAL_IDENT | TRUE | FALSE;

//calc: calc MUL scalar
//    | scalar MUL calc
//    | scalar MUL scalar
//    | calc (PLUS|MIN) calc
//    | CAPITAL_IDENT | PIXELSIZE | PERCENTAGE;
//scalar: SCALAR | CAPITAL_IDENT;

calc: calc MUL calc
    | calc (PLUS|MIN) calc
    | CAPITAL_IDENT | PIXELSIZE | PERCENTAGE | SCALAR;
