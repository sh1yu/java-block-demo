package org.psy.practice.block_demo.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class Block {
    public String hash;
    public String previousHash;
    // private String data;
    private Long timestamp;

    public int nonce; // 用于挖矿的变量
    public ArrayList<Transaction> transactions = new ArrayList<>();
    // merkleRoot充当data的作用（因为区块block本质就是个账本，用交易来充当数据最合理）
    public String merkleRoot;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String rawData = previousHash + merkleRoot + timestamp + nonce;
        String calculatedHash = DigestUtils.sha256Hex(rawData);
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("nonce: " + nonce);
    }

    public boolean addTransaction(Transaction transaction) {
        if (!previousHash.equals("0") && !transaction.processTransaction()) {
            System.out.println("Transaction failed to process");
            return false;
        }
        transactions.add(transaction);
        System.out.println("Transaction added to block");
        return true;

    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }

        List<String> treeLayer = previousTreeLayer;

        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add((DigestUtils.sha256Hex(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i))));
                count = treeLayer.size();
                previousTreeLayer = treeLayer;
            }
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

}
