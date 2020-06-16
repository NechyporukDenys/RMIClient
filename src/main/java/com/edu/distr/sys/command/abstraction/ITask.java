package com.edu.distr.sys.command.abstraction;

public interface ITask<T> {
  T execute();
  byte[] getArray();
}
