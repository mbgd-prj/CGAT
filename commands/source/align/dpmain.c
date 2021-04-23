#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <math.h>
#include "db.h"
#include "matrix.h"
#include "pam.h"
#include "dp.h"

#ifdef USE_PVM
#include <pvm3.h>
#include "pdp.h"
#define SKIP_DBOPEN
#else
#ifdef USE_MPI
#include <mpi.h>
#include "mdp.h"
/* run program on a mpi slave node */
#define main(ac,av) dpmain(ac,av)
#define SKIP_DBOPEN
#endif
#endif


char filename1[200], filename2[200], listfile[200];
char matrixfile[200];
SeqDBflag seqdb_flag;

main(argc, argv)
     int argc;
     char *argv[];
{
    int i, j;
    int jget, jstart = 1;
    int nfile;
    int retcode, retcode2;
    int (*getfile1)(), (*getfile2)();
    int getstanfname(), getstanfnum(), getlistname(), getlistnum(); 
/*
    static char seq1[MAXI],seq2[MAXJ];
*/
    static char buf[MAXI*2];
    char c;
    DB *fp1 = NULL, *fp2 = NULL;
    Seq seq1, seq2;
    MakeAliInfo aliInfo;
    Alignment ali;

    initialize();
    alignInitialize(&aliInfo);
    strcpy(filename1, DATA_FILENAME);
    strcpy(filename2, DATA_FILENAME);
    strcpy(matrixfile, MATFILE);
    make_char2num(amino,tr);

    nfile = check_args(argc, argv, &aliInfo);
#ifndef SKIP_DBOPEN
    if (nfile == 0) {
        if ((fp1 = dbopen("stdin")) == NULL) {
            fprintf( stderr, "file %s cannot open\n", filename1 );
            exit( 1 );
        }
        read_seqset(fp1,seqdb_flag);
	fp2 = dupdb(fp1);
    } else {
        if ( ( fp1 = dbopen( filename1 ) ) == NULL ) {
                fprintf( stderr, "file %s cannot open\n", filename1 );
                exit( 1 );
        }
        read_seqset(fp1,seqdb_flag);
	if (nfile > 1) {
        	if ( (fp2 = dbopen( filename2 )) == NULL ){
               		fprintf(stderr, "file %s cannot open\n", filename2);
               		exit(1);
		}
        	read_seqset(fp2,seqdb_flag);
        } else {
		fp2 = dupdb(fp1);
	}
    }

    if (dbtype(fp1) == GENBANK && dbtype(fp2) == GENBANK) {
	strcpy(matrixfile, NUCSCOREFILE);
    }
#endif
	
    if (calc_pam) {
	aliInfo.allpam = read_allpam(pamfile);
	aliInfo.wm = weight_matrix = create_matrix(strlen(amino));
	set_matrix_from_allpam(aliInfo.allpam, initpam, aliInfo.wm, amino, tr);
	if (aliInfo.gapscale) {
		get_pamgap(initpam, &gap, &extgap);
		aliInfo.gap.init = gap;
		aliInfo.gap.ext = extgap;
if (verbose_flag == 1) {
	printf("initgap=%lf,%lf\n",gap,extgap);
}
	}
    } else {
	aliInfo.wm = weight_matrix = read_matrix(matrixfile, amino, tr);
    }


    if (verbose_flag == 1) {
        if (search == LOCAL) 
	    fprintf(stderr, "Local Search\n");
        else
	    fprintf(stderr, "Global Search\n");
    }

    if (get == ALL) {
	int flag = 0;
	if (*entname1) flag = 1;
	for (i = 1;;i++) {
	    if (flag) {
		/** name of the target sequence is specified **/
		if (flag == 1) {
		    retcode = getseq_byname(fp1,entname1,&seq1);
		    if (retcode == GETSEQ_NOTFOUND) {
			fprintf(stderr, "%s entry not found\n", entname1);
			exit(1);
		    } else if (retcode == GETSEQ_ERROR) {
			fprintf(stderr, "getseq error\n", entname1);
			exit(1);
		    }
		    flag = 2;
		}
	    } else {
		retcode = getseq_next(fp1,&seq1);
	    }
	    if (retcode == GETSEQ_END)		break;
	    else if (retcode == GETSEQ_ERROR)	continue;

	    if (nfile == 1) {
		jstart = i + 1;
	    }
	    set_dbptr(fp2,jstart-1);
	    for (j = jstart;;j++) {
		if (nfile == 0) {
			jget = 1;
		} else if (j == jstart) {
			jget = jstart;
		} else {
			jget = 1;
		}

		retcode =  getseq_next(fp2,&seq2);
		if (retcode == GETSEQ_END)		break;
		else if (retcode == GETSEQ_ERROR)	continue;

		if (verbose_flag == 1)
		    fprintf(stderr,"Trying %d,%d...\n",i,j); 
		if (match(seq1.tit, seq1.seq, seq2.tit, seq2.seq,
				&aliInfo, &ali)) {
			print_result(&ali,&aliInfo);
		}
	    }
	    if ( dbrewind(fp2) != 0) {
		break;
	    }
	}
    } else if (get == ALTER) {
	while (1) {
	    int contflag = 0;
	    retcode = getseq_next(fp1,&seq1);
	    if (retcode == GETSEQ_END) break;
	    else if (retcode == GETSEQ_ERROR) contflag = 1;
	    if (nfile == 2) {
	    	retcode = getseq_next(fp2,&seq2);
	    } else {
	    	retcode = getseq_next(fp1,&seq2);
	    }
	    if (retcode == GETSEQ_END) break;
	    else if (retcode == GETSEQ_ERROR) contflag = 1;
	    if (contflag) continue;

	    if (match(seq1.tit, seq1.seq, seq2.tit, seq2.seq,
				&aliInfo, &ali)){
		    print_result(&ali,&aliInfo);
	    }
	}
    } else if (get == NUM || get == NAME) {
	FILE *lfp;
	char sbuf[BUFSIZ];
	if (! *listfile) {
		lfp = stdin;
	} else if ((lfp = fopen(listfile, "r")) == NULL) {
		fprintf(stderr, "Can't open file: %s\n", listfile);
		exit(1);
	}
	while (fgets(sbuf, BUFSIZ, lfp)) {
	    if (get == NUM) {
		if (sscanf(sbuf, "%d%d", &entnum1, &entnum2) < 2) {
			continue;
		}
	    	retcode = getseq_bynum(fp1,entnum1,&seq1);
	    	retcode2 = getseq_bynum(fp2,entnum2,&seq2);
	    } else {
		if (sscanf(sbuf, "%s%s", &entname1, &entname2) < 2) {
			continue;
		}
	    	retcode = getseq_byname(fp1,entname1,&seq1);
	    	retcode2 = getseq_byname(fp2,entname2,&seq2);
	    }
	    if (retcode == GETSEQ_END || retcode2 == GETSEQ_END) {
		fprintf(stderr, "No such entry\n");
		continue;
	    } else if (retcode == GETSEQ_ERROR || retcode2 == GETSEQ_ERROR) {
		continue;
	    }
	    if (match(seq1.tit, seq1.seq, seq2.tit, seq2.seq,
			&aliInfo, &ali)) {
		    print_result(&ali,&aliInfo);
	    }
/*
	    dbrewind(fp1);
	    dbrewind(fp2);
*/
	} while(scan);
    }
#ifdef USE_PVM
#define OUTBUFSIZ 300000
    else if (get == PVM) {
	int mytid = pvm_mytid();
	int ptid = pvm_parent();
	int bufid;
	int ok = PdpMsg_BEGIN;
	int ng = PdpMsg_NG;
	int dnum = 0, i;
	static char outbuf[OUTBUFSIZ];
	static char entname1[MAXNAMELEN], entname2[MAXNAMELEN];
    	static char seq1[MAXI],seq2[MAXJ];

	while (1) {
	    pvm_initsend(PvmDataDefault);
	    pvm_pkint(&dnum, 1, 1);
	    pvm_send(ptid, PdpMsg_ResultSize);

	    bufid = pvm_recv(-1, PdpMsg_DataSize);
	    pvm_upkint(&dnum, 1, 1); 
	    if (dnum < 0) {
		break;
	    }

	    pvm_initsend(PvmDataDefault);

	    bufid = pvm_recv(-1, PdpMsg_Data);
	    for (i = 0; i < dnum; i++) {
		    pvm_upkstr(entname1); 
		    pvm_upkstr(seq1); 
		    pvm_upkstr(entname2); 
		    pvm_upkstr(seq2); 
		    outbuf[0] = '\0';
		    if (match(entname1, seq1, entname2, seq2,
				&aliInfo, &ali)) {
		    	sprint_result(&ali, outbuf, &aliInfo);
		    } else {
			sprintf(outbuf,
				"# align failed: %s %s\n",entname1,entname2);
		    }
		    pvm_pkstr(outbuf);
	    }
	    pvm_send(ptid, PdpMsg_Result);
	}
	exit(0);
    }
#else
#ifdef USE_MPI
#define OUTBUFSIZ 300000
    else if (get == MPI) {
	int my_rank, master_rank = 0;
	int bufid;
	int datasize = 0, i;
	char *p;
	static char outbuf[OUTBUFSIZ];
	static char seq_buf[SEQ_BUF_SIZ];
	static char result_buf[RESULT_BUF_SIZ];
	char *entname1, *entname2;
    	char *seq1,*seq2;
	MPI_Status status;

	MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

	/** the first message [datasize=0] means the process ready
			for accepting input data **/
	MPI_Send(&datasize, 1, MPI_INT, MasterRank, Mdp_TagResSize,
			MPI_COMM_WORLD);

	while (1) {

/*
fprintf(stderr, "send[%d]>>>%d\n",my_rank,datasize);
*/
	    MPI_Recv(&datasize, 1, MPI_INT, MasterRank, Mdp_TagSeqSize,
			MPI_COMM_WORLD, &status);
/*
fprintf(stderr, "recv[%d]>>>%d\n",my_rank,datasize);
*/
	    if (datasize < 0) {
		/* end of file */
		break;
	    }

	    MPI_Recv(&seq_buf, datasize, MPI_CHAR, MasterRank, Mdp_TagSeqData,
			MPI_COMM_WORLD, &status);
				
	    result_buf[0] = '\0';
	    p = seq_buf;
	    while ( 1 ) {
		    if ( (entname1 = strtok(p, DATA_SEP_CHAR_STR)) ==NULL){
			break;
		    }
		    p = NULL;
		    seq1 = strtok(p, DATA_SEP_CHAR_STR);
		    entname2 =strtok(p, DATA_SEP_CHAR_STR);
		    seq2 = strtok(p, DATA_SEP_CHAR_STR);

		    outbuf[0] = '\0';
/*
printf("TRY DP[%d]: ",my_rank);
printf("%s[%d],%s[%d]\n",entname1,strlen(seq1),entname2,strlen(seq2));
*/
		    if (match(entname1, seq1, entname2, seq2,
				&aliInfo, &ali)) {
		    	sprint_result(&ali, outbuf, &aliInfo);
			strcat(&outbuf[strlen(outbuf)], "\n");
		    } else {
			sprintf(outbuf,
				"# align failed: %s %s\n",entname1,entname2);
		    }
		    strcat(result_buf, outbuf);
	    }
	    datasize = strlen(result_buf) + 1;
	    MPI_Send(&datasize, 1, MPI_INT, MasterRank,
			Mdp_TagResSize, MPI_COMM_WORLD);
	    MPI_Send(&result_buf, datasize, MPI_CHAR, MasterRank,
			Mdp_TagResData, MPI_COMM_WORLD);
	}
	/** exit dpmain **/
	return(0);
    }
#endif
#endif
    
    dbclose( fp1 );
    dbclose( fp2 );
    
    if (verbose_flag)
        fprintf(stderr,"The End of DP.\n");
    
    exit(0);
}

