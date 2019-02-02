/**
 * 11.1.43 OrderColumn Annotation
 * Table 40
 */
package io.github.jeddict.jpa.ordercolumn;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class CreditCard {

    @Id
    private long ccNumber;

    @OneToMany
    @OrderColumn(name = "txTime")
    private List<CardTransaction> transactionHistory;

    public long getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(long ccNumber) {
        this.ccNumber = ccNumber;
    }

    public List<CardTransaction> getTransactionHistory() {
        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }
        return transactionHistory;
    }

    public void setTransactionHistory(List<CardTransaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public void addTransactionHistory(CardTransaction transactionHistory) {
        getTransactionHistory().add(transactionHistory);
    }

    public void removeTransactionHistory(CardTransaction transactionHistory) {
        getTransactionHistory().remove(transactionHistory);
    }

}