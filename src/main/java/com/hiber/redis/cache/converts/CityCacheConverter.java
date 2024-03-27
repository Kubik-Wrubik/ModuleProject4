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

public class CityCacheConverter extends CacheConverter<CityCountryDTO, City> {
    public CityCacheConverter() {
        super(CityCountryDTO.class, City.class);
    }

    @Override
    public List<CityCountryDTO> prepareData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountryDTO res = new CityCountryDTO();
            cacheManager.prepareCity(res, city);

            Country country = city.getCountry();
            cacheManager.prepareCountry(res, country);

            Set<CountryLanguage> countryLanguages = country.getLanguages();
            List<LanguageDTO> languageDTOList = cacheManager.prepareCountryLanguage(countryLanguages);
            Set<LanguageDTO> languageDTOSet = new HashSet<>(languageDTOList);
            res.setLanguages(languageDTOSet);
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public List<City> extractData(List<CityCountryDTO> cityCountries) {
        return cityCountries.stream().map(cityCountryDTO -> {
            City city = cacheManager.extractCity(cityCountryDTO);

            Country country = cacheManager.extractCountry(cityCountryDTO);
            city.setCountry(country);

            Set<LanguageDTO> languageDTOSet = cityCountryDTO.getLanguages();
            List<CountryLanguage> countryLanguageList = cacheManager.extractCountryLanguage(languageDTOSet);
            Set<CountryLanguage> countryLanguageSet = new HashSet<>(countryLanguageList);

            country.setLanguages(countryLanguageSet);
            return city;
        }).collect(Collectors.toList());
    }
}
