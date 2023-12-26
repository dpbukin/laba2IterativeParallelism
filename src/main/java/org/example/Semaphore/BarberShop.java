package org.example.Semaphore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

public class BarberShop extends JFrame {
    private static final int MAX_CHAIRS = 5;
    private Semaphore barberSemaphore = new Semaphore(1);
    private Semaphore customerSemaphore = new Semaphore(1);
    private Semaphore accessSeatsSemaphore = new Semaphore(1);
    private int waitingCustomers = 0;
    private boolean barberSleeping = true;
    private Timer timer;
    private Color barberColor = Color.BLACK;
    private Color[] chairColors = new Color[MAX_CHAIRS];

    public BarberShop() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());
        BarberShop.BarberPanel barberPanel = new BarberShop.BarberPanel();
        add(barberPanel, BorderLayout.CENTER);
        setVisible(true);

        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateColors();
                repaint();
            }
        });
        timer.start();
    }

    private void updateColors() {
        if (barberSleeping) {
            barberColor = Color.BLACK;
        } else {
            barberColor = Color.BLUE;
        }

        for (int i = 0; i < MAX_CHAIRS; i++) {
            if (i < waitingCustomers) {
                chairColors[i] = Color.RED; // Красный цвет - стул занят
            } else {
                chairColors[i] = (i == waitingCustomers) ? Color.YELLOW : Color.WHITE; // Белый - стул пустой, желтый - клиент стрижется
            }
        }
    }

    public void customerArrives(int customerNumber) throws InterruptedException {
        accessSeatsSemaphore.acquire();
        if (waitingCustomers < MAX_CHAIRS) {
            waitingCustomers++;
            accessSeatsSemaphore.release();

            if (barberSleeping) {
                System.out.println("Клиент " + customerNumber + " пришел и разбудил парикмахера");
                barberSleeping = false;
            } else {
                System.out.println("Клиент " + customerNumber + " пришел");
            }

            customerSemaphore.release();
            System.out.println("Клиент " + customerNumber + " стрижется");
            barberSemaphore.acquire();



            System.out.println("Клиент " + customerNumber + " закончил стрижку и уходит");
            System.out.println();
        } else {
            accessSeatsSemaphore.release();
            System.out.println("Клиент " + customerNumber + " пришел, не нашел свободных мест и ушел");
        }
    }

    public void barberWork() throws InterruptedException {
        while (true) {
            if (waitingCustomers == 0) {
                barberSleeping = true;
                System.out.println("Парикмахер спит");
            }

            customerSemaphore.acquire(); //Занимем допуск
            accessSeatsSemaphore.acquire(); //Занимем допуск

            waitingCustomers--;


            accessSeatsSemaphore.release();//Освобождаем допуск
            Thread.sleep(3000);
            barberSemaphore.release(); //Освобождаем допуск барбера и стрижем

            System.out.println("Стрижка закончена");
        }
    }

    class BarberPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 380;
            int y = 150;

            g.setColor(barberColor);
            g.fillOval(x, y, 40, 40);

            for (int i = 0; i < MAX_CHAIRS; i++) {
                x = 50 + i * 150;
                y = 150;
                int[] xPoints = {x, x + 20, x - 20};
                int[] yPoints = {y, y + 40, y + 40};

                g.setColor(chairColors[i]);
                g.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }
}
