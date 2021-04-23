
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

//////////////////////////////////////////////////////////////////////////////
//
public class PropList extends JPanel {
    JTree tree = new JTree();
    PropsPanel propsPanel;

    ///////////////////////////////////////////////////////////////////////////
    //
    public PropList(PropsPanel p) {
//        super();
        JScrollPane scroll = new JScrollPane(tree);
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        propsPanel = p;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void show(String propName) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(propName);
        seekProp(root, propName);
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
        tree.addMouseListener(new OpenPropsCommand(this));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void seekProp(DefaultMutableTreeNode parentProp, String propName) {
        HashMap propKeyHash = propsPanel.getPropKeys(propName);
        Object[] propKeyList = propKeyHash.keySet().toArray();
        if (propKeyList.length == 0) {
            return;
        }

        for(int i = 0; i < propKeyList.length; i++) {
            String propKey = (String)propKeyList[i];
            DefaultMutableTreeNode props = new DefaultMutableTreeNode(propKey);
            parentProp.add(props);
            if (1 < ((Integer)propKeyHash.get(propKey)).intValue()) {
                Dbg.println(1, "Count : "+((Integer)propKeyHash.get(propKey)).intValue());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //

    ///////////////////////////////////////////////////////////////////////////
    //


}
