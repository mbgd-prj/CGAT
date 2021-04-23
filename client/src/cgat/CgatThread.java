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
		// JDialog �ǡּ¹���פ�ɽ���ʥ���󥻥�ܥ���⤢����
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
		
		// Thread ��¹Ԥ���
		if (myThread == null) {
			myThread = new Thread(this);
			myThread.start();
		}

        // Thread ����λ����Τ���
	    try {
            myThread.join();
	    }
	    catch (InterruptedException ie) {
	    }
	}
	
	public void actionPerformed(ActionEvent e) {
		// ����󥻥�ܥ��󤬥���å����줿
		if (myDialog != null) {
			// JDialog ����ɽ��
			myDialog.setVisible(false);
		}

		if (myThread == null) {
			// Thread �ϼ¹Ԥ��Ƥ��ʤ��ΤǤϡ�
			return;
		}
		
		// myThread ����ߤ���
		myThread.interrupt();
		myThread = null;
	}
	
	public void closeDialog() {
		myDialog.setVisible(false);
		myDialog = null;
	}

	public void run() {
		// Thread �Ǽ¹Ԥ���ֽŤ��׽���
		for(int i = 0; i < 1000; i++) {
			System.out.println("Please override this method!!");
			try {
				Thread.sleep(1000);		// 1000ms sleep ����
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
				Thread.sleep(1000);		// 1000ms sleep ����
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
