package org.infnet.auctionservice.enums;

public enum AuctionStatus {
    PENDING_REVIEW, //status inicial
    ACTIVE, //aprovado na analise
    EXPIRED, // expirationDate acabou sem lances
    SOLD, // expirationDate acabou com lances ou valor de arremate batido
    REMOVED, //anunciante removeu o anúncio
    CANCELED, // anúncio cancelado por penalidade
    REJECTED //rejeitado na analise

}