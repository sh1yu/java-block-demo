package org.psy.practice.block_demo.entity;

public class BlockChain {
    private Block[] blocks;

    public BlockChain() {
        blocks = new Block[0];
    }

    public void addBlock(Block block) {
        Block[] newBlocks = new Block[blocks.length + 1];
        System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
        newBlocks[blocks.length] = block;
        blocks = newBlocks;
    }

    public Block getLatestBlock() {
        return blocks[blocks.length - 1];
    }

    public Block getBlock(int index) {
        return blocks[index];
    }

    public int getLength() {
        return blocks.length;
    }
}
