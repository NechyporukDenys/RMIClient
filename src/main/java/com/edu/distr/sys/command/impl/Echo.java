package com.edu.distr.sys.command.impl;

import com.edu.distr.sys.command.abstraction.ICommand;

import java.io.Serializable;

public class Echo implements ICommand<String>, Serializable {
  private String text;

  public Echo(String text) {
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
