
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class PropsPanel extends JSplitPane {
    BaseProps baseProps;
    PropList propList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public PropsPanel(BaseProps b) {
        super();
        baseProps = b;
        setOrientation(HORIZONTAL_SPLIT);       // 縦に分割
        setDividerSize(2);                      // ２分割
        setPropListPanel();
        setPropsPanel(null);
        setDividerLocation(200);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void showPropList(String propName) {
        propList.show(propName);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setPropListPanel() {
        JPanel propListPanel = new JPanel(new BorderLayout());
        propList = new PropList(this);
        propListPanel.add(propList, BorderLayout.CENTER);
        setLeftComponent(propListPanel);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setPropsPanel(String propName) {
        JPanel propsPanel = new JPanel(new BorderLayout());
        Props props = new Props(propName, baseProps);
        JScrollPane scroll = new JScrollPane(props);
        propsPanel.add(scroll, BorderLayout.CENTER);
        setRightComponent(propsPanel);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void seekProps(String propName) {
        setPropsPanel(propName);
        revalidate();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public HashMap getPropKeys(String prefix) {
        return(baseProps.getPropKeys(prefix));
    }


}
