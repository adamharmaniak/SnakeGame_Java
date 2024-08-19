package snake_game;

import javax.swing.*;
import java.awt.*;

public class Snake extends JFrame{
    public Snake(){
        initUI();
    }

    public static void main(String[] args){
        EventQueue.invokeLater(()->{
            JFrame jf = new Snake();
            jf.setVisible(true);
        });
    }

    private void initUI(){
        add(new PlayingArea());

        setResizable(false);
        pack();
        setTitle("Snake Game");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}