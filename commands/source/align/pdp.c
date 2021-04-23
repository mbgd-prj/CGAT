#include <stdio.h>
#include <pvm3.h>
#include <signal.h>
#include <strings.h>
#include <string.h>
#include <unistd.h>
#include "db.h"
#include "pdp.h"

#define MAXSEQNUM 200
#define MAXSEQLEN 50000
#define MAXNAMELEN 100
#define MAXLINELEN 500
#define BLOCKSIZE 500000
#define PROGNAME "pdpc"
#define MAXHOSTS 140

char *hostfile;
char *hosts[MAXHOSTS];
int procnum[MAXHOSTS];
int numhosts = 0;
int numarch = 0;
int addhost = 0;
int default_procnum = 1;
char verbose = 0;

char currenthost[MAXNAMELEN];
/*
char *args[100] = {"-V", "-l", "-P0", "-G", "-s ", 0};
*/
char *args[100] = {"-l", "-P0", "-G", "-s ", 0};

int blocksize = BLOCKSIZE;

/*
char seq[MAXSEQNUM][MAXSEQLEN];
char entname[MAXSEQNUM][MAXNAMELEN];
*/
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

struct pvmhostinfo *hostp;

main(int argc, char **argv)
{
	int mytid = pvm_mytid();
	int numt;
	int i;
	int ti;
	void sigterm_handler();
	void sigkill_handler();
	void sigusr1_handler();
/*
	pvm_catchout(stdout);
*/

	get_args(argc, argv);

	if (addhost) {
		pvm_addhosts(hosts, numhosts, infos);
	} else {
		pvm_config(&numhosts, &numarch, &hostp);
		for (i = 0; i < numhosts; i++) {
			hosts[i] = hostp[i].hi_name;
			procnum[i] = default_procnum;
			infos[i] = 0;
		}
	}

	ti = 0;
	for (i = 0; i < numhosts; i++) {
		if (infos[i] < 0 && infos[i] != PvmDupHost) {
			fprintf(stderr, "addhost failed: %s (skipped)\n", hosts[i]);
			continue;
		}
		numt = pvm_spawn(PROGNAME, args, PvmTaskHost,
				hosts[i], procnum[i], &tids[ti]);
		if (numt <= 0) {
			printf("%s cannot be added: error=%d\n",
				hosts[i],tids[ti]);
		}
		if (numt < 0) {
			fprintf(stderr, "failed\n");
			exit(1);
		}
		ntask += numt;
		ti += procnum[i];
	}

	if (! ntask) {
		exit(1);
	}

	if ( ! (db = dbopen(seqfile)) ) {
		fprintf(stderr, "Can't open file\n");
		exit(1);
	}

	signal(SIGINT, sigterm_handler);
	signal(SIGTERM, sigterm_handler);
	signal(SIGUSR1, sigusr1_handler);
	signal(SIGKILL, sigkill_handler);

	throw_dpcmd();
	if (addhost) {
		pvm_delhosts(hosts, numhosts, infos);
	}
}

throw_dpcmd()
{
	int size = 0;
	int sqn = 0, datanum;
	int tidx = 0;
	int ret;
	int ln = 0;
	FILE *lfp = NULL;

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
			probe_child(&tidx);
			get_result(tidx);

			send_data(tidx, sqn+2, seq);

			tidx =  (tidx+1) % ntask;
			size = sqn = 0;
			if (__ExitFlag == 1) {
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
		probe_child(&tidx);
		get_result(tidx);

		send_data(tidx, (sqn/2)*2, seq);
	}

	if (verbose) {
		printf("Last Loop\n");
	}

	datanum = -1;	/** end of data **/
	for (tidx = 0; tidx < ntask; tidx++) {
		get_result(tidx);
		pvm_initsend(PvmDataDefault);

		if (verbose) {
			printf("End-> tid=%d\n",tids[tidx]);
		}

		pvm_pkint(&datanum, 1, 1);
		pvm_send(tids[tidx], PdpMsg_DataSize);
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
		if (getseq_byname(db, entname1, seq1) != GETSEQ_OK
			|| getseq_byname(db, entname2, seq2) != GETSEQ_OK) {
			/** read error **/
			return 0;
		}
	} else {
		if (getseq_next(db, seq1) == GETSEQ_END ||
			getseq_next(db, seq2) == GETSEQ_END) {
			/** EOF **/
			return -1;
		}
	}
	return 1;
}
#endif

probe_child(int *tidx)
{
	int info;

	Status = 1;
	while ((info=pvm_probe(tids[*tidx], PdpMsg_ResultSize)) <= 0) {
		*tidx =  (*tidx+1) % ntask;
	}
}
get_result(int tidx)
{
	int i;
	int outnum;
	int info;
#ifdef DEBUG
printf("recv: tid=%d\n", tids[tidx]);
#endif
	Status = 2;
	info=pvm_recv(tids[tidx], PdpMsg_ResultSize);
	pvm_upkint(&outnum, 1, 1);
#ifdef DEBUG
printf("outnum: tid=%d, outnum=%d\n", tids[tidx], outnum);
#endif
	if (outnum) {
		info=pvm_recv(tids[tidx], PdpMsg_Result);
		for (i = 0; i < outnum; i++) {
			pvm_upkstr(outbuf);
			printf("%s\n", outbuf);
		}
	}
}
send_data(int tidx, int sqn, Seq *seq)
{
	int i;
	int datanum = sqn / 2;

	Status = 3;
	pvm_initsend(PvmDataDefault);
	pvm_pkint(&datanum, 1, 1);
	pvm_send(tids[tidx], PdpMsg_DataSize);

	pvm_initsend(PvmDataDefault);
	for (i = 0; i < sqn; i++) {
		pvm_pkstr(seq[i].tit);
		pvm_pkstr(seq[i].seq);
	}
	pvm_send(tids[tidx], PdpMsg_Data);

#ifdef DEBUG
	printf("Sent: tid=%d,dnum=%d\n",tids[tidx],datanum);
#endif
}

void sigusr1_handler(int sig)
{
	fprintf(stderr, "Status: %s\n", StatusStr[Status]);
	signal(SIGUSR1, sigusr1_handler);
}
void sigterm_handler(int sig)
{
	__ExitFlag = 1;
}
void sigkill_handler(int sig)
{
	int tidx;
	for (tidx = 0; tidx < ntask; tidx++) {
		pvm_kill(tids[tidx]);
	}
	exit(0);
}
get_args(int argc, char **argv)
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
