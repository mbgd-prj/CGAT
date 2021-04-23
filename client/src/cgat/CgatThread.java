package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CgatThread implements Runnable, ActionListener {
	protected String title = "Now executing";
	protected Thread myThread = null;
	protected JDialog myDialog = null;
	protected JLabel myLabel = new JLabel(title);
	public CgatThread() {
	}

	public void setText(String t) {
		title = t;
		myLabel.setText(t);
	}

	public void startThread() {
		// JDialog で「実行中」と表示（キャンセルボタンもあるょ）
		if (myDialog == null) {
			myDialog = new JDialog();
            myDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
			        myThread.interrupt();
                    return;
                }
            });
            myDialog.setSize(300, 150);
			Container c = myDialog.getContentPane();
			c.setLayout(new BorderLayout());
			
			myLabel.setHorizontalAlignment(SwingConstants.CENTER);
			myLabel.setVerticalAlignment(SwingConstants.CENTER);
			c.add(myLabel, BorderLayout.CENTER);

			JButton b = new JButton("Cancel");
			b.addActionListener(this);
			c.add(b, BorderLayout.SOUTH);
		}
		myDialog.setVisible(true);
		
		// Thread を実行する
		if (myThread == null) {
			myThread = new Thread(this);
			myThread.start();
		}

        // Thread が終了するのを末
	    try {
            myThread.join();
	    }
	    catch (InterruptedException ie) {
	    }
	}
	
	public void actionPerformed(ActionEvent e) {
		// キャンセルボタンがクリックされた
		if (myDialog != null) {
			// JDialog を非表示
			myDialog.setVisible(false);
		}

		if (myThread == null) {
			// Thread は実行していないのでは？
			return;
		}
		
		// myThread を停止する
		myThread.interrupt();
		myThread = null;
	}
	
	public void closeDialog() {
		myDialog.setVisible(false);
		myDialog = null;
	}

	public void run() {
		// Thread で実行する「重い」処理
		for(int i = 0; i < 1000; i++) {
			System.out.println("Please override this method!!");
			try {
				Thread.sleep(1000);		// 1000ms sleep する
			}
			catch (InterruptedException e) {
				System.out.println("Catch InterruptedException");
				break;
			}
		}
	}
		
	public boolean isAlive() {
		if (myThread == null) {
			return false;
		}
		return myThread.isAlive();
	}
	
/*
	public static void main(String[] args) {
		System.err.println("START!!");
		CgatThread t = new CgatThread();
		t.startThread();
		while(t.isAlive()) {
			try {
				Thread.sleep(1000);		// 1000ms sleep する
			}
			catch (InterruptedException e) {
				System.out.println("Catch InterruptedException");
				break;
			}
		}
		System.exit(0);
	}
*/
}
