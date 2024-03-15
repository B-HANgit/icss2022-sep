grammar ICSS;

//--- LEXER: ---

COLOR_PROPERTYS: 'color' | 'background-color';
WIDTH_HEIGHT_PROPERTYS: 'width' | 'height';

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
stylesheet: styleblock+ EOF;
styleblock: stylerule | varAssignment;

stylerule: selector OPEN_BRACE ruletype* CLOSE_BRACE;
ruletype: ifStatement | declaratie | varAssignment;
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;

//declaratie: property COLON propValue SEMICOLON;
//property: LOWER_IDENT;
//propValue: var | COLOR | PIXELSIZE | PERCENTAGE | calc;

declaratie: COLOR_PROPERTYS COLON propColorValue SEMICOLON
    | WIDTH_HEIGHT_PROPERTYS COLON propValue SEMICOLON;
propColorValue: var | COLOR;
propValue: var | PIXELSIZE | PERCENTAGE | calc;

varAssignment: var ASSIGNMENT_OPERATOR varValue SEMICOLON;
var: CAPITAL_IDENT;
varValue: var | COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE | calc;

ifStatement: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE body CLOSE_BRACE (elseStatement)?;
elseStatement: ELSE OPEN_BRACE body CLOSE_BRACE;
expression: var | TRUE | FALSE;
body: ruletype*; //aanname dat een body leeg mag zijn

calc: calcPixel | calcPercent;
calcPixel: calcPixel MUL SCALAR
    | SCALAR MUL calcPixel
    | calcPixel (PLUS|MIN) calcPixel
    | PIXELSIZE
    | var;

calcPercent: calcPercent MUL SCALAR
    | SCALAR MUL calcPercent
    | calcPercent (PLUS|MIN) calcPercent
    | PERCENTAGE
    | var;
