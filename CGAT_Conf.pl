#!/usr/local/bin/perl -s

###############################################################################
require "CGAT_commonPath.pl";

###############################################################################
# 以下のデータは main パッケージに配置する
package main;

###############################################################################
# システム共通パラメータ
###############################################################################
# htblast 関連
$NUM_cpu             =    8;               # 処理マシンの CPU の数
$PRAM_QueryLen       = 5000;            # 分割時の配列長
$PRAM_OVLP           =  500;            # オーバーラップさせる配列長
$PRAM_QueryNum       =  200;            # 一度にホモロジー検索する時の配列数

$PRAM_Bl2seqOpt      = '-p blastn -F F';#
$PRAM_Alignmentcmd  = '-BL2SEQ';        # 再アライメント取得の際のプログラム
###############################################################################
# seg 関連のパラーメータ
###############################################################################
# ProcSelf
$PRAM_SLF_BlastOpt      = "-F F -v 1500 -b 1500 -e 50.0";
$PRAM_SLF_BlastCutoff   = join(" ", "-DIR=BOTH",
                                    "-INTERVAL=0");
$PRAM_SLF_ConcatOpt     = join(" ", "-NEAREST_PAIR_ONLY=0");
$PRAM_SLF_ConvalignOpt  = join(" ", "");
$PRAM_SLF_SegcountOpt   = join(" ", "-CUTOFF=10",
                                    "-MINLEN=10");

###############################################################################
# ProcIS
$PRAM_IS_FastaOpt       = "-m 10 -N 0";
$PRAM_IS_FastaKtup      = 4;
$PRAM_IS_FastaCutoff    = join(" ", "-EVAL_THRE=0.0001",
                                    "-out_name");
$PRAM_IS_ConcatOpt      = join(" ", "-CONNECT_OVLP=1",
                                    "-NEAREST_PAIR_ONLY=0",
                                    "-CHK_NAME",
                                    "-out_name");
$PRAM_IS_ConvalignOpt   = join(" ", "");


###############################################################################
# align 関連のパラーメータ
###############################################################################
# ProcBlast
$PRAM_BL_BlastOpt       = "-F F -v 1500 -b 1500 -e 50.0";
$PRAM_BL_BlastCutoff    = join(" ", "-DIR=BOTH",
                                    "-INTERVAL=0");
$PRAM_BL_ConcatOpt      = join(" ", "-NEAREST_PAIR_ONLY=0");
$PRAM_BL_ConvalignOpt   = join(" ", "");
$PRAM_BL_CompfiltOpt    = join(" ", "");


###############################################################################
# ProcFasta
$PRAM_FAS_NOPT = $PRAM_QueryLen * 2;
$PRAM_FAS_FastaOpt      = "-m 10 -N $PRAM_FAS_NOPT";
$PRAM_FAS_FastaKtup     = 6;          #DNA default
$PRAM_FAS_FastaCutoff   = join(" ", "");
$PRAM_FAS_ConcatOpt     = join(" ", "-CONNECT_OVLP=1",
                                    "-NEAREST_PAIR_ONLY=0");
$PRAM_FAS_ConvalignOpt  = join(" ", "");
$PRAM_FAS_CompfiltOpt   = join(" ", "");

###############################################################################
1;#
###############################################################################
