
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ClearFilterStaCommand implements ActionListener {
    RegionInfoList sp1Seg = null;
    RegionInfoList sp2Seg = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ClearFilterStaCommand(RegionInfoList sp1, RegionInfoList sp2) {
        sp1Seg = sp1;
        sp2Seg = sp2;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        clear(sp1Seg);
        clear(sp2Seg);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �ե��륿��󥰤�Ԥ� ---> ����������ʪ��ɽ�����ʤ�
    //     ʣ���ξ��� AND �Ƿ�礵����
    public void clear(RegionInfoList sp) {
        for(int i = 0; i < sp.size(); i++) {
            RegionInfo r = sp.getRegionInfo(i);
            r.setFilter(false);
        }
    }
}

