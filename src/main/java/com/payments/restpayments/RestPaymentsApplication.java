package com.payments.restpayments;

import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import com.payments.restpayments.transaction.Account;
import com.payments.restpayments.transaction.CreditCard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class RestPaymentsApplication {
	public static List<Client> clients = new ArrayList<>(10);
	static {
		Client client = new Client(1, "Max", "Falk");
		client.addCreditCard(new CreditCard(1,"961", "visa",
				new Account(1, 1200, false)));
		clients.add(client);

		List<CreditCard> creditCards2 = new ArrayList<>();
		creditCards2.add(new CreditCard(2, "456", "mastercard",
				new Account(2, 100, false)));
		Client client2 = new Client(2, "John", "Doe", creditCards2);
		clients.add(client2);

		List<CreditCard> creditCards3 = new ArrayList<>();
		creditCards3.add(new CreditCard(3, "789", "amex",
				new Account(3, 2000, false )));
		Client client3 = new Client(3, "Alice", "Smith", creditCards3);
		//creditCards3.get(0).getPayments().add(creditCards2.get(0).processPayment(creditCards3.get(0), 50));
		clients.add(client3);

		// Додаємо ще 5 клієнтів
		List<CreditCard> creditCards4 = new ArrayList<>();
		creditCards4.add(new CreditCard(4, "135", "discover",
				new Account(4, 150000, true)));
		Client client4 = new Client(4, "Emily", "Johnson", creditCards4);
		clients.add(client4);

		List<CreditCard> creditCards5 = new ArrayList<>();
		creditCards5.add(new CreditCard(5, "246", "visa",
				new Account(5, 300, false)));
		Client client5 = new Client(5, "Michael", "Brown", creditCards5);
		clients.add(client5);

		List<CreditCard> creditCards6 = new ArrayList<>();
		creditCards6.add(new CreditCard(6, "357", "amex",
				new Account(6, 2500, false)));
		Client client6 = new Client(6, "Sophia", "Martinez", creditCards6);
		clients.add(client6);

		List<CreditCard> creditCards7 = new ArrayList<>();
		creditCards7.add(new CreditCard(7, "468", "mastercard",
				new Account(7, 1800, false)));
		Client client7 = new Client(7, "William", "Taylor", creditCards7);
		clients.add(client7);

		List<CreditCard> creditCards8 = new ArrayList<>();
		creditCards8.add(new CreditCard(8, "579", "discover",
				new Account(8, 2200, false)));
		Client client8 = new Client(8, "Olivia", "Anderson", creditCards8);
		clients.add(client8);

	}
	public static List<Administrator> admins = new ArrayList<>(3);
	static {
		admins.add(new Administrator(1, "amin1", "admin1"));
		admins.add(new Administrator(2, "amin2", "admin2"));
		admins.add(new Administrator(3, "amin3", "admin3"));
	}

	public static void main(String[] args) {
		SpringApplication.run(RestPaymentsApplication.class, args);

	}
}