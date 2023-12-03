package org.psy.practice.block_demo.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class XXXChain {
    public static List<Block> blockChain = new ArrayList<>();
    public static int difficulty = 3;

    public static Map<String, TransactionOutput> UTXOS = new HashMap<>();

    public static long minimunTransaction = 1;
    // public static Wallet walletA;
    // public static Wallet walletB;

    public static Transaction genesisTransaction;

    public boolean isChainValid() {
        Block curBlock;
        Block prevBlock;

        String target = new String(new char[difficulty]).replace('\0', '0');
        Map<String, TransactionOutput> tmpUTXOs = new HashMap<>();
        tmpUTXOs.put(genesisTransaction.outputs.get(0).transactionId, genesisTransaction.outputs.get(0));

        for (int i = 1; i < blockChain.size(); i++) {
            curBlock = blockChain.get(i);
            prevBlock = blockChain.get(i - 1);

            if (!curBlock.hash.equals(curBlock.calculateHash())) {
                System.out.println("block的hash值计算错误");
                return false;
            }

            if (!curBlock.previousHash.equals(prevBlock.calculateHash())) {
                System.out.println("区块之间的链接关系错误");
                return false;
            }

            if (!curBlock.hash.substring(0, difficulty).equals(target)) {
                System.out.println("区块的hash值难度不够");
                return false;
            }

            for (Transaction transaction : curBlock.transactions) {
                if (!transaction.verifySignature()) {
                    System.out.println("交易的签名验证失败");
                    return false;
                }

                if (transaction.getInputsAmount() != transaction.getOutputsAmount()) {
                    System.out.println("交易的 Inputs 与 Outputs 金额不等");
                    return false;
                }

                for (TransactionInput input : transaction.inputs) {
                    TransactionOutput output = tmpUTXOs.get(input.transactionOutputId);
                    if (output == null || output.amount != input.UTXO.amount) {
                        System.out.println("交易 Inputs 引用的UTXO不存在或金额错误");
                        return false;
                    }

                    tmpUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : transaction.outputs) {
                    tmpUTXOs.put(transaction.transactionId, output);
                }

                if (transaction.outputs.get(0).recipient != transaction.recipient) {
                    System.out.println("交易 Outputs 的接收方错误");
                    return false;
                }
                if (transaction.outputs.get(1).recipient != transaction.sender) {
                    System.out.println("交易的找零的交易输出没有发给发送者！");
                    return false;
                }
            }
        }

        System.out.println("区块链验证通过");
        return true;
    }

    public void addBlock(Block block) {
        block.mineBlock(difficulty);
        blockChain.add(block);
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(blockChain);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
