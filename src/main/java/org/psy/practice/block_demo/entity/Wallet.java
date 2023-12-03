package org.psy.practice.block_demo.entity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    public PublicKey publicKey;
    public PrivateKey privateKey;

    public Map<String, TransactionOutput> UTXOS = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {

        try {

            // ECDSA:椭圆曲线数字签名算法
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public long getBalance() {
        long total = 0;
        for (TransactionOutput output : XXXChain.UTXOS.values()) {
            if (output.isMine(publicKey)) {
                UTXOS.put(output.transactionId, output);
                total += output.amount;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, long amount) {
        if (amount > getBalance()) {
            System.out.println("Insufficient balance");
            return null;
        }

        List<TransactionInput> inputs = new ArrayList<>();

        long total = 0;
        for (TransactionOutput output : UTXOS.values()) {
            total += output.amount;
            inputs.add(new TransactionInput(output.transactionId));
            if (total >= amount) {
                break;
            }
        }

        Transaction transaction = new Transaction(publicKey, recipient, amount, inputs);
        transaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOS.remove(input.transactionOutputId);
        }
        return transaction;

    }
}
