#include <stdio.h>
#include <mpi.h>
#include <signal.h>
#include <strings.h>
#include <string.h>
#include <unistd.h>
#include "db.h"
#include "mdp.h"

#define MAXSEQNUM 500
#define MAXNAMELEN 100
#define MAXLINELEN 500
#define BLOCKSIZE 500000
#define PROGNAME "pdpc"
#define MAXHOSTS 140
/** defined in mdp.h
#define RESULT_BUF_SIZ 1000000
#define SEQ_BUF_SIZ 2000000
**/

char *hostfile;
char *hosts[MAXHOSTS];
int procnum[MAXHOSTS];
int numhosts = 0;
int numarch = 0;
int addhost = 0;
int default_procnum = 1;
char verbose = 0;

char currenthost[MAXNAMELEN];
char *args[100] = {"-l", "-P0", "-G", "-s ", 0};

int blocksize = BLOCKSIZE;

Seq seq[MAXSEQNUM];
char seqfile[MAXNAMELEN] = "stdin";
char listfile[MAXNAMELEN];
char outbuf[MAXLINELEN];
int infos[MAXHOSTS];
int skip;
DB *db;
int tids[MAXHOSTS];
int ntask;
int __ExitFlag;
int Status;
char *StatusStr[] = {
	"", "probe_child", "get_result", "send_data",
};
char result_buf[RESULT_BUF_SIZ];
char seq_buf[SEQ_BUF_SIZ];


struct pvmhostinfo *hostp;

main(int argc, char **argv)
{
	int my_rank;
	void sigterm_handler(int sig), sigusr_handler(int sig),
		sigkill_handler(int sig);

	signal(SIGINT, sigterm_handler);
	signal(SIGTERM, sigterm_handler);
	signal(SIGQUIT, sigterm_handler);
	signal(SIGUSR2, sigusr_handler);
	signal(SIGKILL, sigkill_handler);

	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
	MPI_Comm_size(MPI_COMM_WORLD, &ntask);
	mdp_get_args(argc, argv);
	if (my_rank == MasterRank) {
		throw_dpcmd();
	} else {
		argc = 1;
		while (args[argc-1]) {
			argv[argc] = args[argc-1];
			argc++;
		}
		dpmain(argc, argv);
	}
	MPI_Finalize();
}

throw_dpcmd()
{
	int size = 0;
	int sqn = 0, datanum;
	int target;
	int ret;
	int ln = 0;
	FILE *lfp = NULL;

	if ( ! (db = dbopen(seqfile)) ) {
		fprintf(stderr, "Can't open file: %s\n", seqfile);
		exit(1);
	}

	if (*listfile) {
		if ((lfp = fopen(listfile, "r")) == NULL) {
			fprintf(stderr, "Can't open listfile: %s\n", listfile);
			exit(1);
		}
		read_seqset(db, FLAG_ADDHASH);
	} else {
		read_seqset(db, 0);
	}
	while ((ret = get_seqpair(db, lfp, &seq[sqn], &seq[sqn+1])) >= 0) {
		if (ret == 0) continue;
		if (skip && ++ln <= skip) {
			/** skip first lines **/
			continue;
		}
		size += strlen(seq[sqn].seq) * strlen(seq[sqn+1].seq);
		if (size >= blocksize || sqn+2 >= MAXSEQNUM-1) {
			target = MPI_ANY_SOURCE;
			get_result(&target);

			send_data(target, sqn+2, seq);

/**
			target =  (target+1) % ntask;
**/
			size = sqn = 0;
			if (__ExitFlag == 1) {
printf("EXIT!!!!\n");
				break;
			}
		} else {
			sqn+=2;
		}
	}

	if (verbose) {
		fprintf(stderr, "Last Trial\n");
	}

	/** Last Trial **/
	if (sqn/2 >= 1) {
		target = MPI_ANY_SOURCE;
		get_result(&target);
/*
printf("finaltarget: %d,%d\n",target,sqn);
*/

		send_data(target, (sqn/2)*2, seq);
	}

	if (verbose) {
		printf("Last Loop\n");
	}

	datanum = -1;	/** end of data **/
	for (target = 0; target < ntask; target++) {
		if (target == MasterRank) continue;

/*
printf("Last: %d\n",target);
*/
		get_result(&target);

/*
printf("LastOK: %d\n",target);
*/
		if (verbose) {
			printf("End: target=%d\n",target);
		}
		MPI_Send(&datanum, 1, MPI_INT, target, Mdp_TagSeqSize, MPI_COMM_WORLD);
	}
}
#ifdef AAA
get_seqpair(DB *db, FILE *lfp, Seq *seq1, Seq *seq2)
{
	static char entname1[MAXNAMELEN], entname2[MAXNAMELEN];
	static char buf[BUFSIZ];
	if (lfp) {
		if (fgets(buf, BUFSIZ, lfp) == NULL) {
			/** EOF **/
			return -1;
		}
		if (sscanf(buf, "%s%s", entname1, entname2) < 2) {
			/** read error **/
			return 0;
		}
		if (getseq_byname(db, entname1, seq1) < 0 || getseq_byname(db, entname2, seq2) < 0) {
			/** read error **/
			return 0;
		}
	} else {
		if (getseq_next(db, seq1) < 0 || getseq_next(db, seq2) < 0) {
			/** EOF **/
			return -1;
		}
	}
	return 1;
}
#endif

