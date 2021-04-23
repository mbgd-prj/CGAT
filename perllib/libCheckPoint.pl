#!/usr/bin/perl -s

###############################################################################
use DirHandle;
use File::Path;
require "CGAT_Conf.pl";

#$mian::DEBUG = 1;

###############################################################################
#
# 処理の区切り（チェックポイント）に関する処理
#
###############################################################################
package main;

# チェックポイントファイルを格納するディレクトリ
mkpath("$main::DIR_work", 0, 0750);

###############################################################################
# チェックポイントファイルをクリア(削除)する
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
# チェックポイントファイルを作成
#     ファイル名 : ".$chkPointName.end"
sub fileCheckPoint {
    my($chkPointName) = @_;

    return "$main::DIR_work/.$chkPointName.end";
}

###############################################################################
# チェックポイントファイルを作成する
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
# チェックポイントファイルの存在をチェックする
sub passCheckPoint {
    my($chkPointName) = @_;
    my($procFile);

    $procFile = &fileCheckPoint($chkPointName);
    if (-e "$procFile") {
        # チェックポイントファイルが存在する
        return 1;
    }


    # チェックポイントファイルが存在しない
    return 0;
}

###############################################################################
# 外部コマンド版
# チェックポイントファイルの存在をチェックし、処理を実行するかしないかを判断
# 処理を実行した場合、チェックポイントファイルを作成する
sub procCheckPoint {
    my($chkPointName, $cmd) = @_;
    my($sta);

    $sta = &passCheckPoint($chkPointName);
    if ($sta) {
        # 処理済み
        return 0;
    }
    # 処理を実行
    print STDERR "CMD :: $cmd\n" if ($main::DEBUG);
    $sta = system("$cmd");
    if ($sta) {
        # 終了ステータスが 0 以外（何らかの問題発生）
        print STDERR "WARNING :: Cmd returns $sta\n" if ($main::DEBUG);
        return $sta;
    }

    # 処理が正常終了
    &touchCheckPoint($chkPointName);

    return 0;
}

###############################################################################
# サブルーチン版
# チェックポイントファイルの存在をチェックし、処理を実行するかしないかを判断
# 処理を実行した場合、チェックポイントファイルを作成する
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