initialize()
{
	display = NO;
	search = GLOBAL;
	get = ALL;
	scan = 0;
	count_or_not = NOCOUNT;
	gap = GAP;
	edgegap = EDGEGAP;
	extgap = EXTGAP;
	edgepath1 = 1;
	edgepath3 = 3;
	threshold = BADVALUE;
	amino = AMINO;
	initpam = 250;
#ifdef USE_PVM
	get = PVM;
	separate = ' ';
#else
#ifdef USE_MPI
	get = MPI;
	separate = ' ';
#endif
#endif
}

check_args(int argc, char **argv, MakeAliInfo *aliInfo)
{
    int i;
    int nfile = 0;
    int digit_flag = 0;
    char *filename[2];
    
    filename[0] = filename1;
    filename[1] = filename2;

    if (argc <= 1) {
	usage(argv[0]);
	exit(1);
    }
    else  {
	i = 0;

      option_check:
	while (++i < argc) {
	    if (*argv[i] == '-') {
		while (*++argv[i] != '\0') {
		    switch (*argv[i]) {
		      case 'd': 
			display = YES;
			aliInfo->disp_align = 1;
			break;
		      case 'l':
			search = LOCAL;
			aliInfo->local = 1;
			edgegap = 0;
			edgepath1 = 0;
			edgepath3 = 0;
			break;
		      case 'i':
			aliInfo->gap.init = -AtoScore(++argv[i]);
			break;
		      case 'g':
			gap = AtoScore(++argv[i]);
			if (BETTER(gap,0)) {
				gap *= -1;
			}
			aliInfo->gap.init = gap;
			digit_flag = 1;
			break;
		      case 'G':
			aliInfo->gapscale = gapscale = 1;

/*** commented out OLD
			Gonnet, Cohen, Benner  Science 1992
			constgap = 39.21; pamgap = 7.75; lengap = 1.65;
commented out ****/

/*	Benner, Cohen, Gonnet  JMB 229,1065 (1993) [eq.12] */
/* gap(len,PAM) = -constgap + pamgap*log10(PAM) - lengap*(len-1) */
/*** commented out
			constgap = 37.31; pamgap = 6.88; lengap = 1.47;
commented out ****/

			if (*(argv[i]+1)) {
				char *tmp;
				Score constgap, pamgap,lengap;
				if ((tmp = strtok(++argv[i], ",")) != NULL) {
					constgap = (Score)atof(tmp);
				    if ((tmp = strtok(NULL, ",")) != NULL) {
					pamgap = (Score)atof(tmp);
					if ((tmp = strtok(NULL, ",")) != NULL) {
						lengap = (Score)atof(tmp);
					}
				    }
				}
				argv[i] = tmp + strlen(tmp) - 1;
				set_gapscale(constgap, pamgap, lengap);
			}

			gotoh_gap_flag = 1;
			break;
		      case 'e':
			edgegap = AtoScore(++argv[i]);
			if (BETTER(edgegap,0)) {
				edgegap *= -1;
			}
			if (edgegap) {
			    count_or_not = NOCOUNT;
			}
			aliInfo->gap.edge_init = edgegap;
			aliInfo->noedgegap = 0;
			digit_flag = 1;
			break;
		      case 'x':
			extgap = AtoScore(++argv[i]);
			if (BETTER(extgap,0)) {
				extgap *= -1;
			}
			aliInfo->gap.ext = extgap;
			gotoh_gap_flag = 1;
			digit_flag = 1;
			break;
		      case 't':
			threshold = AtoScore(++argv[i]);
			digit_flag = 1;
			break;
		      case 'T':
			title_flag = 1;
			break;
		      case 'a':
			get = ALL;
			break;
		      case 'A':
			get = ALTER;
			break;
/**
#ifdef USE_PVM
		      case 'V':
			get = PVM;
			break;
#endif
**/
		      case 'n':
			if (i+1 < argc) {
			    strcpy(listfile, argv[++i]);
			}
			get = NUM;
			goto option_check;
		      case 'N':
			if (i+1 < argc) {
			    strcpy(listfile, argv[++i]);
			}
			get = NAME;
			seqdb_flag |= FLAG_ADDHASH;
			goto option_check;
		      case 's':
			if (*(argv[i]+1) && *(argv[i]+1) < 'A') {
			    separate = *++argv[i];
			} else {
			    separate = ' ';
			}
			aliInfo->separate = separate;
			break;
		      case 'h':
			usage(argv[0]);
			fprintf(stderr, "options : d display alignment, l local search, D align sequences as DNA\n");
			fprintf(stderr, "	p<mode> print path, h help\n");
			fprintf(stderr, "	Default: gap=8, edge_gap=0, gap_extension=gap, score=Dayhoff's PAM250\n");
			fprintf(stderr, "	print_path_mode 1-PATH,2-WEIGHT,4-GOTOH'S PATH,8-GOTOH'S WEIGHT\n");
			exit(0);
		      case 'm':
			if (i+1 >= argc) {
				usage(argv[0]);
				exit(0);
			}
			strcpy(matrixfile, argv[++i]);
			goto option_check;
			break;
		      case 'D':
/*
			strcpy(matrixfile, NUCSCOREFILE);
*/
			DEBUG=1;
			break;
		      case 'v':
			verbose_flag = 1;
			break;
		      case 'w':
			if ( isdigit( *(++argv[i]) ) ) {
				walimode = (short) atoi(++argv[i]);
				if (walimode == 0) {
					walimode = 1;
				}
			} else if (*argv[i] == 'l') {
				/* output line length */
				wline = atoi(++argv[i]);
				while (*argv[i] >= '0' && *argv[i] <= '9')
					argv[i]++;
				argv[i]--;
			}
			break;
		      case 'W':
			if (*(argv[i]+1) == '.') {
				winsizerate = atof(++argv[i]);
				argv[i]++;
			} else {
				winsize = atoi(++argv[i]);
			}
			while (*argv[i] >= '0' && *argv[i] <= '9')
				argv[i]++;
			argv[i]--;
			break;
		      case 'F':
			maxdiffrate = atof(++argv[i]);
			if (*argv[i] == '.') {
				argv[i]++;
			}
			while (*argv[i] >= '0' && *argv[i] <= '9')
				argv[i]++;
			argv[i]--;
			break;
		      case 'L':
			aliInfo->lsdp = 1;
			break;
		      case 'p':
			path_flag = (short) atoi(++argv[i]);
			if (!path_flag) path_flag = 1;
			digit_flag = 1;
			break;
		      case 'P':
			if (! calc_pam) {
				/* default */
				calc_pam |= 1;
/**
				calc_pam |= CALC_PAM_EXPPAM;
**/
			}
			if (*(argv[i]+1) == 'e') {
				calc_pam |= CALC_PAM_EXPPAM;
			} else if (*(argv[i]+1) == 'M') {
				calc_pam &= ~CALC_PAM_EXPPAM;
			} else if (*(argv[i]+1) == '0') {
				calc_pam |= CALC_PAM_ONEITER;
				argv[i]++;
			} else if (*(argv[i]+1) == 's') {
				char *p;
				argv[i]++;
				pam_stop = strtod(++argv[i], &p);
				argv[i] = p-1;
			} else if (isdigit( *(argv[i]+1) )) {
				initpam = atoi(++argv[i]);
				while (*argv[i] >= '0' && *argv[i] <= '9')
					argv[i]++;
				argv[i]--;
			}
			break;
		      case '\0':
			strcpy(filename[nfile], "stdin");
			break;
		      default:
			usage(argv[0]);
			exit(0);
		    }
		    if (digit_flag) {
			while (*argv[i] >= '0' && *argv[i] <= '9'
			    || *argv[i] == '-' || *argv[i] == '.')
				++argv[i];
			--argv[i];
			digit_flag = 0;
		    }
		}
	    } else { /* *argv[i] != '-' */
		if (nfile >= 2) {
		    usage(argv[0]);
		    exit(1);
		} else {
		    strcpy(filename[nfile], argv[i]);
		    if (verbose_flag)
		        fprintf(stderr, "%d,%s\n", nfile, filename[nfile]);
		    nfile++;
		}
	    }
	}
    }
/***
    if (nfile == 1)
	strcpy(filename[1], filename[0]);
***/
    if (verbose_flag) {
	fprintf(stderr,"gap = %d\n", (int)gap);
	fprintf(stderr,"edgegap = %d\n", (int)edgegap);
	if (gotoh_gap_flag)
		fprintf(stderr,"extgap = %d\n", (int)extgap);
    }
    if (strcmp(entname2,"-") == 0){
	get = ALL;
	nfile = 2;
    }
    return nfile;
}

usage(cmdname)
     char *cmdname;
{
    fprintf(stderr, "Usage : %s <filename1> [ [-dlhDp] [-m <scorefile>]\n",cmdname);
    fprintf(stderr, "	[-g<gap>] [-e<edgegap>] [-x<gap extension>]\n"); 
    fprintf(stderr, " 	[-n <num1> <num2>][-N <entname1> <entname2>] ]\n");
}

