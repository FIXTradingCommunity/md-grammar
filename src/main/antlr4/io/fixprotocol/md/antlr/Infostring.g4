/* Type of a fenced code block with extensions for file imports */
grammar Infostring;

infostring
:
	type? IGNORE_WS? importspec? EOF
;

type
:
TEXT
;

importspec
:
	IMPORT IGNORE_WS? path IGNORE_WS? (start IGNORE_WS? end?)?
;

start
:
	FROM? IGNORE_WS? location
;

end
:
	TO IGNORE_WS? location
;

location
:
	LINENUMBER | STRING
;

LINENUMBER
:
	DIGIT+
;

path
:
	TEXT
;

STRING
:	
	'"' STRINGCHAR+ '"' 
;

IMPORT
:
	'import'
;

FROM
:
	'from'
;

TO
:
	'to' | '-'
;


DIGIT
:
	[0-9]
;

TEXT
:
	WORDCHAR+
;

IGNORE_WS
:
	WS -> skip
;

fragment
WS
:
	[  \t]
;

fragment
WORDCHAR
:
	~[\n\r\t |`]
;

fragment
STRINGCHAR
:
	~[\n\r\t"|`]
;
