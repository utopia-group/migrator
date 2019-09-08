#!/bin/bash -e

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
working_path="$parent_path/../src/migrator/parser"
antlr4="java -Xmx500M -cp $parent_path/../lib/antlr-4.6.jar org.antlr.v4.Tool"

# run antlr4
$antlr4 $parent_path/../dsl/AntlrDsl.g4 -no-listener -visitor -o $working_path

# enter working directory
cd "$working_path"

# delete redundant files
rm "AntlrDsl.tokens"
rm "AntlrDslLexer.tokens"
rm "AntlrDslBaseVisitor.java"

# replace first_line of remaining files
first_line="\/\/ Generated from AntlrDsl.g4 by ANTLR 4.6" 
sed -i "1s/.*/$first_line/" AntlrDslParser.java
sed -i "1s/.*/$first_line/" AntlrDslLexer.java
sed -i "1s/.*/$first_line/" AntlrDslVisitor.java

