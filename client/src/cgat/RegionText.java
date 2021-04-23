
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionText extends JTextField {
    RegionInfoList regionList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionText(RegionInfoList rList, int len) {
        super(len);
        setHorizontalAlignment(JTextField.RIGHT);
        regionList = rList;
    }

}
