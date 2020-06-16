package com.edu.distr.sys.client.menu;

import com.edu.distr.sys.client.CommandParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Menu {
  public CommandParser commandParser;

  public Menu(CommandParser commandParser) {
    this.commandParser = commandParser;
  }

  public void getMenu() {
    while (true) {
      System.out.println("Please, type your command: ");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      Scanner scanner = new Scanner(System.in);
      String command = scanner.nextLine();
      if (command.toUpperCase().equals("EXIT")) break;
      commandParser.callSafe(command);
    }


  }
}
