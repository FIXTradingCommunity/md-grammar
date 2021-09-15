/* Grammar for a subset of markdown */
parser grammar MarkdownParser;

options { tokenVocab=MarkdownLexer; }

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
	OPEN_FENCE infostring? FENCED_IGNORE_WS? importspec? FENCED_NEWLINE
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
