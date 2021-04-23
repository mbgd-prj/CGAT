#!/usr/bin/perl -s

###############################################################################
use DirHandle;
use File::Path;
require "CGAT_Conf.pl";

#$mian::DEBUG = 1;

###############################################################################
#
# �����ζ��ڤ�ʥ����å��ݥ���ȡˤ˴ؤ������
#
###############################################################################
package main;

# �����å��ݥ���ȥե�������Ǽ����ǥ��쥯�ȥ�
mkpath("$main::DIR_work", 0, 0750);

###############################################################################
# �����å��ݥ���ȥե�����򥯥ꥢ(���)����
sub clearCheckPoint {
    my($regexp) = @_;
    my($file);
    my($dh);

    $dh = new DirHandle("$main::DIR_work") || die("Can not open $main::DIR_work($!)");
print STDERR "$main::DIR_work\n";
    foreach $file ($dh->read()) {
        next if ($file !~ /^\./);
        next if ($file !~ /.end$/);
	if ($file =~ /$regexp/) {
	        unlink("$main::DIR_work/$file");
		print STDERR "$file\n";
	}
    }
    undef($dh);

    return;
}

###############################################################################
# �����å��ݥ���ȥե���������
#     �ե�����̾ : ".$chkPointName.end"
sub fileCheckPoint {
    my($chkPointName) = @_;

    return "$main::DIR_work/.$chkPointName.end";
}

###############################################################################
# �����å��ݥ���ȥե�������������
sub touchCheckPoint {
    my($chkPointName) = @_;
    my($procFile);
    my($cmd);

    $procFile = &fileCheckPoint($chkPointName);
    $cmd = "$main::CMD_touch $procFile";
    system("$cmd");

    return;
}

###############################################################################
# �����å��ݥ���ȥե������¸�ߤ�����å�����
sub passCheckPoint {
    my($chkPointName) = @_;
    my($procFile);

    $procFile = &fileCheckPoint($chkPointName);
    if (-e "$procFile") {
        # �����å��ݥ���ȥե����뤬¸�ߤ���
        return 1;
    }


    # �����å��ݥ���ȥե����뤬¸�ߤ��ʤ�
    return 0;
}

###############################################################################
# �������ޥ����
# �����å��ݥ���ȥե������¸�ߤ�����å�����������¹Ԥ��뤫���ʤ�����Ƚ��
# ������¹Ԥ�����硢�����å��ݥ���ȥե�������������
sub procCheckPoint {
    my($chkPointName, $cmd) = @_;
    my($sta);

    $sta = &passCheckPoint($chkPointName);
    if ($sta) {
        # �����Ѥ�
        return 0;
    }
    # ������¹�
    print STDERR "CMD :: $cmd\n" if ($main::DEBUG);
    $sta = system("$cmd");
    if ($sta) {
        # ��λ���ơ������� 0 �ʳ��ʲ��餫������ȯ����
        print STDERR "WARNING :: Cmd returns $sta\n" if ($main::DEBUG);
        return $sta;
    }

    # ���������ｪλ
    &touchCheckPoint($chkPointName);

    return 0;
}

###############################################################################
# ���֥롼������
# �����å��ݥ���ȥե������¸�ߤ�����å�����������¹Ԥ��뤫���ʤ�����Ƚ��
# ������¹Ԥ�����硢�����å��ݥ���ȥե�������������
sub funcCheckPoint {
    my($chkPointName, $refFunc, @args) = @_;
    my($sta);

    $sta = &passCheckPoint($chkPointName);
    if ($sta) {
        return 0;
    }

    $sta = &{$refFunc}(@args);
    if ($sta) {
        return $sta;
    }

    &touchCheckPoint($chkPointName);

    return 0;
}

###############################################################################
1;#
###############################################################################
