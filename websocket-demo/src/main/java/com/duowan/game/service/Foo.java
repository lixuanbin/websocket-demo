package com.duowan.game.service;

import com.jcabi.aspects.Loggable;

public class Foo {
	@Loggable(Loggable.INFO)
	public double power(int x, int p) {
		return Math.pow(x, p);
	}
	public static void main(String[] args) {
		System.out.println(new Foo().power(2, 2));
	}
}