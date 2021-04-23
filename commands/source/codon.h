
#define isNUC(x) (NID[x]>=0)
#define isAMINO(x) (AID[x]>=0)

#define COD3(x) (NUC[x&3])
#define COD2(x) (NUC[(x>>2)&3])
#define COD1(x) (NUC[(x>>4)&3])

const char *NUC = "TCAG";

const int NID[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,4,1,4,-1,-1,3,4,-1,-1,4,-1,4,4,-1,-1,-1,4,4,0,-1,4,4,-1,4,-1,-1,-1,-1,-1,-1,-1,2,4,1,4,-1,-1,3,4,-1,-1,4,-1,4,4,-1,-1,-1,4,4,0,-1,4,4,-1,4,-1,-1,-1,-1,-1,-1,};

const char *AMINO = "ARNDCQEGHILKMFPSTWYVBZX*";

const int AID[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,23,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0,20,4,3,6,13,7,8,9,-1,11,10,12,2,-1,14,5,1,15,16,-1,19,17,22,18,21,-1,-1,-1,-1,-1,-1,0,20,4,3,6,13,7,8,9,-1,11,10,12,2,-1,14,5,1,15,16,-1,19,17,22,18,21,-1,-1,-1,-1,-1,};

const int CodonTabID[] = { -1,0,1,2,3,4,5,-1,-1,6,7,8,9,10,11,12, };

const char *CodonName[] = {
	"The Standard Code (transl_table=1)",
	"The Vertebrate Mitochondrial Code (transl_table=2)",
	"The Yeast Mitochondrial Code (transl_table=3)",
	"The Mold, Protozoan, and Coelenterate Mitochondrial Code and the Mycoplasma/Spiroplasma Code (transl_table=4)",
	"The Invertebrate Mitochondrial Code (transl_table=5)",
	"The Ciliate, Dasycladacean and Hexamita Nuclear Code (transl_table=6)",
	"The Echinoderm Mitochondrial Code (transl_table=9)",
	"The Euplotid Nuclear Code (transl_table=10)",
	"The Bacterial \"Code\" (transl_table=11)",
	"The Alternative Yeast Nuclear Code (transl_table=12)",
	"The Ascidian Mitochondrial Code (transl_table=13)",
	"The Flatworm Mitochondrial Code (transl_table=14)",
	"Blepharisma Nuclear Code (transl_table=15)",
};
const char *CodonTrans[] = {
	"FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSS**VVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCWWTTTTPPPPHHQQRRRRIIMMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSSSVVVVAAAADDEEGGGG",
	"FFLLSSSSYYQQCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCCWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CC*WLLLSPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
	"FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSGGVVVVAAAADDEEGGGG",
	"FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG",
	"FFLLSSSSYY*QCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG",
};
const char *CodonStart[] = {
	"---M---------------M---------------M----------------------------",
	"--------------------------------MMMM---------------M------------",
	"-----------------------------------M----------------------------",
	"--MM---------------M------------MMMM---------------M------------",
	"---M----------------------------MMMM---------------M------------",
	"-----------------------------------M----------------------------",
	"-----------------------------------M----------------------------",
	"-----------------------------------M----------------------------",
	"---M---------------M------------MMMM---------------M------------",
	"-------------------M---------------M----------------------------",
	"-----------------------------------M----------------------------",
	"-----------------------------------M----------------------------",
	"-----------------------------------M----------------------------",
};
