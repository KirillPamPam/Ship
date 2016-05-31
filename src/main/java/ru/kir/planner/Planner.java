package ru.kir.planner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill Zhitelev on 30.05.2016.
 */
public class Planner extends JPanel {
    private BufferedImage seaMap;
    private int xPosition, yPosition;
    private List<Coordinate> coordinates = new ArrayList<>();
    private JButton drawLineButton = new JButton("Нарисовать");
    private JButton clearButton = new JButton("Очистить");
    private JButton calculateButton = new JButton("Посчитать");
    private boolean drawLine, drawPoint = true;
    private JTextField containers = new JTextField();
    private JTextField daysNavigation = new JTextField();
    private JTextField shipSpeed = new JTextField();
    private JTextField shipCapacity = new JTextField();
    private JTextField containersSpeed = new JTextField();
    private JComboBox maps;
    private int km = 200, px = 80;
    private String[] items = {
            "Средиземное море",
            "Балтийское море",
    };

    public Planner() {
        init();

        click();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        setLayout(null);

        initImage("/Sredizemnoe.jpg");

        maps = new JComboBox(items);

        maps.setBounds(0, 0, 150, 25);

        shipSpeed.setBounds(25, 620, 80, 25);
        containers.setBounds(175, 620, 80, 25);
        daysNavigation.setBounds(320, 620, 80, 25);
        shipCapacity.setBounds(465, 620, 80, 25);
        containersSpeed.setBounds(640, 620, 80, 25);

        drawLineButton.setBounds(230, 655, 100, 25);
        clearButton.setBounds(335, 655, 80, 25);
        calculateButton.setBounds(420, 655, 100, 25);

        add(maps);
        add(calculateButton);
        add(containers);
        add(daysNavigation);
        add(shipCapacity);
        add(containersSpeed);
        add(shipSpeed);
        add(drawLineButton);
        add(clearButton);
    }

    private void click() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                xPosition = e.getX();
                yPosition = e.getY();
                int x = (getWidth() - seaMap.getWidth(null)) / 2;
                if(drawPoint  && yPosition >= 42 && yPosition <= seaMap.getHeight() + 35
                        && xPosition >= x
                        && xPosition <= seaMap.getWidth() + x -5) {
                    coordinates.add(new Coordinate(xPosition, yPosition));
                }
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkFieldsAndRoute())
                    calculateHours();
                else
                    JOptionPane.showMessageDialog(getRootPane(), "Заполните поля и нарисуйте маршрут", " Ошибка",
                            JOptionPane.ERROR_MESSAGE);

            }
        });

        drawLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawLine = true;
                drawPoint = false;

                calculateDistance();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAction();
            }
        });

        maps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(maps.getSelectedItem() == "Средиземное море") {
                    initImage("/Sredizemnoe.jpg");
                    km = 200;
                    px = 80;
                }
                else if(maps.getSelectedItem() == "Балтийское море") {
                    initImage("/Baltiyskoye.jpg");
                    km = 150;
                    px = 66;
                }
                clearAction();
            }
        });
    }

    private void clearAction() {
        setDefaultTextToFields();

        coordinates.clear();
        drawLine = false;
        drawPoint = true;
        repaint();
    }

    private void initImage(String path) {
        try {
            seaMap = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int x = (getWidth() - seaMap.getWidth(null)) / 2;
        g2.drawImage(seaMap, x, 40, this);
        g2.setStroke(new BasicStroke(2));

        drawTitles(g2);
        drawPoints(g2);
        drawLines(g2);
    }

    private void drawTitles(Graphics2D g2) {
        g2.setFont(new Font("arial", Font.BOLD, 13));
        drawString(g2, "Скорость корабля\n(км/ч)", 10, 585);
        drawString(g2, "Кол-во контейнеров", 150, 585);
        drawString(g2, "Период навигации\n(дни)", 300, 585);
        drawString(g2, "Грузоподъемность\n(контейнеры)", 450, 585);
        drawString(g2, "Скорость загрузки/разгрузки\n(контейнеры/ч)", 600, 585);
    }

    private void drawString(Graphics g2, String text, int x, int y) {
        for (String line : text.split("\n"))
            g2.drawString(line, x, y += g2.getFontMetrics().getHeight());
    }

    private void drawLines(Graphics2D g2) {
        if(drawLine) {
            for (int i = 0; i < coordinates.size()-1; i++) {
                g2.drawLine(coordinates.get(i).getX(), coordinates.get(i).getY(),
                        coordinates.get(i+1).getX(), coordinates.get(i+1).getY());
            }
            repaint();
        }
    }

    public void drawPoints(Graphics2D g2) {
            for (Coordinate coordinate : coordinates) {
                g2.fillOval(coordinate.getX() - 3, coordinate.getY() - 3, 8, 8);
            }
            repaint();
    }

    private double calculateDistance() {
        int xVector, yVector;
        double distance = 0;
        for (int i=0; i < coordinates.size()-1; i++) {
            xVector = coordinates.get(i+1).getX() - coordinates.get(i).getX();
            yVector = coordinates.get(i+1).getY() - coordinates.get(i).getY();

            distance += (km * Math.sqrt(Math.pow(xVector, 2) + Math.pow(yVector, 2))) / px;

        }

        return distance;
    }

    private void calculateHours() {
        int containersValue = Integer.parseInt(containers.getText());
        int daysNavigationValue = Integer.parseInt(daysNavigation.getText());
        double shipSpeedValue = Double.parseDouble(shipSpeed.getText());
        double shipCapacityValue = Double.parseDouble(shipCapacity.getText());
        double containersSpeedValue = Double.parseDouble(containersSpeed.getText());

        double what = containersValue / shipCapacityValue;
        int howManyTimes = (int) what == what ? (int) what : (int) what + 1;

        double hoursForWay = calculateDistance() / shipSpeedValue * 2;
        double hoursForLoad = containersValue / containersSpeedValue * 2;

        double result = howManyTimes * (hoursForWay + hoursForLoad);

        String message = "Дней потребуется: " + String.format("%.2f", result/24);

        JOptionPane.showMessageDialog(getRootPane(),
                checkTime(result, daysNavigationValue) ? "Транспортировка возможна. " + message :
                        "Транспортировка невозможна. " + message,
                "Результат", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean checkTime(double hours, double daysNavigation) {
        return (daysNavigation >= 365 || hours <= daysNavigation * 24);
    }

    private boolean checkFieldsAndRoute() {
        return !containers.getText().isEmpty()
                && !daysNavigation.getText().isEmpty()
                && !shipSpeed.getText().isEmpty()
                && !shipCapacity.getText().isEmpty()
                && !containersSpeed.getText().isEmpty()
                && drawLine;
    }

    private void setDefaultTextToFields() {
        containers.setText("");
        daysNavigation.setText("");
        shipSpeed.setText("");
        shipCapacity.setText("");
        containersSpeed.setText("");
    }

}
