package com.duowan.game.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.duowan.game.annotation.ImportantLog;

public class BankTransactions {
	private Random randomGenerator = new Random();

	@ImportantLog(fields = { "1", "2" })
	public void login(String password, String accountId, String userName) {
		try {
			TimeUnit.MILLISECONDS.sleep(randomGenerator.nextInt(3000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("fake login, user: %s, account: %s", userName, accountId));
	}

	@ImportantLog(fields = { "0", "1" })
	public void withdraw(String accountId, Double moneyToRemove) {
		try {
			TimeUnit.MILLISECONDS.sleep(randomGenerator.nextInt(3000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("fake withdraw, account: %s, money: %.2f", accountId,
				moneyToRemove));
	}

	public static void main(String[] args) {
		BankTransactions bank = new BankTransactions();
		for (int i = 0; i < 1; i++) {
			String accountId = "account" + i;
			bank.login("password", accountId, "Ashley");
			bank.unimportantProcessing(accountId);
			bank.withdraw(accountId, Double.valueOf(i));
		}
		System.out.println("Transactions completed");
	}

	public void unimportantProcessing(String accountId) {
		try {
			TimeUnit.MILLISECONDS.sleep(randomGenerator.nextInt(3000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("nothing important, account: %s", accountId));
	}
}