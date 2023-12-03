package org.psy.practice.block_demo.entity;

import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class TransactionOutput {
    public String transactionId;

    public PublicKey recipient;
    public double amount;

    private String parentTransactionId;

    public TransactionOutput(PublicKey recipient, double amount, String parentTransactionId) {

        this.recipient = recipient;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;

        this.transactionId = DigestUtils.sha256Hex(
                Base64.encodeBase64String(this.recipient.getEncoded()) + this.amount + this.parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return this.recipient.equals(publicKey);
    }
}