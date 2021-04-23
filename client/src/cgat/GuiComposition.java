package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class GuiComposition {
    protected String cgi = "";
    protected ArrayList elm;

    ///////////////////////////////////////////////////////////////////////////
    //���󥹥ȥ饯��������/���С��ѿ�����������᥽�åɤ�ƤӽФ�
    public GuiComposition () {
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //���С��ѿ��ν����
    private void clear() {
        elm  = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getCgi(){
        return cgi;
    }

    ///////////////////////////////////////////////////////////////////////////
    //����줿ʸ���󤬲�ʸ���ʤΤ���������Ȥ��롣
    public int countData(){
        return elm.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //���С��ѿ����������᥽�å�
    public GuiElement get (int idx) {
        return (GuiElement)elm.get(idx);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void readComposition(String filename) {
try {
        BaseFile bf = new DiskFile(filename);
        readComposition(bf);
}
catch (Exception e) {
}
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void readComposition(BaseFile br) {
        StringTokenizer st;
        String txt = null;
        while ((txt=br.readLine()) != null) {
            txt = txt.trim();   // ����ζ���ʸ�������
            if(txt.length() == 0) {
                // ����
                continue;
            }
            if(txt.startsWith("#")) {         // �����ȹ�
                if(txt.startsWith("#CGI\t")) {
                    st = new StringTokenizer(txt, "\t");
                    try {
                        st.nextToken();  // �ɤ߼Τ�
                        cgi = st.nextToken();
                    }
                    catch (NoSuchElementException nsee) {
                        continue;
                    }
                }

                continue;
            }

            //
            String strList[] = txt.split("\t");
            int strIdx = 0;

            //
            String type, text, varname, val;
            int nr, nc;
            try {
                type = strList[strIdx++];
            }
            catch (NoSuchElementException nsee) {
                continue;
            }
            catch (ArrayIndexOutOfBoundsException aioobe) {
                continue;
            }

            if (type.equalsIgnoreCase("label")) {
                try {
                      text = strList[strIdx++].trim();
                    if (text.equals("")) {
                        continue;
                    }
                }
                catch (NoSuchElementException nsee) {
                    text = "";
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    text = "";
                }

                //
                elm.add(new GuiElement(type, text));
            }
            else if (type.equalsIgnoreCase("text")) {
                try {
                    text = strList[strIdx++].trim();
                    varname = strList[strIdx++].trim();
                    if (varname.equals("")) {
                        continue;
                    }
                }
                catch (NoSuchElementException nsee) {
                    continue;     // ������­
                }
                try {
                    val = strList[strIdx++].trim();
                }
                catch (NoSuchElementException nsee) {
                    val = "";        // �����̤����
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    val = "";
                }

                elm.add(new GuiElement(type, text, varname, val));
            }
            else if (type.equalsIgnoreCase("radio")) {
                try {
                    text = strList[strIdx++].trim();
                    varname = strList[strIdx++].trim();
                    if (varname.equals("")) {
                        continue;
                    }
                    val = strList[strIdx++].trim();
                }
                catch (NoSuchElementException nsee) {
                    continue;     // ������­
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    continue;
                }

                elm.add(new GuiElement(type, text, varname, val));
            }
            else if (type.equalsIgnoreCase("textarea")) {
                try {
                    text = strList[strIdx++].trim();
                    varname = strList[strIdx++].trim();
                    if (varname.equals("")) {
                        continue;
                    }
                }
                catch (NoSuchElementException nsee) {
                    continue;     // ������­
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    continue;
                }

                try {
                    val = strList[strIdx++].trim();
                    nr = Integer.parseInt(strList[strIdx++]);
                    nc = Integer.parseInt(strList[strIdx++]);
                }
                catch (Exception nsee) {
                    val = "";
                    nr = 5;
                    nc = 15;
                }
                if (nr < 1) {
                    nr = 1;
                }
                else if (20 < nr) {
                    nr = 20;
                }
                if (100 < nc) {
                    nc = 100;
                }

                elm.add(new GuiElement(type, text, varname, val, nr, nc));
            }
        }
    }
}
