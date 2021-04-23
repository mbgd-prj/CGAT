#!/bin/csh
setenv	CGAT_HOME	/db/project/MBGD/CGAT
setenv	CGAT_PERLLIB		"${CGAT_HOME}/perllib:${CGAT_HOME}/commands"
if ($?PERL5LIB) then
	setenv	PERL5LIB		"${CGAT_PERLLIB}:${PERL5LIB}"
else if ($?PERLLIB) then
	setenv	PERLLIB		"${CGAT_PERLLIB}:${PERLLIB}"
else
	setenv	PERLLIB		"${CGAT_PERLLIB}"
endif
setenv PATH	"${CGAT_HOME}/build/bin:${CGAT_HOME}/commands:${CGAT_HOME}/commands/binary:${PATH}"
