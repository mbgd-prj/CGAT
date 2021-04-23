
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionListSelection implements MouseListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionListSelection(ViewWindow vWin) {
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // ����å����ν���
    //
    // ��ջ���
    //   ���֥륯��å�������硢��ף����ƤӽФ���롣
    //     ���֥륯��å��Σ����ܤΥ���å��Ǥ⤳�Υ᥽�åɤ��ƤӽФ���뤿�ᡣ
    //   �ȥ�ץ륯��å�������硢��ף����ƤӽФ���롣
    //
    //   mouseClicked() �ʤΤ��������������Τ��Ȥ��⤷��ʤ�����
    //   ���֥륯��å����줿���Τ߽����������Ȥ��ϡ��ɤΤ褦�ˤ�����ɤ��Τ��ʡ�
    //   (���󥰥륯��å���ȥ�ץ륯��å��Ͻ������ʤ�)
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable)e.getSource();
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        String spec = "";
        int from = 1;
        int to   = 1;
        for(int i = 0; i < table.getColumnCount(); i++) {

            // SPEC name �����
            if (i == 0) {       // �ǽ�Υե�����ɤ���ʪ��̾����Ǽ����Ƥ���
                spec = (String)table.getValueAt(row, i);
            }
            // from �����
            if (table.getColumnName(i).equalsIgnoreCase(SearchResults.TAB_ITEM_FROM)) {
                from = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // to �����
            if (table.getColumnName(i).equalsIgnoreCase(SearchResults.TAB_ITEM_TO)) {
                to = ((Integer)table.getValueAt(row, i)).intValue();
            }
        }

        // ����å����줿 ORF ���濴��ɽ������
        viewWin.viewPos(spec, (from + to) / 2);
    }


}
