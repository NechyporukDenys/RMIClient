package com.edu.distr.sys.client;

import com.edu.distr.sys.client.utils.FileManager;
import com.edu.distr.sys.command.abstraction.IRemote;
import com.edu.distr.sys.command.abstraction.ICommand;
import com.edu.distr.sys.command.impl.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class RemoteRequestManager {
  private final IRemote engine;
  private final Logger logger;

  public RemoteRequestManager(IRemote engine) {
    this.engine = engine;
    this.logger = Logger.getGlobal();
  }

  private <T> T send(ICommand<T> task) throws RemoteException {
    Instant startTime = Instant.now();
    T response = this.engine.executeTask(task);
    Instant stopTime = Instant.now();
    long duration = Duration.between(startTime, stopTime).toMillis();
    logger.info("Execution time: " + duration + " ms.");

    return response;
  }

  public void ping() throws RemoteException {
    ICommand<Integer> task = new Ping();
    int response = this.send(task);
    logger.info("Server response to PING command: " + response);
  }

  public void echo(String text) throws RemoteException {
    ICommand<String> task = new Echo(text);
    String response = this.send(task);
    logger.info("Server response to ECHO command: " + response);
  }

  public void sort(String inputFilePath, long value) throws IOException {
    logger.info("Asking server to sort numbers in '" + inputFilePath + "' file...");
    byte[] input = FileManager.read(inputFilePath);
    ICommand<byte[]> task = new Search(input, value, inputFilePath);
    byte[] response = this.send(task);
    byte[] newArray = task.getArray();
    String indexes = new String(response);
    if (indexes.equals("")) indexes = "none";
    FileManager.write(newArray, inputFilePath);
    logger.info("Numbers are sorted and saved to '" + inputFilePath + "' file.");
    logger.info("Indexes of elements from file " + inputFilePath + " that have value " + value + ": " + indexes);
  }

  public void generate(String outputFilePath, long count, NumberType type, long min, long max) throws IOException {
    if (min > max) {
      throw new IllegalArgumentException("Cannot generate numbers in range from " + min + " to " + max + ". " +
              "Make sure min < max.");
    }

    ICommand<byte[]> task = new Generate(count, type, min, max);
    logger.info("Asking server to generate " + count + " " + type.name().toLowerCase() + " numbers " +
            "in range from " + min + " to " + max + "...");
    byte[] response = this.send(task);
    FileManager.write(response, outputFilePath);

    logger.info("Numbers are generated and saved to '" + outputFilePath + "'.");
  }
}
