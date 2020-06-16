package com.edu.distr.sys.command.impl;

import com.edu.distr.sys.command.abstraction.ICommand;

import java.io.Serializable;

public class Ping implements ICommand<Integer>, Serializable {
  @Override
  public Integer execute() {
    return 0;
  }

  @Override
  public byte[] getArray() {
    return new byte[0];
  }
}
