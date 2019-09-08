grammar RewriteAntlrDsl;

@header {
package migrator.rewrite.parser;
}

// case-insensitive
fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

IN  : I N ;
AND : A N D ;
OR  : O R ;
NOT : N O T ;

SELECT: S E L E C T ;
UPDATE: U P D A T E ;
INSERT: I N S E R T ;
DELETE: D E L E T E ;

AS    : A S ;
ON    : O N ;
SET   : S E T ;
FROM  : F R O M ;
INTO  : I N T O ;
JOIN  : J O I N ;
WHERE : W H E R E ;
VALUES: V A L U E S ;

CREATE: C R E A T E ;
TABLE : T A B L E ;

PRIMARY   : P R I M A R Y ;
FOREIGN   : F O R E I G N ;
KEY       : K E Y ;
REFERENCES: R E F E R E N C E S ;

QUERY: Q U E R Y ;
FRESH: F R E S H ;

EQ  : '=' ;
INEQ: '<>' | '<' | '<=' | '>' | '>=' ;
ID  : [a-zA-Z_][a-zA-Z0-9._-]* ;
NUM : [0-9]+ ;
WS  : [ \t\n\r]+ -> skip ;

queryList
    : query*
    ;

query
    : SELECT column ( ',' column )* FROM table join* ( WHERE predicate )? ';'
        # selectQuery
    | INSERT INTO table '(' column ( ',' column )* ')' VALUES '(' value ( ',' value )* ')' ';'
        # insertQuery
    | DELETE FROM table ( WHERE predicate )? ';'
        # deleteQuery
    | DELETE table ( ',' table )* FROM table join* ( WHERE predicate )? ';'
        # compoundDeleteQuery
    | UPDATE table SET set ( ',' set )* ( WHERE predicate )? ';'
        # updateQuery
    | UPDATE table join+ SET set ( ',' set )* ( WHERE predicate )? ';'
        # compoundUpdateQuery
    ;

column
    : ID
    ;

table
    : name=ID (AS reference=ID)?
    ;

join
    : JOIN table ON column EQ column
    ;

set
    : column EQ value
    ;

predicate
    : predOr
    ;

predOr
    : predAnd ( OR predAnd )*
    ;

predAnd
    : predNot ( AND predNot )*
    ;

predNot
    : predTop     # predPositive
    | NOT predNot # predNegate
    ;

predTop
    : '(' predicate ')' # predParen
    | value INEQ value  # predIneq
    | value EQ value    # predEq
    | value IN subquery # predIn
    ;

value
    : { getCurrentToken().getText().equals("<") }? INEQ ID { getCurrentToken().getText().equals(">") }? INEQ # valueParameter
    | ID                # valueIdentifier
    | NUM               # valueNumber
    | subquery          # valueSubquery
    | FRESH '(' NUM ')' # valueFresh
    ;

subquery
    : '(' SELECT column FROM table join* ( WHERE predicate )? ')'
    ;

/* === schema === */

schema
    : tableDef*
    ;

tableDef
    : CREATE TABLE tableName=ID '(' (columnStmt (',' columnStmt)* ','?)? ')' ';' ;

columnStmt
    : name=ID type=ID
        # columnDef
    | PRIMARY KEY '(' columnName=ID ')'
        # pkDef
    | FOREIGN KEY '(' columnName=ID ')' REFERENCES destTable=ID '(' destColumn=ID ')'
        # fkDef
    ;

/* === table mapping === */

tuple
    : tableName=ID '(' (pair (',' pair)*)? ')' optionalMark='?'?
    ;

pair
    : name=ID (EQ valueLabel=ID)?
    ;

tupleList
    : tuple (',' tuple)*
    ;

mapping
    : sourceTuples=tupleList '->' destTuples=tupleList
    ;

tableMapping
    : mapping*
    ;

/* === whole program === */

program
    : method* EOF
    ;

signature
    : type=(UPDATE | QUERY) name=ID '(' ( parameter ( ',' parameter )* )? ')'
    ;

method
    : signature '{' queryList '}'
    ;

parameter
    : type=ID name=ID
    ;
