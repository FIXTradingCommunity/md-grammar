# md-grammar

Parser and writer for Markdown documents. 

The markdown grammar follows [GitHub Flavored Markdown Spec](https://github.github.com/gfm/). 

## Supported markdown features

### Leaf blocks

* ATX headings
* Fenced code blocks -- a fenced code block has an optional infostring that tells the language of the code block. Originally, language was for programming language of the code block to support syntax-specific formatting. However, language has expanded to include various file types. See the list of [languages recognized by GitHub](https://github.com/github/linguist/blob/master/lib/linguist/languages.yml).
* Paragraphs
* Tables
* Block quotes
* Lists

### Unsupported

The following markdown features are currently unsupported.

* Thematic breaks
* Setext headings
* HTML blocks
* Link reference definitions

## Markdown extension for file import

The markdown grammar has been extended to support importing an external file to populate a fenced code block. Following the opening fence, markdown
specifies an optional [infostring](https://github.github.com/gfm/#info-string) to describe the class of the block contents. Markdown processors do not validate an infostring, but typically use it has a hint to control syntax highlighting. In this implementation, infostring has been extended with syntax to specify importing of a file or portion of a file. The motivation for this extension is to support snippets of technical code.  Imports remain dynamic rather than frozen by cut and paste. Such snippets can be generated, updated, and validated by external tools. The expected benefit is timeliness and accuracy.

### Extended infostring syntax

The original infostring as a class of data is still supported. Optionally, it may be extended with an `import` clause followed by a filename to import an entire file. If only a portion of a file is desired, it can either be specified as a range of line numbers or by the user of a search strings for the start and end of the file portion. 

Syntax summary:

```
<class>
<class> import <filename>
<class> import <filename> <beginlineno> - <endlineno>
<class> import <filename> from "beginstring" to "endstring"
```

* `import`, `from` and `to` are literal keywords. A hyphen `-` may be used as a synonym for `to`. The `from` keyword is optional.
* Line number ranges are inclusive. 
* Search strings are delimited by double quote `"` while line numbers consist of digits without a delimiter.
* The end search string matches the first instance of the string after the begin search is matched.
* If no end line number or end search string is given, then the remainder of the file is included.

## Prerequisites
This project requires Java 11 or later. It should run on any platform for which a JVM is supported. Several open-source JDK implementations are available, including [Eclipse Temurin](https://adoptium.net/) and [Azul Zulu](https://www.azul.com/downloads/?package=jdk).

## Build
The project is built with Maven version 3.0 or later.

## License
© Copyright 2020-2021 FIX Protocol Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
