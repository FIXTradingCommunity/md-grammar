/* Grammar for a subset of markdown */
parser grammar MarkdownParser;

options { tokenVocab=MarkdownLexer; }

document
:
	block+ EOF
;

block
:
	heading
	| paragraph
	| list
	| blockquote
	| fencedcodeblock
	| table
	| NEWLINE
;

heading
:
	NEWLINE* HEADINGLINE NEWLINE
;

paragraph
:
	NEWLINE* paragraphline+
;

paragraphline
:
	PARAGRAPHLINE
	(
		NEWLINE
		| EOF
	)
;

list
:
	NEWLINE* listline+
;

listline
:
	LISTLINE
	(
		NEWLINE
		| EOF
	)
;

blockquote
:
	NEWLINE* quoteline+
;

quoteline
:
	QUOTELINE
	(
		NEWLINE
		| EOF
	)
;

fencedcodeblock
:
	OPEN_FENCE infostring? FENCED_IGNORE_WS? importspec? NEWLINE
	textline*
	CLOSE_FENCE
;

textline
:
	TEXTLINE
;

infostring
:
	WORD
;

importspec
:
	IMPORT FENCED_IGNORE_WS? path FENCED_IGNORE_WS? (start FENCED_IGNORE_WS? end?)?
;

path
:
	WORD | STRING
;

start
:
	FROM? FENCED_IGNORE_WS? location
;

end
:
	TO FENCED_IGNORE_WS? location
;

location
:
	LINENUMBER | STRING
;


table
:
	NEWLINE* tableheading tabledelimiterrow tablerow+
;

tableheading
:
	tablerow
;

tablerow
:
	cell+ PIPE?
	(
		NEWLINE
		| EOF
	)
;

cell
:
	CELLTEXT
;

tabledelimiterrow
:
	TABLEDELIMINATORCELL+ PIPE? NEWLINE
;
