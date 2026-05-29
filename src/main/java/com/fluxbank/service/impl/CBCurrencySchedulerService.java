package com.fluxbank.service.impl;

import com.fluxbank.dto.CBCurrencyResponseDto;
import com.fluxbank.entity.CBCurrency;
import com.fluxbank.enums.Currency;
import com.fluxbank.repository.CBCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CBCurrencySchedulerService {
    private final RestTemplate restTemplate;
    private final CBCurrencyRepository cbCurrencyRepository;

    @Value("${cb.url}")
    private String url;

    @Scheduled(fixedRate = 600000)
    public void updateRate() {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("token", "afsgdhdgsdasfgdhjfkjthgf");
//        HttpEntity httpEntity = new HttpEntity(null, httpHeaders);
//        restTemplate.exchange(url, HttpMethod.GET, httpEntity, CBCurrencyResponseDto.class);

        ResponseEntity<CBCurrencyResponseDto> forEntity = restTemplate.getForEntity(url, CBCurrencyResponseDto.class);
        if (forEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
            CBCurrencyResponseDto body = forEntity.getBody();

            if (body == null) {
                return;
            }

            saveCurrency(Currency.USD, body.getUsd());
            saveCurrency(Currency.RUB, body.getRub());
            saveCurrency(Currency.EUR, body.getEur());
        }
    }

    private void saveCurrency(Currency currency, BigDecimal rate) {
        CBCurrency cbCurrency = cbCurrencyRepository.findByCode(currency).orElse(new CBCurrency());
        cbCurrency.setCode(currency);
        cbCurrency.setRate(rate);
        cbCurrencyRepository.save(cbCurrency);
    }
}
