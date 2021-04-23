
/**
 * �����ȥ�:     Test<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
// �ܥ��󤬲�����³�����Ȥ��ν������������
//     �ܥ��󤬲����줿�����ߥ󥰤ǡ�Timer ������+��ư
//     ������ַв��ˡ�Timer �ˤ�ꥤ�٥��ȯ��
//     �ܥ��������줿�����ߥ󥰤ǡ�Timer �����+�˴�
public class ButtonAcceleration implements ActionListener, MouseListener {
    private Timer timer = null;
    private ActionListener action = null;
    private int delay;

    //
    private int iniDelay;
    private int minDelay;
    private int acceleration;
    private int maxAccCount;
    private int accCount;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ButtonAcceleration(int d, ActionListener act) {
        setIniDelay(d);
        setDelay(d);
        setAction(act);

        // ������ư�ֳ֤β�®�ˤĤ���
        setMinDelay(100);           // ��û 0.1 �ôֳ֤Ǽ¹�
        setMaxAccCount(1);      //
        setAcceleration(3);         // �����β�®
        clearAccCount();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setIniDelay(int d) {
        iniDelay = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private int getIniDelay() {
        return(iniDelay);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDelay(int d) {
        delay = d;
        if (timer != null) {
            timer.setDelay(delay);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private int getDelay() {
        return(delay);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAction(ActionListener act) {
        //
        action = act;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private ActionListener getAction() {
        //
        return(action);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMinDelay(int d) {
        //
        minDelay = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private int getMinDelay() {
        //
        return(minDelay);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����¹Ԥ��®����
    //   Ϣ³ maxAccCount ����������¹Ԥ��줿�顢delay �� 1/acceleration �ˤ���
    public void setAcceleration(int acc) {
        acceleration = acc;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private int getAcceleration() {
        return(acceleration);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMaxAccCount(int acc) {
        maxAccCount = acc;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private int getMaxAccCount() {
        return(maxAccCount);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearAccCount() {
        accCount = 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private int upAccCount() {
        accCount++;

        return(accCount);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void startTimer() {
        // �⤷�������ޡ���ư���Ƥ����� ---> �����ޡ���ߤ��
        stopTimer();

        // ���٥�ȼ¹Բ�����ꥢ
        clearAccCount();

        // Timer ������
        timer = new Timer(getDelay(), this);
        timer.setInitialDelay(1);       // �����ˡ��ǽ�Υ��٥�Ȥ�ȯ��������
        timer.start();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void stopTimer() {
        if (timer != null) {
            // �����ޡ���λ
            timer.stop();
            timer = null;
            clearAccCount();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        // �����ޡ����٥�Ȥ�ȯ������
        ActionListener act = getAction();
        act.actionPerformed(e);

        // ���٥�ȼ¹Բ��������ȥ��å�
        int n = upAccCount();
        if (getMaxAccCount() <= n) {
            // �������¹Ԥ��줿�Τǡ����ν����ޤǤλ��֤�û������
            int newDelay = getDelay() / getAcceleration();
            int minDelay = getMinDelay();
            if (newDelay < minDelay) {
                newDelay = minDelay;        // �¹Դֳ֤ϡ�����ʾ�û�����ʤ�
            }
            setDelay(newDelay);

            // ���٥�ȼ¹Բ�����ꥢ
            clearAccCount();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
        // �ܥ��󤬲����줿
        // �����ޡ��򥻥å�
        setDelay(getIniDelay());
        startTimer();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
        // �ܥ��󤬤Ϥʤ��줿
        // �����ޡ���ߤ��
        stopTimer();
    }

}
