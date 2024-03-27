package com.hiber.redis.cache.converts;

import com.hiber.domain.CountryLanguage;
import com.hiber.redis.cache.dto.LanguageDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryLanguageCacheConverter extends CacheConverter<LanguageDTO, CountryLanguage> {

    public CountryLanguageCacheConverter() {
        super(LanguageDTO.class, CountryLanguage.class);
    }

    @Override
    public List<LanguageDTO> prepareData(List<CountryLanguage> countryLanguages) {
        Set<CountryLanguage> countryLanguageSet = new HashSet<>(countryLanguages);
        return cacheManager.prepareCountryLanguage(countryLanguageSet);
    }

    @Override
    public List<CountryLanguage> extractData(List<LanguageDTO> languages) {
        Set<LanguageDTO> languageSet = new HashSet<>(languages);
        return cacheManager.extractCountryLanguage(languageSet);
    }
}