probe_child(int *tidx, int tag, MPI_Comm comm)
{
	int info;
	MPI_Status status;

	Status = 1;
	while ((MPI_Probe(tids[*tidx], tag, comm, &status)) <= 0) {
		*tidx =  (*tidx+1) % ntask;
	}
}
get_result(int *target)
{
	int i;
	int datasize;
	MPI_Status status;

	Status = 2;
	MPI_Recv(&datasize, 1, MPI_INT, *target, Mdp_TagResSize,
		MPI_COMM_WORLD, &status);
	*target = status.MPI_SOURCE;
#ifdef DEBUG
printf("Recieved: target=%d, datasize=%d\n", *target, datasize);
#endif
	if (datasize) {
		MPI_Recv(result_buf, datasize, MPI_CHAR, *target, Mdp_TagResData,
			MPI_COMM_WORLD, &status);

		printf("%s", result_buf);
	}
}
send_data(int dest_id, int sqn, Seq *seq)
{
	int i;
	int datanum = sqn / 2;
	int datasize = 0;
	char *strp;

	Status = 3;

	for (i = 0; i < sqn; i++) {
		datasize += strlen(seq[i].tit)+1;
		datasize += strlen(seq[i].seq)+1;
	}
	datasize++;
	seq_buf[0] = '\0';
	strp = seq_buf;
	for (i = 0; i < sqn; i++) {
		sprintf(strp, "%s%c%s%c", seq[i].tit, DATA_SEP_CHAR,
				seq[i].seq, DATA_SEP_CHAR);
		strp += strlen(strp);
	}
	strp[strlen(strp)] = '\0';
#ifdef DEBUG
	fprintf(stderr, "Try: dest_id=%d, datasize=%d\n", dest_id, datasize);
#endif

	MPI_Send(&datasize, 1, MPI_INT, dest_id, Mdp_TagSeqSize, MPI_COMM_WORLD);
	if (datasize > 0) {
		MPI_Send(seq_buf, datasize, MPI_CHAR, dest_id, Mdp_TagSeqData, MPI_COMM_WORLD);
	}

#ifdef DEBUG
	printf("Sent: dest_id=%d,datasize=%d\n",dest_id,datasize);
#endif
}

void sigusr_handler(int sig)
{
	fprintf(stderr, "Status: %s\n", StatusStr[Status]);
	signal(SIGUSR2, sigusr_handler);
}
void sigterm_handler(int sig)
{
	__ExitFlag = 1;
}
void sigkill_handler(int sig)
{
	int tidx;
	MPI_Abort(MPI_COMM_WORLD, 111);
	exit(111);
}
/* processing mdp specific arguments */
mdp_get_args(int argc, char **argv)
{
	int i, j;
	char fileflg = 0;

	j = 0;
	for (i = 1; i < argc; i++) {
		if (*argv[i] == '+') {
			char *p = argv[i];
			switch (*++p) {
			case 'H':
				if (*++p) {
					char *q;
					q = strtok(p, ":");
					hosts[numhosts] = q;
					if (q = strtok(NULL, ":")) {
						procnum[numhosts] = atoi(q);
					} else {
						procnum[numhosts] = default_procnum;
					}
					numhosts++;
					addhost = 1;
				}
				break;
			case 'F':
				hostfile = ++p;
				break;
			case 'P':
				default_procnum = atoi(++p);
				break;
			case 'B':
				blocksize = atoi(++p);
				break;
			case 'S':
				/** skip the first S lines **/
				skip = atoi(++p);
				break;
			case 'V':
				verbose = 1;
				break;
			default:
				args[j] = argv[i];
				j++;
				break;
			}
		} else if (*argv[i] == '-') {
			char *p = argv[i];
			switch (*++p) {
			case 'h':
				fprintf(stderr, "Usage: %s [+Fhostfile] [+H<hostname>:<procnum>] [+H...] [+P<default_procnum>] [<filename>] [<dp options> ...]\n", argv[0]);
				exit(0);
				break;
			case 'N':
				strcpy(listfile, argv[++i]);
				break;
			default:
				args[j] = argv[i];
				j++;
				break;
			}
		} else {
			if (! fileflg) {
				strcpy(seqfile, argv[i]);
				fileflg = 1;
			} else {
				args[j] = argv[i];
				j++;
			}
		}
	}
	if (j>0) {
		args[j] = 0;
	}
	if (hostfile) {
		read_hostfile(hostfile);
	}
/**
	if (numhosts == 0) {
		gethostname(currenthost, MAXNAMELEN);
		hosts[numhosts] = currenthost;
		procnum[numhosts] = default_procnum;
		numhosts = 1;
	}
**/
}
read_hostfile(char *hostfile)
{
	static char buf[BUFSIZ];
	char *p;
	int hn;
	FILE *fp;

	if(! hostfile) return 0;
	if ( (fp = fopen(hostfile, "r")) == NULL ) {
		fprintf(stderr, "Can't read hostfile\n");
		exit(1);
	}
	hn = 0;
	while (fgets(buf, BUFSIZ, fp) != NULL) {
		if (buf[0] == '#') continue;

		p = strtok(buf, ": \t\n");
		if (p == NULL) continue;
		hosts[hn] = strdup(p);
		p = strtok(NULL, ": \t\n");
		if (p == NULL || atoi(p) == 0)  {
			procnum[hn] = default_procnum;
		} else {
			procnum[hn] = atoi(p);
		}
		hn++;
	}
	fclose(fp);
	if (hn > 0) {
		numhosts = hn;
		addhost = 1;
	}
}
