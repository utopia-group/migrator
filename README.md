## Migrator

Migrator is a prototype for synthesizing database programs for schema refactoring.

### Publication

- Yuepeng Wang, James Dong, Rushi Shah, Isil Dillig.  
  Synthesizing Database Programs for Schema Refactoring. PLDI 2019

### Dependencies

- Java 8
- Ant 1.10.3
- Z3 4.8.5

### Build

Migrator can be built with Ant
```
ant jar
```

### Usage

After building the tool, you can use Migrator in the following way
```
./migrator.sh -srcSchema <srcSchemaFile> -tgtSchema <tgtSchemaFile> -srcProg <srcProgFile> -output <outputFile> [optional arguments]
```

For example, you can try the following command in the root directory of this repo.
```
./migrator.sh -srcSchema benchmarks/bench1/src-schema.txt -tgtSchema benchmarks/bench1/tgt-schema.txt -srcProg benchmarks/bench1/src-prog.txt -output tgt-prog.txt
```

### Tests

The tests of Migrator are built and executed with Ant
```
ant test
```

