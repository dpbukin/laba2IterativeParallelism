package org.example.Semaphore;

public class BarberShopMain {
    public static void main(String[] args) throws InterruptedException {
        BarberShop barberShop = new BarberShop();
        Barber barber = new Barber(barberShop);
        barber.start();

        for (int i = 1; i <= 5; i++) {
            Customer customer = new Customer(barberShop, i);
            customer.start();
        }

        for (int i = 6; i <= 40; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 11) {
                Thread.sleep(20000);
            }
//            if(i == 20){
//                Thread.sleep(10000);
//            }

            Customer customer = new Customer(barberShop, i);
            customer.start();
        }
    }
}
