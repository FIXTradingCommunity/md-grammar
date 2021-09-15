lexer grammar MarkdownLexer;

@header {
/*
 * Copyright 2021 FIX Protocol Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
}

/* default mode */

LITERAL
:
	BACKTICK ' '? LITERALCHAR+ ' '? BACKTICK
;

HEADINGLINE
:
	HASH+ ' '? LINECHAR+
;

QUOTELINE
:
	GT ' '? ( LINECHAR+ | LITERAL)+
;

LISTLINE
:
	WS*
	(
		BULLET
		| LISTNUMBER
	) WS+ LINECHAR*
;

PARAGRAPHLINE
:
	INITIALPARACHAR (LINECHAR+ | LITERAL)*
;

TABLEDELIMINATORCELL
:
	PIPE? ' '? ':'? '-'+ ':'? ' '?
;

OPEN_FENCE
:
	'```' -> mode(FENCED)
;

IGNORE_WS
:
	WS -> skip
;

NEWLINE
:
	'\r'? '\n'
;

/* low priority since it can match empty string */
CELLTEXT
:
	PIPE IGNORE_WS (CELLCHAR+ | LITERAL)*
;

/* Unescaped backtick character; not preceded by backslash */
BACKTICK
:
	{_input.LA(-1) != 92}?

	'`'
;

/* Unescaped greater-than character; not preceded by backslash */
GT
:
	{_input.LA(-1) != 92}?

	'>'
;

/* Unescaped hash character; not preceded by backslash */
HASH
:
	{_input.LA(-1) != 92}?

	'#'
;

/* Unescaped pipe character; not preceded by backslash */
PIPE
:
	{_input.LA(-1) != 92}?

	'|'
;

/* disallow unescaped pipe, newline, literal within a table cell */
fragment
CELLCHAR
:
	(
		ESCAPEDCHAR
		| ESCAPEDPIPE
		| WS
		| ALPHANUMERIC
		| PUNCTUATION
	)
;

/* Escaped punctuation, includes escaped backslash */
fragment
ESCAPEDCHAR
:
	'\\' [\\!"#$%&'()*+,\-./:;<=>?@[\]^_{|}~`]
;

fragment
ALPHANUMERIC
:
	[a-zA-Z0-9\u0080-\uFFFF]
;

fragment
PUNCTUATION
:
	[!"#$%&'()*+,\-./:;<=>?@[\]^_{}]
;

fragment
ESCAPEDPIPE
:
	{_input.LA(-1) == 92}?
	'|'
;

fragment
WS
:
	[ \t]
;

fragment
LISTNUMBER
:
	[1-9] [.)]
;

fragment
BULLET
:
	[-+*]
;

fragment
INITIALPARACHAR
:
	~[#>|\n\r]
;

fragment
LITERALCHAR
:
	~[`]
;

fragment
LINECHAR
:
	~[\n\r`]
;

mode FENCED;

CLOSE_FENCE
:
	'```' (FENCED_NEWLINE | EOF) -> mode(DEFAULT_MODE)
;

TEXTLINE
:
	INITIALTEXTCHAR TEXTCHAR* FENCED_NEWLINE
;


INITIALTEXTCHAR
:
	{_input.LA(-1) == 10 || _input.LA(-1) == 13}?
	~[\n\r]
;

LINENUMBER
:
	DIGIT+
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

STRING
:	
	'"' LINECHAR*? '"' 
;

WORD
:
	WORDCHAR+
;

FENCED_NEWLINE
:
	'\r'? '\n' 
;

FENCED_IGNORE_WS
:
	WS -> skip
;

fragment
DIGIT
:
	[0-9]
;

fragment
WORDCHAR
:
	~[\n\r\t |`]
;

fragment
TEXTCHAR
:
	~[\n\r]
;
