package com.dhb.mr;

public class Test {

    public static void main(String[] args) {
        String str = "1363157991076 \t13926435656\t20-10-7A-28-CC-0A:CMCC\t120.196.100.99\t\t\t2\t4\t132\t1512\t200";
        System.out.println(str.split("\t").length);
        System.out.println(str.split("\t")[8]);
    }
}
