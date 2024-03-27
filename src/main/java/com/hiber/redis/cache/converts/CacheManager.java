package com.hiber.redis.cache.converts;

import com.hiber.domain.City;
import com.hiber.domain.Country;
import com.hiber.domain.CountryLanguage;
import com.hiber.redis.cache.dto.CityCountryDTO;
import com.hiber.redis.cache.dto.LanguageDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CacheManager {
    public List<LanguageDTO> prepareCountryLanguage(Set<CountryLanguage> countryLanguages) {
        return countryLanguages.stream().map(cl -> {
            LanguageDTO languageDTO = new LanguageDTO();
            languageDTO.setId(cl.getId());
            languageDTO.setLanguage(cl.getLanguage());
            languageDTO.setOfficial(cl.getOfficial());
            languageDTO.setPercentage(cl.getPercentage());
            return languageDTO;
        }).collect(Collectors.toList());
    }

    public List<CountryLanguage> extractCountryLanguage(Set<LanguageDTO> languages) {
        return languages.stream().map(l -> {
            CountryLanguage countryLanguage = new CountryLanguage();
            countryLanguage.setId(l.getId());
            countryLanguage.setLanguage(l.getLanguage());
            countryLanguage.setOfficial(l.getOfficial());
            countryLanguage.setPercentage(l.getPercentage());
            return countryLanguage;
        }).collect(Collectors.toList());
    }

    public void prepareCity(CityCountryDTO res, City city) {
        res.setId(city.getId());
        res.setName(city.getName());
        res.setPopulation(city.getPopulation());
        res.setDistrict(city.getDistrict());
    }

    public void prepareCountry(CityCountryDTO res, Country country) {
        res.setAlternativeCountryCode(country.getAlternativeCode());
        res.setContinent(country.getContinent());
        res.setCountryCode(country.getCode());
        res.setCountryName(country.getName());
        res.setCountryPopulation(country.getPopulation());
        res.setCountryRegion(country.getRegion());
        res.setCountrySurfaceArea(country.getSurfaceArea());
        res.setIndepYear(res.getIndepYear());
        res.setPopulation(res.getPopulation());
        res.setLifeExpectancy(res.getLifeExpectancy());
        res.setGnp(res.getGnp());
        res.setGnpoId(res.getGnpoId());
        res.setLocalName(res.getLocalName());
        res.setGovernmentForm(res.getGovernmentForm());
        res.setHeadOfState(res.getHeadOfState());
    }

    public City extractCity(CityCountryDTO cityCountryDTO) {
        City city = new City();
        city.setId(cityCountryDTO.getId());
        city.setName(cityCountryDTO.getName());
        city.setPopulation(cityCountryDTO.getPopulation());
        city.setDistrict(cityCountryDTO.getDistrict());
        return city;
    }

    public Country extractCountry(CityCountryDTO cityCountryDTO) {
        Country country = new Country();
        country.setId(cityCountryDTO.getId());
        country.setCode(cityCountryDTO.getCountryCode());
        country.setAlternativeCode(cityCountryDTO.getAlternativeCountryCode());
        country.setName(cityCountryDTO.getName());
        country.setContinent(cityCountryDTO.getContinent());
        country.setRegion(cityCountryDTO.getCountryRegion());
        country.setSurfaceArea(cityCountryDTO.getCountrySurfaceArea());
        country.setIndepYear(cityCountryDTO.getIndepYear());
        country.setPopulation(cityCountryDTO.getPopulation());
        country.setLifeExpectancy(cityCountryDTO.getLifeExpectancy());
        country.setGnp(cityCountryDTO.getGnp());
        country.setGnpoId(cityCountryDTO.getGnpoId());
        country.setLocalName(cityCountryDTO.getLocalName());
        country.setGovernmentForm(cityCountryDTO.getGovernmentForm());
        country.setHeadOfState(cityCountryDTO.getHeadOfState());
        return country;
    }
}
