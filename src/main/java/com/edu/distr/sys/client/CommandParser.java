package com.edu.distr.sys.client;

import com.edu.distr.sys.client.utilities.EnumManager;
import com.edu.distr.sys.command.impl.NumberType;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
  enum Command {
    PING,
    ECHO,
    GENERATE,
    PROCESS,
    HELP;
  }

  private static class Patterns {
    static final Pattern command = Pattern.compile("^([a-zA-z]+) ?.*");
    static final Pattern ping = Pattern.compile("(?i)^ping\\s*$");
    static final Pattern echo = Pattern.compile("(?i)^echo\\s+(.*)");
    static final Pattern generate = Pattern.compile("(?i)^generate\\s+\"(.+)\"\\s+(\\d+)\\s+([a-z]+)\\s+(-?\\d+)\\s+(-?\\d+)$");
    static final Pattern process = Pattern.compile("(?i)^process\\s+\"(.+)\"\\s(.+)$");
    static final Pattern plainHelp = Pattern.compile("(?i)^help\\s*$");
    static final Pattern help = Pattern.compile("(?i)^help\\s+([a-z]+)\\s*$");
  }

  private final RemoteRequestManager manager;
  private final Logger logger;

  public CommandParser(RemoteRequestManager manager) {
    this.manager = manager;
    this.logger = Logger.getGlobal();
  }

  public void callSafe(String execString) {
    try{
      call(execString);
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  public void call(String execString) throws IOException {
    Matcher commandMatcher = Patterns.command.matcher(execString);

    if(!commandMatcher.find()) {
      throw new IllegalArgumentException("Unknown command pattern");
    }

    String commandString = commandMatcher.group(1);
    Command command = (Command) EnumManager.parse(Command.class, commandString);

    switch (command) {
      case PING:
        if(!Patterns.ping.matcher(execString).find()) {
          throw new IllegalArgumentException("No additional characters are allowed in PING command");
        }
        this.manager.ping();
        break;
      case ECHO:
        Matcher echoMatcher = Patterns.echo.matcher(execString);
        if(!echoMatcher.find()) {
          throw new IllegalArgumentException("No text to send to server");
        }
        String text = echoMatcher.group(1);
        this.manager.echo(text);
        break;
      case GENERATE:
        Matcher generateMatcher = Patterns.generate.matcher(execString);
        if(!generateMatcher.find()) {
          throw new IllegalArgumentException("Incorrect GENERATE command format. " +
                  "Use 'help generate' to get instructions.");
        }
        String filePath = generateMatcher.group(1);
        long count = Long.parseLong(generateMatcher.group(2));
        NumberType type = (NumberType) EnumManager.parse(NumberType.class,  generateMatcher.group(3));
        long min = Long.parseLong(generateMatcher.group(4));
        long max = Long.parseLong(generateMatcher.group(5));

        this.manager.generate(filePath, count, type, min, max);
        break;
      case PROCESS:
        Matcher processMatcher = Patterns.process.matcher(execString);
        if(!processMatcher.find()) {
          throw new IllegalArgumentException("Incorrect PROCESS command format. " +
                  "Use 'help process' to get instructions.");
        }

        String inputFilePath = processMatcher.group(1);
        String strValue = processMatcher.group(2);
        long value = Long.parseLong(strValue);
        this.manager.sort(inputFilePath,  value);
        break;
      case HELP:
        Matcher plainHelpMatcher = Patterns.plainHelp.matcher(execString);
        StringBuilder helpStringBuilder = new StringBuilder("Executing help command.");
        if(plainHelpMatcher.find()) {
          helpStringBuilder.append(this.help());
        } else {
          Matcher helpMatcher = Patterns.help.matcher(execString);
          if(!helpMatcher.find()) {
            throw new IllegalArgumentException("Incorrect HELP command format");
          }

          String argString = helpMatcher.group(1);
          Command argCommand = (Command) EnumManager.parse(Command.class, argString);
          helpStringBuilder.append(this.help(argCommand));
        }
        logger.info(helpStringBuilder.toString());
    }
  }

  public String help() {
    StringBuilder builder = new StringBuilder();
    for (Command command : Command.values()) {
      builder.append(help(command));
    }
    return builder.toString();
  }

  public String help(Command command) {
    switch (command) {
      case PING:
        return "\n----------> PING <---------- \n" +
                "Sends an empty message to the server to test the connection.\n" +
                "Note: no other symbols are allowed after the command.";
      case ECHO:
        return "\n----------> ECHO <---------- \n" +
                "Sends selected text to the server and returns it back.\n" +
                "Example: echo 'Hello world!'";
      case GENERATE:
        return "\n----------> GENERATE <---------- \n" +
                "Sends request to server to generate numbers (whole or decimal) and saves the generated numbers to file.\n" +
                "Template: generate \"path/to/file\" <amount of numbers to generate> <type: decimal or whole> <min> <max>\n" +
                "Example: generate \"to_sort.txt\" 100 whole 0 1000\n" +
                "(This will generate 100 whole numbers from 0 to 1000 and put them into file named 'to_sort.txt')\n" +
                "Note: quotes (\") around file path are mandatory.";
      case PROCESS:
        return "\n----------> PROCESS <---------- \n" +
                "Reads the file, sends its content to server. Server sorts the numbers and sends the result, which is written to output file.\n" +
                "Template: process \"path/to/input/file\" value\n" +
                "Example: process \"to_sort.txt\" 10";

      case HELP:
        return "\n----------> HELP <----------\n" +
                "Print out helpful message on how to use the program.\n" +
                "Use 'help <command>' to get info about specific command";
      default:
        return "";
    }
  }
}
