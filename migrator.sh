#!/bin/bash

java -Xmx32g -classpath migrator.jar:lib/antlr-4.6.jar:lib/gson-2.8.0.jar:lib/sat4j-maxsat.jar:lib/mediator-0.1.jar:lib/z3.jar:lib/args4j-2.33.jar migrator.Migrator $@

