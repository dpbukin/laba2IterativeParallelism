package org.example.Semaphore;
public class Barber extends Thread {
    private BarberShop barberShop;
    public Barber(BarberShop barberShop) {
        this.barberShop = barberShop;
    }

    @Override
    public void run() {
        try {
            barberShop.barberWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
