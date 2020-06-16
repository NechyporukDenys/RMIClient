package com.edu.distr.sys.command.impl;

import com.edu.distr.sys.command.abstraction.ITask;

import java.io.Serializable;

public class EchoTask implements ITask<String>, Serializable {
  private String text;

  public EchoTask(String text) {
    this.text = text;
  }

  @Override
  public String execute() {
    return text;
  }

  @Override
  public byte[] getArray() {
    return new byte[0];
  }
}
