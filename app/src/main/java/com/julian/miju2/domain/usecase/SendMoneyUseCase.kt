package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import com.julian.miju2.domain.repository.AccountRepository
import com.julian.miju2.domain.repository.TransactionRepository

class SendMoneyUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(
        senderId: String,
        recipientId: String,
        amount: Double,
        concept: String,
        onResult: (Boolean, Int) -> Unit
    ) {
        accountRepository.getAccount(senderId) { senderAccount ->
            if (senderAccount == null) {
                onResult(false, R.string.send_money_error_connection)
                return@getAccount
            }

            if (amount > senderAccount.balance) {
                onResult(false, R.string.send_money_error_insufficient)
                return@getAccount
            }

            accountRepository.getAccount(recipientId) { recipientAccount ->
                if (recipientAccount == null) {
                    onResult(false, R.string.send_money_error_recipient_not_found)
                    return@getAccount
                }

                val newSenderBalance = senderAccount.balance - amount
                val newRecipientBalance = recipientAccount.balance + amount

                accountRepository.updateBalance(senderId, newSenderBalance) { senderUpdated ->
                    if (!senderUpdated) {
                        onResult(false, R.string.send_money_error_connection)
                        return@updateBalance
                    }

                    accountRepository.updateBalance(recipientId, newRecipientBalance) { recipientUpdated ->
                        if (!recipientUpdated) {
                            onResult(false, R.string.send_money_error_connection)
                            return@updateBalance
                        }

                        val tx = mapOf(
                            "from" to senderId,
                            "to" to recipientId,
                            "amount" to amount,
                            "date" to System.currentTimeMillis(),
                            "status" to "completed",
                            "concept" to concept
                        )
                        transactionRepository.createTransaction(tx) { created ->
                            if (created) {
                                onResult(true, R.string.send_money_success)
                            } else {
                                onResult(false, R.string.send_money_error_connection)
                            }
                        }
                    }
                }
            }
        }
    }
}
