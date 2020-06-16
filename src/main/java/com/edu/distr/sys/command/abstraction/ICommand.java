package com.edu.distr.sys.command.abstraction;

public interface ICommand<T> {
  T execute();
  byte[] getArray();
}
