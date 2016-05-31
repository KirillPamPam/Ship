package ru.kir.planner;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * Created by Kirill Zhitelev on 30.05.2016.
 */
public class MainForm extends JFrame {
    private static final int WIDTH = 800, HEIGHT = 670;

    public MainForm() {
        initMetalLookAndFeel();
        init();

        add(new Planner());

        setVisible(true);
    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Planner");
        setLocationRelativeTo(null);
    }

    private static void initMetalLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainForm::new);
    }
}
