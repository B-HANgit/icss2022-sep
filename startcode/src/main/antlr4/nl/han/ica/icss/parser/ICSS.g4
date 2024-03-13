grammar ICSS;

//--- LEXER: ---

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
selector: (ID_IDENT | CLASS_IDENT | LOWER_IDENT) ;

declaratie: property COLON value SEMICOLON;
//alleen mogelijke properties: color, background-color, width, height
property: LOWER_IDENT;
//color en background-color MOETEN hex zijn
value: COLOR | PIXELSIZE | PERCENTAGE | var;

//TODO fix value, variabele en expression relatie
//de value van een property mag ook een variabele zijn
//alleen kan een variabale een waarde krijgen (TRUE) die niet valide is binnen een property
//expressions mag ook een var zijn die alleen een bool type bevat.

//verschil maken tussen 3 variabele typen 1: propery mogelijk 2: scalar erbij 3: bools
// valueVar: COLOR | PIXELSIZE | PERCENTAGE
// normalVar: COLOR | PIXELSIZE | PERCENTAGE | SCALAR
// boolVar: TRUE | FALSE

varAssignment: var ASSIGNMENT_OPERATOR varValue SEMICOLON;
var: CAPITAL_IDENT;
varValue: COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE;

ifStatement: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE body CLOSE_BRACE (elseStatement)?;
elseStatement: ELSE OPEN_BRACE body CLOSE_BRACE;
//expressions mag ook een var zijn die een bool bevat.
expression: var | TRUE | FALSE;
body: ruletype*;
