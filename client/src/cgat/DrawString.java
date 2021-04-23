
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;

///////////////////////////////////////////////////////////////////////////////
// ʸ��������
// �����Υ᥽�åɤǤϡ�Font �θ����ϡ��ѹ����ޤ���
//     ���Ȥ��� drawStringUp2Down() �Ǥϡ��夫�鲼�˸�����ʸ���������ޤ���
//     �̾�β����� Font ���Ϥ��Ƥ⡢Font �� 90 �ٱ���ž�������褷�ޤ���
//     90 �ٱ���ž���� Font ��������Ƥ��� drawStringUp2Down() ��ƤӽФ��Ƥ���������
//     �ڤ����͡�MyFont.java
// ��ա�����ե��٥åȰʳ���ʸ���ϡ��б����Ƥ��ޤ���
public class DrawString {

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawString() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����鱦���������̾��
    public static void drawStringLeft2Right(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // ������Υե���Ȥ�����
        bakFont = g.getFont();

        // ���ꤵ�줿�ե���Ȥ�ʸ��������
        g.setFont(f);
        g.drawString(s, x, y);

        // ���Υե���Ȥ��᤹
        g.setFont(bakFont);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����麸������
    //     ���Τˤϡ������鱦��ʸ����ս�����褹��
    public static void drawStringRight2Left(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // ������Υե���Ȥ�����
        bakFont = g.getFont();

        // ���ꤵ�줿�ե���Ȥ�ʸ��������
        g.setFont(f);

        // ʸ��������
        StringBuffer newStr = new StringBuffer("");
        for(int i = s.length() - 1; i >= 0; i--) {
            newStr.append(s.charAt(i));
        }
        g.drawString(newStr.toString(), x, y);

        // ���Υե���Ȥ��᤹
        g.setFont(bakFont);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �夫�鲼������
    public static void drawStringUp2Down(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // ������Υե���Ȥ�����
        bakFont = g.getFont();

        // ���ꤵ�줿�ե���Ȥ�ʸ��������
        g.setFont(f);

        // ʸ��������
        int fontHeight = g.getFontMetrics().getHeight();
        for(int i = 0; i < s.length(); i--) {
            String c = s.substring(i, i + 1);
            g.drawString(c, x, y + fontHeight * i);
        }

        // ���Υե���Ȥ��᤹
        g.setFont(bakFont);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ������������
    //     ���Τˤϡ��夫�鲼��ʸ����ս�����褹��
    public static void drawStringDown2Up(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // ������Υե���Ȥ�����
        bakFont = g.getFont();

        // ���ꤵ�줿�ե���Ȥ�ʸ��������
        g.setFont(f);

        // ʸ��������
        int fontHeight = g.getFontMetrics().getHeight();
        for(int i = s.length() - 1; i >= 0; i--) {
            String c = s.substring(i, i + 1);
            g.drawString(c, x, y + fontHeight * (s.length() - i - 1));
        }

        // ���Υե���Ȥ��᤹
        g.setFont(bakFont);
    }



}
