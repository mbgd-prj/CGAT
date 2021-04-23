
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.util.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionInfoList extends Observable {
    public static final String  ATTR_name    = "name";
    public static final String  ATTR_product = "product";
    public static final boolean TYPE_ATTR_STR = true;
    public static final boolean TYPE_ATTR_VAL = false;

    protected String id = "";
    protected BusyFlag busy;

    protected String dataFilename;
    protected boolean flagDataLoad = false;

    protected String regDataName;
    protected String attrName[];
    protected String setName = null;
    protected int maxLane = 1;
    protected boolean attrDataType[];         // attr �ǡ����μ��� String(true) or not(false)

    protected RegionInfo cRegInfList[];   // From ������ˤʤ�褦�˥ǡ������Ǽ����
    protected String regColorType = null;     // �ɤ߹���� Regs �� colorType ����

    protected HashMap hashRegion;             // ORF ̾�ǡ���®�˸������뤿��

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfoList() {
        busy = new BusyFlag();
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAttrName(int i) {
        if (attrName[i] != null) {
            return attrName[i];
        }
        return(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAttrDataType(int i, boolean sta) {
        attrDataType[i] = sta;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getAttrDataType(int i) {
        return attrDataType[i];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getColorType() {
        return(regColorType);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setId(String i) {
        id = i;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearSetName() {
        setName = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSetName(String sn) {
        setName = sn.toLowerCase();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSetName() {
        return setName;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearMaxLane() {
        maxLane = 1;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMaxLane(int ml) {
        maxLane = ml;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getMaxLane() {
        return maxLane;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        cRegInfList = null;
        hashRegion  = new HashMap();

        setDataFilename("");

        // °��̾�γ�Ǽ�ΰ�
        attrName     = new String[RegionInfo.maxAttrNum];
        attrDataType = new boolean[RegionInfo.maxAttrNum];
        for(int i = 0; i < RegionInfo.maxAttrNum; i++) {
            attrName[i]     = null;
            attrDataType[i] = TYPE_ATTR_VAL;        // �ǥե���ȡ����ͥǡ���
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFlagDataLoad(boolean f) {
        flagDataLoad = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getFlagDataLoad() {
        return(flagDataLoad);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String name) {
        dataFilename = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        if (busy.getBusyFlagOwner() != null) {
            // �ǡ��� load ��
            return(0);
        }

        try {
            return(cRegInfList.length);
        }
        catch (NullPointerException np) {
            return(0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo getRegionInfo(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return(cRegInfList[index]);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo getRegionInfo(String name) {
        try {
            return (RegionInfo)hashRegion.get(name);
        }
        catch (Exception e) {
            return(null);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
        String fname = "";

        if (dataFilename.equals(filename)) {
            // �����ɤ߹�����ǡ�����Ʊ�� ---> �����ɤ߹���ɬ�פ��ʤ�
            setFlagDataLoad(false);
            return(true);
        }

        // �ǡ��� Load ����
        busy.getBusyFlag();
        setFlagDataLoad(true);

        // �ǡ���������
        clear();

        // �ǡ����ե�����̾���ݻ�
        setDataFilename(filename);
        BaseFile regionFile;
        try {
            // �ե�����Υ��������
            fname = filename;
            if (fname.startsWith("http")) {
                regionFile = new UrlFile(fname);
            }
            else {
                regionFile = new DiskFile(fname);
            }
        }
        catch (Exception e2) {
            // ���顼��å�����ɽ��
            String msg;
            msg = "File not found.\n" + "File : " + fname + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // �ǡ��� Load ��λ
            busy.freeBusyFlag();
            return(false);
        }

        boolean sta = parse(fname, regionFile);

        // �ǡ��� Load ��λ
        busy.freeBusyFlag();
        return(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(String fname) {
        try {
            DiskFile df = new DiskFile(fname);
            return(parse(fname, df));
        }
        catch (Exception e) {
            return(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(File f) {
        return(parse(f.getAbsolutePath()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // RegionInfo �ǡ������ñ�̤��ɤ߹��ߡ�����˳�Ǽ
    //   ���ϥ쥳���ɷ���������Ǥ��뤿�ᡢ�ǽ�� ArrayList ���ɤ߹���
    //   ��������ꤷ�����ȡ�����˰ܤ��ؤ���
    public boolean parse(String fname, BaseFile bf) {
        ArrayList wkData = new ArrayList();
        int lenWkData;
        int lineNo = 0;
        boolean readHeader = false;
        regColorType = ColorTab.TYPE_INT;
        ArrayList orderItems = new ArrayList();                  // ���ܤν�

        clearSetName();      // set_name �򥯥ꥢ
        clearMaxLane();      // max_lane �򥯥ꥢ
        try {
            for(;;) {
                String buf = bf.readLine();     // �����ɤ߹���
                lineNo++;                       // �ɤ߹�����Կ��򥫥���ȡʥ��顼��å����������ѡ�

                if (buf == null) {
                    break;
                }

                // '#' �ǳ��Ϥ��Ƥ��� attrName ���ɤ߹���Ǥ��ʤ�
                if (buf.startsWith("#")) {
                    if (! readHeader) {
                        // '#' �ʹߤ�ʸ���� StringTokenizer ��ʬ�򤹤�
                        String bufAttr = null;
                        for(int i = 0; i < buf.length(); i++) {
                            if (buf.charAt(i) != '#') {
                                // ʸ�����������դ��Ƥ������פʶ���ʸ�������
                                bufAttr = buf.substring(i).trim();
                                break;
                            }
                        }
                        if (bufAttr != null) {
                            //
                            StringTokenizer tokenAttrName = new StringTokenizer(bufAttr, "\t");

                            //
                            int idxAttr = 0;
                            while(tokenAttrName.hasMoreTokens()) {
                                String itemName = tokenAttrName.nextToken().toLowerCase();
                                orderItems.add(itemName);
                                if (itemName.equals("sp") ||
                                    itemName.equals("from") ||
                                    itemName.equals("to") ||
                                    itemName.equals("dir") ||
                                    itemName.equals("color") ||
                                    itemName.equals("lane") ||
                                    itemName.equals("weight")) {
                                    // �������
                                }
                                else if (idxAttr < RegionInfo.maxAttrNum) {
                                    // attr ��̾�Τ������
                                    attrName[idxAttr] = itemName;
                                    idxAttr++;
                                }
                            }

                            readHeader = true;        // head ���ɤ߹����
                        }
                    }
                    else if (buf.startsWith("#set_name")) {
                        String bufSetName = null;
                        for(int i = 0; i < buf.length(); i++) {
                            if (buf.charAt(i) != '#') {
                                // ʸ�����������դ��Ƥ������פʶ���ʸ�������
                                bufSetName = buf.substring(i).trim();
                                break;
                            }
                        }
                        StringTokenizer tokenSetName = new StringTokenizer(bufSetName, "\t");
                        tokenSetName.nextToken(); // #set_name �ϡ��ɤ߼Τ�
                        setSetName(tokenSetName.nextToken().toLowerCase());
                    }
                    else if (buf.startsWith("#max_lane")) {
                        String bufMaxLane = null;
                        for(int i = 0; i < buf.length(); i++) {
                            if (buf.charAt(i) != '#') {
                                // ʸ�����������դ��Ƥ������פʶ���ʸ�������
                                bufMaxLane = buf.substring(i).trim();
                                break;
                            }
                        }
                        StringTokenizer tokenMaxLane = new StringTokenizer(bufMaxLane, "\t");
                        tokenMaxLane.nextToken(); // #max_lane �ϡ��ɤ߼Τ�

int maxLane = Integer.parseInt(tokenMaxLane.nextToken());
                        setMaxLane(maxLane);
                    }
                    else if (buf.startsWith("#set_name")) {
                        String bufSetName = null;
                        for(int i = 0; i < buf.length(); i++) {
                            if (buf.charAt(i) != '#') {
                                // ʸ�����������դ��Ƥ������פʶ���ʸ�������
                                bufSetName = buf.substring(i).trim();
                                break;
                            }
                        }
                        StringTokenizer tokenSetName = new StringTokenizer(bufSetName, "\t");
                        tokenSetName.nextToken(); // #set_name �ϡ��ɤ߼Τ�

String setName = tokenSetName.nextToken().trim();
                        setSetName(setName);
                    }
                    continue;
                }

                //
                RegionInfo inf = new RegionInfo();
                StringTokenizer token = new StringTokenizer(buf, "\t");

                // header �˵��Ҥ��줿���ܽ�˥ǡ������ɤ߹���
                int    from;
                int    to;
                byte   dir;
                String col;
                int    lane = 1;
                float  weight = 1.0f;
                int idxAttr = 0;

                //
                Iterator it = orderItems.iterator();
                while(it.hasNext()) {
                    String key = (String)it.next();
                    if (key.equals("sp")) {
						// ���ι��ܤϻȤ�ʤ��ʤä����ɤ߼ΤƤ�
						// �Ť��ǡ����Ȥθߴ����Τ���
						token.nextToken();
                    }
                    else if (key.equals("from")) {
                        from  = Integer.parseInt(token.nextToken());
                        inf.setFrom(from);
                    }
                    else if (key.equals("to")) {
                        to    = Integer.parseInt(token.nextToken());
                        inf.setTo(to);
                    }
                    else if (key.equals("dir")) {
                        String strDir = token.nextToken();
                        if (strDir.equalsIgnoreCase("+1") ||
                            strDir.equalsIgnoreCase("1") ||
                            strDir.equalsIgnoreCase("DIR") ||
                            strDir.equalsIgnoreCase("+") ||
                            strDir.equalsIgnoreCase("f")) {
                            dir = 1;
                        }
                        else if (strDir.equalsIgnoreCase("-1") ||
                                 strDir.equalsIgnoreCase("INV") ||
                                 strDir.equalsIgnoreCase("-") ||
                                 strDir.equalsIgnoreCase("r")) {
                            dir = -1;
                        }
                        else {
                            dir = Byte.parseByte(strDir);
                        }
                        inf.setDir(dir);
                    }
                    else if (key.equals("color")) {
                        col  = token.nextToken();
                        // color �Υǡ������̤�Ƚ��
                        if (regColorType.equals(ColorTab.TYPE_INT)) {
                            try {
                                Integer.valueOf(col);
                            } catch (NumberFormatException e) {
                                regColorType = ColorTab.TYPE_FLOAT;
                            }
                        }
                        if (regColorType.equals(ColorTab.TYPE_FLOAT)) {
                            try {
                                Float.valueOf(col);
                            } catch (NumberFormatException e) {
                                regColorType = ColorTab.TYPE_STR;
                            }
                        }
                        inf.setColor(col);
                    }
                    else if (key.equals("lane")) {
                        lane = Integer.parseInt(token.nextToken());
                        inf.setLane(lane);
                    }
                    else if (key.equals("weight")) {
                        weight = Float.parseFloat(token.nextToken());
                        inf.setWeight(weight);
                    }
                    else {
                        try {
                            String attr = token.nextToken();
                            inf.setAttr(idxAttr, attr);

                            // attr �ǡ�������Ƚ��
                            try {
                                double d = Double.valueOf(attr).doubleValue();
                            }
                            catch (Exception e0) {
                                // ���ͤ��Ѵ��Ǥ��ʤ� ---> ʸ����ǡ���
                                setAttrDataType(idxAttr, TYPE_ATTR_STR);
                            }
                            idxAttr++;
                        }
                        catch (Exception e) {
                        }
                    }
                }

                wkData.add(inf);
                int idxName = getAttrIndex(RegionInfoList.ATTR_name);
                String name = inf.getAttr(idxName);
                if (name != null) {
                    // name ����ʸ��������
                    name = name.toUpperCase();
                    hashRegion.put(name, inf);
                }
            }
	} catch (Exception e) {
            // ���顼��å�����ɽ��
            String msg;
            msg = "Data Format Error.\n" + "File : " + fname + "\n" + "Line : " + lineNo + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // File IO Error
            Dbg.println(0, "File Read Error : Region Info");
            return(false);
        }

        // �ɤ߹�����ǡ���������˥��ԡ�����from1 ���¤��ؤ���
        lenWkData = wkData.size();
        Dbg.println(1, "read genes :: " + lenWkData);
        if (lenWkData == 0) {
          return true;
        }
        cRegInfList = new RegionInfo[lenWkData];

        // ���ԡ�
        for(int i = 0; i < lenWkData; i++) {
            cRegInfList[i] = (RegionInfo)wkData.get(i);
        }

        // �ɤ߹�����ǡ������¤��ؤ���
        Comparator c;

        // from1 ���¤��ؤ�
        c = new CompRegionByFrom();
        Arrays.sort(cRegInfList, c);

        return(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // NAME �򸡺�����
    public ArrayList searchName(ArrayList nameList) {
        ArrayList matchInfo = new ArrayList();     // ���� Hit �ǡ���
        HashMap nameHash = new HashMap();
        String name;

        // hash �� name �򥻥å�
        for(int i = 0; i < nameList.size(); i++) {
            nameHash.put((String)nameList.get(i), null);
        }

        // ��������
        int idxName = getAttrIndex(RegionInfoList.ATTR_name);
        for(int i = 0; i < size(); i++) {
            RegionInfo r = cRegInfList[i];
            name = r.getAttr(idxName);
            if (nameHash.containsKey(name)) {
                // hash ����Ͽ ---> �����о�
                matchInfo.add(r);
            }
        }

        return(matchInfo);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataName(String name) {
        regDataName = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getDataName() {
        return regDataName;
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����ǤΥե��륿��󥰾��֤򥯥ꥢ ----> ���ǡ�����ɽ���оݤȤ���
    public void clearFilter() {
        for(int i = 0; i < size(); i++) {
            setFilter(i, true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����ǤΥե��륿��󥰾��֤�����
    public void setFilter(int i, boolean f) {
        RegionInfo r = cRegInfList[i];
        r.setFilter(f);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getAttrIndex(String key) {
        int idx ;

        if (key == null) {
            return -1;
        }
        if (attrName == null) {
            return -1;
        }

        for(idx = 0; idx < attrName.length; idx++) {
            if (key.equalsIgnoreCase(attrName[idx])) {
                return idx;
            }
        }

        return -1;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ArrayList getSegmentSet(String name) {
        ArrayList segmentSet = new ArrayList();
        String key = getSetName();
        if ((key == null) || (name == null)) {
            return segmentSet;
        }
        int idxSegSet = getAttrIndex(key);

        for(int i = 0; i < cRegInfList.length; i++) {
            String segName = cRegInfList[i].getAttr(idxSegSet);
            if (segName.equals(name)) {
                segmentSet.add(cRegInfList[i]);
            }
        }

        return segmentSet;
    }



    ///////////////////////////////////////////////////////////////////////////
    // from ����˥����Ȥ��뤿���ɾ���ؿ�
    class CompRegionByFrom implements Comparator {
        public int compare(Object objA, Object objB) {
            RegionInfo a = (RegionInfo)objA;
            RegionInfo b = (RegionInfo)objB;
            return(a.getFrom() - b.getFrom());
        }
    }

}
