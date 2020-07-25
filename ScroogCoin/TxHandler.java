import java.util.ArrayList;

public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    private UTXOPool utxoPool;
    public TxHandler(UTXOPool utxoPool) {

        this.utxoPool = new UTXOPool(utxoPool);
        // IMPLEMENT THIS
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    	
        byte[] hash = tx.getHash();
        double outputTotal=0, inputTotal=0;

        //(1)
        for(int i =0; i < tx.numInputs(); i++)
            if(! utxoPool.contains(new UTXO(hash,i)))
                return false;

        //(2)
        for(int i =0; i < tx.numInputs(); i++)
        {
        	UTXO temp = new UTXO(tx.getInput(i).prevTxHash, tx.getInput(i).outputIndex);
        	
            if(! Crypto.verifySignature(utxoPool.getTxOutput(temp).address ,temp.getTxHash(), tx.getInput(i).signature))
                return false;
        }
        
        //(3)
        for(UTXO u: utxoPool.getAllUTXO())
        	if(u.getTxHash().equals(tx.getHash()))
        		return false;
        //(4)
        for(int i =0; i < tx.numOutputs(); i++)
        {
        	double temp = tx.getOutput(i).value;
        	if(temp < 0 )
        		return false;
        	outputTotal += temp;
        }
        //(5)
        for(int i =0; i < tx.numOutputs(); i++)
        	inputTotal += tx.getOutput(i).value;
        
        if(outputTotal > inputTotal)
        	return false;
        
        
        return true;


        
        // IMPLEMENT THIS
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	ArrayList<Transaction> result = new ArrayList<Transaction>();
    	
    	for(Transaction tx : possibleTxs)
    	{
    		if(this.isValidTx(tx))
    		{
    			result.add(tx);
    	        for(int i =0; i < tx.numOutputs(); i++)
    	        {
    	        	UTXO temp = new UTXO(tx.getHash(), i);
    	        	utxoPool.addUTXO(temp, tx.getOutput(i));
    	        }
    		}
    	}
    	return (Transaction[])result.toArray();
        // IMPLEMENT THIS
    }

}
