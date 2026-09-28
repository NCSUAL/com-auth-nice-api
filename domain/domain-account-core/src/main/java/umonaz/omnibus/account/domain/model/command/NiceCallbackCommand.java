package umonaz.omnibus.account.domain.model.command;

import lombok.Builder;

@Builder
public record NiceCallbackCommand(String webTransactionId, String transactionId) {}
