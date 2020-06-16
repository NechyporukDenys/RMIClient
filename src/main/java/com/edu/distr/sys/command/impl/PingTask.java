package com.edu.distr.sys.command.impl;

import com.edu.distr.sys.command.abstraction.ITask;

import java.io.Serializable;

public class PingTask implements ITask<Integer>, Serializable {
  @Override
  public Integer execute() {
    return 0;
  }

  @Override
  public byte[] getArray() {
    return new byte[0];
  }
}
