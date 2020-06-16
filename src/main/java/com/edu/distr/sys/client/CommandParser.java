package com.edu.distr.sys.client;

import com.edu.distr.sys.client.utils.EnumManager;
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
    HELP,
    EXIT;
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
        return "\n1) PING \n" +
                "Write: ping";
      case ECHO:
        return "\n2) ECHO \n" +
                "Write: echo 'some text' \n" +
                "Scopes are not allowed";
      case GENERATE:
        return "\n3) GENERATE \n" +
                "Write: generate \"file_root\" <amount> <type> <min> <max>\n" +
                "You can use either whole and decimal numbers";
      case PROCESS:
        return "\n4) PROCESS  \n" +
                "Write: process \"file_root\" <value>";
      case HELP:
        return "\n5) HELP \n" +
                "Write: help <command>";
      case EXIT:
        return "\n6) EXIT \n" +
                "Write: exit";
      default:
        return "";
    }
  }
}
