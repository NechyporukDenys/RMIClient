package com.edu.distr.sys.command.impl;

import com.edu.distr.sys.command.abstraction.ICommand;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Search implements ICommand<byte[]>, Serializable {
  private byte[] byteArray;
  private long value;
  private String inputFiles;

  public Search(byte[] byteArray, long value, String inputFiles) {
    this.byteArray = byteArray;
    this.value = value;
    this.inputFiles = inputFiles;
  }

  private double[] getModernArray(double[] arr, long value) {
    double temporary;
    double[] array = arr;
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) {
        if (i == 0) {
          continue;
        } else {
          temporary = array[i];
          array[i] = array[i - 1];
          array[i - 1] = temporary;
        }

      }
    }
    return array;
  }

  private int countCopies(double[] array, long value) {
    int count = 0;
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) count++;
    }
    return count;
  }

  public double[] searchByValue(double[] array, long value) {
    double temporary;
    int countCopies;
    int count = 0;
    countCopies = countCopies(array, value);
    double[] arrayIndex = new double[countCopies];

    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) {
        if (i == 0) {
          arrayIndex[count] = 0;
          count++;
        } else {
          temporary = array[i];
          array[i] = array[i - 1];
          array[i - 1] = temporary;
          arrayIndex[count] = i;
          count++;
        }

      }
    }
    return arrayIndex;
  }


  @Override
  public byte[] execute() {
    String delimiter = " ";

    String content = new String(this.byteArray);
    String[] strArray = content.replace(System.lineSeparator(), delimiter).split(delimiter);
    double[] numberArray = Arrays.stream(strArray).mapToDouble(Double::parseDouble).toArray();

    double[] result = this.searchByValue(numberArray, value);

    DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    String strResult = Arrays.stream(result).mapToObj(decimalFormat::format).collect(Collectors.joining(delimiter));
    return strResult.getBytes();
  }

  @Override
  public byte[] getArray() {
    String delimiter = " ";

    String content = new String(this.byteArray);
    String[] strArray = content.replace(System.lineSeparator(), delimiter).split(delimiter);
    double[] numberArray = Arrays.stream(strArray).mapToDouble(Double::parseDouble).toArray();

    double[] result = this.getModernArray(numberArray, value);

    DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    String strResult = Arrays.stream(result).mapToObj(decimalFormat::format).collect(Collectors.joining(delimiter));
    return strResult.getBytes();
  }
}
