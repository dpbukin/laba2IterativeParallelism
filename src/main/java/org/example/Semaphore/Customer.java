package org.example.Semaphore;

public class Customer extends Thread {
    private BarberShop barberShop;
    private int customerNumber;

    public Customer(BarberShop barberShop, int customerNumber) {
        this.barberShop = barberShop;
        this.customerNumber = customerNumber;
    }

    @Override
    public void run() {
        try {
            barberShop.customerArrives(customerNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

