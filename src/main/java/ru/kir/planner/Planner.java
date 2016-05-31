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

    public Planner() {
        init();

        click();
    }

    private void init() {
        setLayout(null);
        try {
            seaMap = ImageIO.read(getClass().getResourceAsStream("/Sredizemnoe.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        shipSpeed.setBounds(25, 580, 80, 25);
        containers.setBounds(175, 580, 80, 25);
        daysNavigation.setBounds(320, 580, 80, 25);
        shipCapacity.setBounds(465, 580, 80, 25);
        containersSpeed.setBounds(640, 580, 80, 25);

        drawLineButton.setBounds(230, 615, 100, 25);
        clearButton.setBounds(335, 615, 80, 25);
        calculateButton.setBounds(420, 615, 100, 25);

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
                if(drawPoint && yPosition <= seaMap.getHeight()-5) {
                    coordinates.add(new Coordinate(xPosition, yPosition));
                }
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(checkFieldsAndRoute())
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
                setDefaultTextToFields();

                coordinates.clear();
                drawLine = false;
                drawPoint = true;
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(seaMap, 0, 0, this);
        g2.setStroke(new BasicStroke(2));

        drawTitles(g2);
        drawPoints(g2);
        drawLines(g2);
    }

    private void drawTitles(Graphics2D g2) {
        g2.setFont(new Font("arial", Font.BOLD, 13));
        drawString(g2, "Скорость корабля\n(км/ч)", 10, 545);
        drawString(g2, "Кол-во контейнеров", 150, 545);
        drawString(g2, "Период навигации\n(дни)", 300, 545);
        drawString(g2, "Грузоподъемность\n(контейнеры)", 450, 545);
        drawString(g2, "Скорость загрузки/разгрузки\n(контейнеры/ч)", 600, 545);
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

            distance += (200 * Math.sqrt(Math.pow(xVector, 2) + Math.pow(yVector, 2))) / 80;

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
