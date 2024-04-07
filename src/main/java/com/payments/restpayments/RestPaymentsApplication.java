package com.payments.restpayments;

import com.payments.restpayments.data.load.CardFileData;
import com.payments.restpayments.data.load.RoleFileData;
import com.payments.restpayments.role.Administrator;
import com.payments.restpayments.role.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableWebMvc
public class RestPaymentsApplication {
	public static final String ANSI_RED = "\u001b[31m";
	public static final String ANSI_RESET = "\u001b[0m";
	public static List<Client> clients = new ArrayList<>(10);
	public static List<Administrator> admins = new ArrayList<>(2);
	static {
		RoleFileData roleFileData = new RoleFileData("D://programming//distributed-systems-and-parallel-computing//restful-payments//src//main//java//com//payments//restpayments//data//clientData.txt",
				"D://programming//distributed-systems-and-parallel-computing//restful-payments//src//main//java//com//payments//restpayments//data//adminData.txt");
		roleFileData.getAdminData();
		roleFileData.getUserData();

		CardFileData cardFileData = new CardFileData("D://programming//distributed-systems-and-parallel-computing//restful-payments//src//main//java//com//payments//restpayments//data//creditCardData.txt",
				"D://programming//distributed-systems-and-parallel-computing//restful-payments//src//main//java//com//payments//restpayments//data//paymentData.txt");
		cardFileData.loadCreditCards();
		cardFileData.loadPayments();
	}

	public static void main(String[] args) {
		SpringApplication.run(RestPaymentsApplication.class, args);
	}


}