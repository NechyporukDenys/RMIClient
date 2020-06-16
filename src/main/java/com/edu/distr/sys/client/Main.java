package com.edu.distr.sys.client;

import com.edu.distr.sys.client.logging.LogFormatter;
import com.edu.distr.sys.client.logging.LoggerHandler;
import com.edu.distr.sys.client.ui.MainFrame;
import com.edu.distr.sys.client.utilities.LogSecurityManager;
import com.edu.distr.sys.command.abstraction.IRemote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class Main {
  public static void main(String[] args) {
    String name = "com.edu.distr.sys.server.ServerEngine";
    int port = 2090;

    MainFrame mainFrame = new MainFrame();
    LogFormatter logFormatter = new LogFormatter();

    LoggerHandler handler = new LoggerHandler(mainFrame);
    handler.setFormatter(logFormatter);

    Logger logger = Logger.getGlobal();
    logger.addHandler(handler);

    mainFrame.setVisible(true);

    CommandParser parser = null;
    try {
      if(args.length == 2) {
        name = args[0];
        port = Integer.parseInt(args[1]);
      }

      Thread.sleep(500);
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new LogSecurityManager());
      }

      logger.info("Connecting to server [name = " + name + ", port = " + port + "]..." );
      Registry registry = LocateRegistry.getRegistry("localhost", port);
      IRemote engine = (IRemote) registry.lookup(name);

      RemoteRequestManager manager = new RemoteRequestManager(engine);
      parser = new CommandParser(manager);
    } catch (RemoteException | NotBoundException | InterruptedException e) {
      logger.severe("Could not connect to the server: " + e.getMessage());
      System.exit(1);
    }

    logger.info("Successfully connected to server.");
    logger.info("Here are some tips on how to use the program:" + parser.help());
    logger.info("Please, enter your commands.");
    mainFrame.setParser(parser);
  }
}
