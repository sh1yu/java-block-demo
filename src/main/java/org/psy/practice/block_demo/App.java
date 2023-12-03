package org.psy.practice.block_demo;

import java.security.Security;

import org.psy.practice.block_demo.entity.Block;
import org.psy.practice.block_demo.entity.Transaction;
import org.psy.practice.block_demo.entity.TransactionOutput;
import org.psy.practice.block_demo.entity.Wallet;
import org.psy.practice.block_demo.entity.XXXChain;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) {
        // BlockChain blockChain = new BlockChain();
        // blockChain.addBlock(new Block("Hello", null));
        // blockChain.addBlock(new Block("World",
        // blockChain.getLatestBlock().getHash()));
        // System.out.println("Blockchain length: " + blockChain.getLength());

        // for (int i = 0; i < blockChain.getLength(); i++) {
        // Block block = blockChain.getBlock(i);
        // try {
        // LOG.info("Block: {} {}", i, JSON.writeValueAsString(block));
        // } catch (JsonProcessingException e) {
        // LOG.error("json marshal block index {} error: {}", i, e.getMessage());
        // }
        // }

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        XXXChain xxxChain = new XXXChain();
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        Wallet coinBase = new Wallet();

        System.out.println("------ 第一次交易");
        XXXChain.genesisTransaction = new Transaction(coinBase.publicKey, walletA.publicKey, 100, null);
        XXXChain.genesisTransaction.generateSignature(coinBase.privateKey);
        XXXChain.genesisTransaction.transactionId = "0";
        XXXChain.genesisTransaction.outputs.add(
                new TransactionOutput(XXXChain.genesisTransaction.recipient, XXXChain.genesisTransaction.amount,
                        XXXChain.genesisTransaction.transactionId));
        XXXChain.UTXOS.put(XXXChain.genesisTransaction.outputs.get(0).transactionId,
                XXXChain.genesisTransaction.outputs.get(0));

        System.out.println("------ 第一个区块");
        Block genesis = new Block("0");
        genesis.addTransaction(XXXChain.genesisTransaction);
        xxxChain.addBlock(genesis);

        System.out.println("第二笔交易开始");
        System.out.println("钱包A的余额:" + walletA.getBalance());
        System.out.println("钱包B的余额:" + walletB.getBalance());
        Block block1 = new Block(genesis.hash);
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 20));
        xxxChain.addBlock(block1);
        System.out.println("第二笔交易结束");
        System.out.println("钱包A的余额:" + walletA.getBalance());
        System.out.println("钱包B的余额:" + walletB.getBalance());
    }
}
