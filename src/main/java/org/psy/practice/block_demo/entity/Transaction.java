package org.psy.practice.block_demo.entity;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.psy.practice.block_demo.utils.Util;

public class Transaction {
    // 交易编号
    public String transactionId;
    // 交易序号
    private static int sequence = 0;

    // 发送方地址/public key
    public PublicKey sender;
    // 接收方地址/public key
    public PublicKey recipient;
    // 交易额
    public long amount;

    // 发送方签名
    private byte[] signature;

    // 本次交易所有交易输入
    public List<TransactionInput> inputs = new ArrayList<TransactionInput>();

    // 本次交易所有交易输出
    public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public Transaction(PublicKey sender, PublicKey recipient, long amount, List<TransactionInput> inputs) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return DigestUtils.sha256Hex(
                Base64.encodeBase64String(this.sender.getEncoded()) +
                        Base64.encodeBase64String(this.recipient.getEncoded()) +
                        +this.amount + sequence);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = Base64.encodeBase64String(this.sender.getEncoded())
                + Base64.encodeBase64String(this.recipient.getEncoded()) +
                this.amount;
        this.signature = Util.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = Base64.encodeBase64String(this.sender.getEncoded())
                + Base64.encodeBase64String(this.recipient.getEncoded()) + this.amount;

        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(this.sender);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(this.signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Invalid signature");
            return false;
        }

        for (TransactionInput input : this.inputs) {
            input.UTXO = XXXChain.UTXOS.get(input.transactionOutputId);
        }

        if (getInputsAmount() < XXXChain.minimunTransaction) {
            System.out.println("Transaction amount is too low");
            return false;
        }

        long leftover = getInputsAmount() - amount;
        if (leftover < 0) {
            System.out.println("Transaction overspent");
            return false;
        }

        transactionId = calculateHash();
        outputs.add(new TransactionOutput(recipient, amount, transactionId));
        if (leftover > 0) {
            outputs.add(new TransactionOutput(sender, leftover, transactionId));
        }

        for (TransactionOutput output : outputs) {
            XXXChain.UTXOS.put(output.transactionId, output);
        }

        for (TransactionInput input : inputs) {
            if (input.UTXO != null) {
                XXXChain.UTXOS.remove(input.UTXO.transactionId);
            }
        }

        return true;

    }

    public long getInputsAmount() {
        long amount = 0;
        for (TransactionInput input : this.inputs) {
            if (input.UTXO != null) {
                amount += input.UTXO.amount;
            }
        }
        return amount;
    }

    public long getOutputsAmount() {
        long sum = 0;
        for (TransactionOutput output : outputs) {
            sum += output.amount;
        }
        return sum;
    }
}
