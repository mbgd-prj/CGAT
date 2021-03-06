#!/usr/bin/perl -s

use File::Basename;
use File::Path;

###############################################################################
package main;

###############################################################################
$ENV{'CGAT_HOME'} = "/db/project/MBGD/CGAT" if (! $ENV{'CGAT_HOME'});

use FindBin;
use lib "$FindBin::Bin/..";

use lib "$ENV{'CGAT_HOME'}/perllib";
###############################################################################
$SYS_name		    = "Linux";
###############################################################################
$DIR_cgathome               = $ENV{'CGAT_HOME'};
$DIR_log                    = "$DIR_cgathome/log";
$DIR_build                  = "$DIR_cgathome/build";
$DIR_database               = "$DIR_cgathome/database";
$DIR_databaseWork           = "$DIR_cgathome/database.work";
$DIR_spec                   = "$DIR_database/Species";
$DIR_commands               = "$DIR_cgathome/commands";
$DIR_perllib                = "$DIR_cgathome/perllib";
$DIR_work                   = "$DIR_cgathome/work";
$DIR_tmp                    = "$DIR_work/tmp";
$DIR_query                  = "$DIR_work/query";
$DIR_blastmat               = "$DIR_cgathome/etc";
$DIR_www                    = "$DIR_cgathome/htdocs/CGAT";

$DIR_align                  = "align";
$DIR_alignSeq               = "alignSeq";
$DIR_geneattr               = "geneattr";
$DIR_segment                = "segment";

$DIR_colortab               = "$DIR_cgathome/etc/colorTab";
$DIR_guiinfo                = "$DIR_cgathome/etc/guiInfo";

$DIR_genomeseq		    = "genomes";
$DIR_genes		    = "genes";
$DIR_ntgeneseq		    = "$DIR_genes/nt";
$DIR_aageneseq		    = "$DIR_genes/aa";
$DIR_genetab		    = "$DIR_genes/tab";

###############################################################################
$FILE_RGB                   = "$DIR_cgathome/etc/rgb.txt";
$FILE_SpecListPub           = "$DIR_cgathome/etc/speclist.pub";
$FILE_SpecListUpd           = "$DIR_cgathome/etc/speclist.upd";
$FILE_NucMat		    = "$DIR_cgathome/etc/NUC.4.4";


###############################################################################
# Unix Commands
###############################################################################
$CMD_tar         		="/bin/tar";
$CMD_cp          		="/bin/cp";
$CMD_cat         		="/bin/cat";
$CMD_tee         		="/usr/bin/tee";
$CMD_sort        		="/bin/sort";
$CMD_uniq        		="/usr/bin/uniq";
$CMD_touch       		="/bin/touch";
$CMD_echo        		="/bin/echo";
$CMD_formatdb    		="/bio/bin/formatdb";
$CMD_blastall    		="/bio/bin/blastall";
$CMD_megablast   		="/bio/bin/megablast";
$CMD_fasta       		="/bio/bin/fasta";
$CMD_grep        		="/bin/grep";
$CMD_wc          		="/usr/bin/wc";
$CMD_awk         		="/bin/awk";
$CMD_rsync       		="/usr/bin/rsync";


###############################################################################
# CGAT Commands
###############################################################################
$CMD_ConcatAlign            = "$DIR_build/bin/ConcatAlign.pl";

$CMD_Kmatch                 = "$DIR_commands/binary/kmatch";
$CMD_SimpleRep                 = "$DIR_commands/binary/rep";
$CMD_usage                  = "$DIR_commands/binary/usage";

$CMD_karlinB                = "$DIR_build/bin/karlinB.pl";
$CMD_GC3                    = "$DIR_build/bin/GC3.pl";
$CMD_mkAlignSeqIndex        = "$DIR_build/bin/mkAlignSeqIndex.pl";
###############################################################################
1;#
###############################################################################
