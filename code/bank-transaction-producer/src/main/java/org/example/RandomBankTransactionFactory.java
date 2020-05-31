package org.example;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomBankTransactionFactory {
    private static final List<String> DEFAULT_POSSIBLE_NAMES = Arrays.asList("John", "Sally", "Fred", "May", "Joe", "Jane");
    private static final int DEFAULT_MIN_AMOUNT = -100;
    private static final int DEFAULT_MAX_AMOUNT = 100;
    private static final Clock DEFAULT_CLOCK = Clock.systemDefaultZone();

    private final List<String> possibleNames;
    private final int minAmount;
    private final int maxAmount;
    private final Clock clock;
    private final Random random;

    public RandomBankTransactionFactory() {
        this(DEFAULT_POSSIBLE_NAMES, DEFAULT_MIN_AMOUNT, DEFAULT_MAX_AMOUNT, DEFAULT_CLOCK);
    }

    public RandomBankTransactionFactory(List<String> possibleNames, int minAmount, int maxAmount, Clock clock) {
        this.possibleNames = possibleNames;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.clock = clock;
        this.random = new Random();
    }

    public BankTransaction build() {
        String name = possibleNames.get(random.nextInt(possibleNames.size()));
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);
        LocalDateTime time = LocalDateTime.now(clock);
        BankTransaction transaction = new BankTransaction(name, amount, time);
        return transaction;
    }
}
