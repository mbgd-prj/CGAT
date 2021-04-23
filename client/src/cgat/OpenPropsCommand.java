
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

///////////////////////////////////////////////////////////////////////////////
//
public class OpenPropsCommand extends MouseAdapter {
    PropList propList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public OpenPropsCommand(PropList p) {
        propList = p;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
        TreePath treePath = propList.tree.getPathForLocation(e.getX(), e.getY());
        if (treePath == null) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();

        String propPath = "";
        TreeNode [] treeList = node.getPath();
        for(int i = 0; i < treeList.length; i++) {
            if (! propPath.equals("")) {
                propPath = propPath + ".";
            }
            propPath = propPath + treeList[i].toString();
        }
        propList.seekProp(node, propPath);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
        TreePath treePath = propList.tree.getPathForLocation(e.getX(), e.getY());
        if (treePath == null) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();

        String propPath = "";
        TreeNode [] treeList = node.getPath();
        for(int i = 0; i < treeList.length; i++) {
            if (! propPath.equals("")) {
                propPath = propPath + ".";
            }
            propPath = propPath + treeList[i].toString();
        }
        propList.propsPanel.seekProps(propPath);
    }


}
