package org.framegen.util;

import lombok.Getter;

import java.util.Scanner;

public class ScannerHolder {

    @Getter
    private static final Scanner scanner = new Scanner(System.in);

    private ScannerHolder() {}
}
