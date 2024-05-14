package com.hiber.redis.cache.converts;

import com.hiber.domain.City;
import com.hiber.domain.Country;
import com.hiber.domain.CountryLanguage;
import com.hiber.redis.cache.dto.CityCountryDTO;
import com.hiber.redis.cache.dto.LanguageDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CountryCacheConverter extends CacheConverter<CityCountryDTO, Country> {
    public CountryCacheConverter() {
        super(CityCountryDTO.class, Country.class);
    }

    @Override
    public List<CityCountryDTO> prepareData(List<Country> countries) {
        return countries.stream().map(country -> {
            CityCountryDTO res = new CityCountryDTO();
            City capital = country.getCapital();
            cacheManager.prepareCity(res, capital);

            cacheManager.prepareCountry(res, country);

            Set<CountryLanguage> countryLanguages = country.getLanguages();
            List<LanguageDTO> languageDTOList = cacheManager.prepareCountryLanguage(countryLanguages);
            Set<LanguageDTO> languageDTOSet = new HashSet<>(languageDTOList);
            res.setLanguages(languageDTOSet);
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Country> extractData(List<CityCountryDTO> cityCountries) {
        return cityCountries.stream().map(cityCountryDTO -> {

            City city = cacheManager.extractCity(cityCountryDTO);

            Country country = cacheManager.extractCountry(cityCountryDTO);
            city.setCountry(country);

            Set<LanguageDTO> languageDTOSet = cityCountryDTO.getLanguages();
            List<CountryLanguage> countryLanguageList = cacheManager.extractCountryLanguage(languageDTOSet);
            Set<CountryLanguage> countryLanguageSet = new HashSet<>(countryLanguageList);

            country.setLanguages(countryLanguageSet);
            return country;
        }).collect(Collectors.toList());
    }
}
