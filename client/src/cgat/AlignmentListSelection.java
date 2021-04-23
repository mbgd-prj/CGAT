
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentListSelection implements MouseListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentListSelection(ViewWindow vWin) {
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

        int from1 = 1;
        int to1   = 1;
        int from2 = 1;
        int to2   = 1;
        for(int i = 0; i < table.getColumnCount(); i++) {
            // from1 �����
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_FROM1)) {
                from1 = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // to1 �����
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_TO1)) {
                to1 = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // from2 �����
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_FROM2)) {
                from2 = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // to2 �����
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_TO2)) {
                to2 = ((Integer)table.getValueAt(row, i)).intValue();
            }
        }


        // ����å��������֤��濴�� region ��ɽ������
        viewWin.viewPos((from1 + to1) / 2, (from2 + to2) / 2);
    }

}
