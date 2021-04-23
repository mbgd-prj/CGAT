#!/usr/bin/perl -s

###############################################################################
package main;

###############################################################################
%Default_Feature_Opt = (
	RepNumCut => [2, 8, 20, 100],
	HighRepNum => 100,
);
%Default_Homology_Opt = (
	CutoffEvalue => 1e-5,
	LargeGapForSplit => 10,
	MinScoreForSplit => 20,
	maskRegion => "HighRep>=$Default_Feature_Opt{'HighRepNum'}",
	SaveResult => 1,
	MergeMaxGap => 200,
	ChainMaxDist => 20000,
	BestHitCoverage => 0.5,
	BestHitScoreRatio => 0.8,
);
###############################################################################
1;#
###############################################################################
