#!/bin/bash
echo "Code coverage:"
cat ./reports/coverage-report.csv | \
grep "us.kbase" | grep -v "{...}" | \
grep -v "us.kbase.narrativemethodstore" | \
grep -v "us.kbase.catalog" | \
awk -F ',' '{ cov+=$5; all+=$4+$5; print 100*$5/($4+$5)"\t"$2"."$3, "(covered="$5"/"($4+$5)")" } END { print "Common coverage: "(100*cov/all)"("cov"/"all")" }' | \
grep -v ".test." | \
sort -k1,1nr