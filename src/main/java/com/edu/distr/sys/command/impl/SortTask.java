package com.edu.distr.sys.command.impl;

import com.edu.distr.sys.command.abstraction.ITask;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SortTask implements ITask<byte[]>, Serializable {
  byte[] byteArray;
  long value;
  String inputFiles;

  public SortTask(byte[] byteArray, long value, String inputFiles) {
    this.byteArray = byteArray;
    this.value = value;
    this.inputFiles = inputFiles;
  }
  private double[] test(double[] arr, long value) {
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
  private byte[] convertDoubleToByteArray(double number) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
    byteBuffer.putDouble(number);
    return byteBuffer.array();
  }

  private byte[] convertDoubleArrayToByteArray(double[] data) {
    if (data == null) return null;
    byte[] byts = new byte[data.length * Double.BYTES];
    for (int i = 0; i < data.length; i++)
      System.arraycopy(convertDoubleToByteArray(data[i]), 0, byts, i * Double.BYTES, Double.BYTES);
    return byts;
  }

  private int countCopies(double[] array, long value) {
    int count = 0;
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) count++;
    }
    return count;
  }

  public double[] sort(double[] array, long value) {
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
    byteArray = convertDoubleArrayToByteArray(array);
    return arrayIndex;
  }


  @Override
  public byte[] execute() {
    String delimiter = " ";

    String content = new String(this.byteArray);
    String[] strArray = content.replace(System.lineSeparator(), delimiter).split(delimiter);
    double[] numberArray = Arrays.stream(strArray).mapToDouble(Double::parseDouble).toArray();

    double[] result = this.sort(numberArray, value);

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

    double[] result = this.test(numberArray, value);
    System.out.println(Arrays.toString(result));

    DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    String strResult = Arrays.stream(result).mapToObj(decimalFormat::format).collect(Collectors.joining(delimiter));
    return strResult.getBytes();
  }
}
