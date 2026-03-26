package logprocessing;

import java.math.BigDecimal;
import java.util.*;

public class TransactionLogProcessor {

    enum TX_TYPE {
        WITHDRAW,
        DEPOSIT
    }
    public static void main(String[] args) {
        List<String> logs = Arrays.asList(
                "tx1001,alice,DEPOSIT,500",
                "tx1002,bob,DEPOSIT,300",
                "tx1003,alice,WITHDRAW,100",
                "tx1002,bob,DEPOSIT,300",
                "tx1004,charlie,WITHDRAW,50",
                "bad,line"
        );

        TransactionResult transactionResult = parsLogs(logs);

        System.out.println("Final Balance");
        for(Map.Entry<String,List<Transaction>> entry: transactionResult.getTransactions().entrySet()){
            List<Transaction> transactions = entry.getValue();
            BigDecimal amount =  BigDecimal.ZERO;
            for(Transaction transaction: transactions){
                if(TX_TYPE.WITHDRAW.name().equalsIgnoreCase(transaction.getDepartment())){
                    amount = amount.subtract(transaction.getAmount());
                } else {
                    amount = amount.add(transaction.getAmount());
                }

            }
            System.out.println(entry.getKey()+ " -> "+amount);
        }

        System.out.println("duplicate Tx");
        for(Transaction tx: transactionResult.getDuplicateTransactions()){
            System.out.println(tx.getTransactionId());
        }
        System.out.println("Invalid Tx");
        for (String invalidTxId : transactionResult.getInvalidTransactions()) {
            System.out.println(invalidTxId);

        }
    }

    public static TransactionResult parsLogs(List<String> logs) {
        TransactionResult  transactionResult = new TransactionResult();
        Set<String> txIds = new HashSet<>();
        for(String log : logs) {
            Optional<Transaction> transaction = normalize(log);
            if(transaction.isPresent()) {
                Transaction tx = transaction.get();
                if(txIds.contains(tx.getTransactionId())) {
                    transactionResult.getDuplicateTransactions().add(tx);
                } else {
                    txIds.add(tx.getTransactionId());
                    transactionResult.getTransactions().putIfAbsent(tx.getUserId(), new ArrayList<>());
                    transactionResult.getTransactions().get(tx.getUserId()).add(tx);
                }
            } else {
                transactionResult.getInvalidTransactions().add(log);
            }
        }
        return transactionResult;
    }

    public static  Optional<Transaction> normalize(String log) {

        if(log == null) {
            return Optional.empty();
        }
        String[] parts = log.split(",");
        if(parts.length != 4) {
            return Optional.empty();
        }
        if(!(TX_TYPE.WITHDRAW.name().equalsIgnoreCase(parts[2]) || TX_TYPE.DEPOSIT.name().equalsIgnoreCase(parts[2]))) {
            return Optional.empty();
        }
        return Optional.of(new Transaction(parts[0],parts[1],parts[2],new BigDecimal(parts[3])));
    }

}

class TransactionResult {
    private Map<String, List<Transaction>> transactions = new HashMap<>();
    private List<Transaction> duplicateTransactions = new ArrayList<>();
    private List<String> invalidTransactions = new ArrayList<>();

    public Map<String, List<Transaction>> getTransactions() {
        return transactions;
    }

    public void setTransactions(Map<String, List<Transaction>> transactions) {
        this.transactions = transactions;
    }

    public List<Transaction> getDuplicateTransactions() {
        return duplicateTransactions;
    }

    public void setDuplicateTransactions(List<Transaction> duplicateTransactions) {
        this.duplicateTransactions = duplicateTransactions;
    }

    public List<String> getInvalidTransactions() {
        return invalidTransactions;
    }

    public void setInvalidTransactions(List<String> invalidTransactions) {
        this.invalidTransactions = invalidTransactions;
    }
}
class Transaction {
    private String transactionId;
    private String userId;
    private String department;
    private BigDecimal amount;

    public Transaction(String transactionId, String userId, String department, BigDecimal amount) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.department = department;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDepartment() {
        return department;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
