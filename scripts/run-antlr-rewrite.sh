#!/bin/bash -e

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
working_path="$parent_path/../src/migrator/rewrite/parser"
antlr4="java -Xmx500M -cp $parent_path/../lib/antlr-4.6.jar org.antlr.v4.Tool"

# run antlr4
$antlr4 $parent_path/../dsl/RewriteAntlrDsl.g4 -no-listener -visitor -o $working_path

# enter working directory
cd "$working_path"

# delete redundant files
rm "RewriteAntlrDsl.tokens"
rm "RewriteAntlrDslLexer.tokens"
rm "RewriteAntlrDslBaseVisitor.java"

# replace first_line of remaining files
first_line="\/\/ Generated from RewriteAntlrDsl.g4 by ANTLR 4.6"
sed -i "1s/.*/$first_line/" RewriteAntlrDslParser.java
sed -i "1s/.*/$first_line/" RewriteAntlrDslLexer.java
sed -i "1s/.*/$first_line/" RewriteAntlrDslVisitor.java

