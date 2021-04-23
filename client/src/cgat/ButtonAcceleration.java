
/**
 * タイトル:     Test<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
// ボタンが押され続けたときの処理を実装する
//     ボタンが押されたタイミングで、Timer を生成+起動
//     一定時間経過後に、Timer によりイベント発生
//     ボタンが放されたタイミングで、Timer を停止+破棄
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

        // 処理起動間隔の加速について
        setMinDelay(100);           // 最短 0.1 秒間隔で実行
        setMaxAccCount(1);      //
        setAcceleration(3);         // 処理の加速
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
    // 処理実行を加速する
    //   連続 maxAccCount 回数処理が実行されたら、delay を 1/acceleration にする
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
        // もし、タイマーが動いていたら ---> タイマーを止める
        stopTimer();

        // イベント実行回数クリア
        clearAccCount();

        // Timer を生成
        timer = new Timer(getDelay(), this);
        timer.setInitialDelay(1);       // すぐに、最初のイベントを発生させる
        timer.start();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void stopTimer() {
        if (timer != null) {
            // タイマー終了
            timer.stop();
            timer = null;
            clearAccCount();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        // タイマーイベントが発生した
        ActionListener act = getAction();
        act.actionPerformed(e);

        // イベント実行回数カウントアップ
        int n = upAccCount();
        if (getMaxAccCount() <= n) {
            // 一定回数実行されたので、次の処理までの時間を短くする
            int newDelay = getDelay() / getAcceleration();
            int minDelay = getMinDelay();
            if (newDelay < minDelay) {
                newDelay = minDelay;        // 実行間隔は、一定以上短くしない
            }
            setDelay(newDelay);

            // イベント実行回数クリア
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
        // ボタンが押された
        // タイマーをセット
        setDelay(getIniDelay());
        startTimer();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
        // ボタンがはなされた
        // タイマーを止める
        stopTimer();
    }

}
