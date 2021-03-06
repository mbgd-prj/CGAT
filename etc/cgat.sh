#!/bin/sh
CGAT_HOME=/db/project/MBGD/CGAT; export CGAT_HOME
CGAT_PERLLIB="${CGAT_HOME}/perllib:${CGAT_HOME}/commands"; export CGAT_PERLLIB
if [ $?PERL5LIB ]; then
	PERL5LIB="${CGAT_PERLLIB}:${PERL5LIB}"; export PERL5LIB
elif [ $?PERLLIB ]; then
	PERLLIB="${CGAT_PERLLIB}:${PERLLIB}"; export PERLLIB
else
	PERLLIB="${CGAT_PERLLIB}"; export PERLLIB
fi
PATH="${CGAT_HOME}/build/bin:${CGAT_HOME}/commands:${CGAT_HOME}/commands/binary:${PATH}"; export PATH
