#!/bin/bash -e

enter_working_dir() {
    parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
    working_path="$parent_path/.."
    cd "$working_path"
}

build_migrator() {
    ant
    ant jar
}

run_migrator_bench_translator() {
    java -classpath migrator.jar:lib/antlr-4.6.jar:lib/gson-2.8.0.jar:lib/sat4j-maxsat.jar migrator.bench.Translator $1 $2 $3 $4
}

translate_benchmark() {
    index="$1"
    benchmark_dir="benchmarks/bench$1"
    legacy_bench_path="benchmarks/legacy/benchmark$1.json"
    src_schema_path="$benchmark_dir/src-schema.txt"
    tgt_schema_path="$benchmark_dir/tgt-schema.txt"
    src_prog_path="$benchmark_dir/src-prog.txt"
    run_migrator_bench_translator $legacy_bench_path $src_schema_path $tgt_schema_path $src_prog_path
}

main() {
    enter_working_dir
    build_migrator
    for i in `seq 1 20`; do
        echo "translating benchmark $i"
        translate_benchmark $i
    done
}

main

