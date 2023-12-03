package org.psy.practice.block_demo.utils;

import java.security.PrivateKey;
import java.security.Signature;

public class Util {

    public static byte[] applyECDSASig(PrivateKey privateKey, String data) {
        Signature dsa;
        // 提前声明变量，避免最后不能返回有效值
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = data.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
