package com.covenant.tribe.client.dadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReverseGeocodingData {

    @JsonProperty("postal_code")
    private String postalCode;

    private String country;

    @JsonProperty("country_iso_code")
    private String countryIsoCode;

    @JsonProperty("federal_district")
    private String federalDistrict;

    @JsonProperty("region_fias_id")
    private String regionFiasId;

    @JsonProperty("region_kladr_id")
    private String regionKladrId;

    @JsonProperty("region_iso_code")
    private String regionIsoCode;

    @JsonProperty("region_with_type")
    private String regionWithType;

    @JsonProperty("region_type")
    private String regionType;

    @JsonProperty("region_type_full")
    private String regionTypeFull;

    private String region;

    @JsonProperty("area_fias_id")
    private String areaFiasId;

    @JsonProperty("area_kladr_id")
    private String areaKladrId;

    @JsonProperty("area_with_type")
    private String areaWithType;

    @JsonProperty("area_type")
    private String areaType;

    @JsonProperty("area_type_full")
    private String areaTypeFull;

    private String area;

    @JsonProperty("city_fias_id")
    private String cityFiasId;

    @JsonProperty("city_kladr_id")
    private String cityKladrId;

    @JsonProperty("city_with_type")
    private String cityWithType;

    @JsonProperty("city_type")
    private String cityType;

    @JsonProperty("city_type_full")
    private String cityTypeFull;

    private String city;

    @JsonProperty("city_area")
    private String cityArea;

    @JsonProperty("city_district_fias_id")
    private String cityDistrictFiasId;

    @JsonProperty("city_district_kladr_id")
    private String cityDistrictKladrId;

    @JsonProperty("city_district_with_type")
    private String cityDistrictWithType;

    @JsonProperty("city_district_type")
    private String cityDistrictType;

    @JsonProperty("city_district_type_full")
    private String cityDistrictTypeFull;

    @JsonProperty("city_district")
    private String cityDistrict;

    @JsonProperty("settlement_fias_id")
    private String settlementFiasId;

    @JsonProperty("settlement_kladr_id")
    private String settlementKladrId;

    @JsonProperty("settlement_with_type")
    private String settlementWithType;

    @JsonProperty("settlement_type")
    private String settlementType;

    @JsonProperty("settlement_type_full")
    private String settlementTypeFull;

    private String settlement;

    @JsonProperty("street_fias_id")
    private String streetFiasId;

    @JsonProperty("street_kladr_id")
    private String streetKladrId;

    @JsonProperty("street_with_type")
    private String streetWithType;

    @JsonProperty("street_type")
    private String streetType;

    @JsonProperty("street_type_full")
    private String streetTypeFull;

    private String street;

    @JsonProperty("stead_fias_id")
    private String steadFiasId;

    @JsonProperty("stead_cadnum")
    private String steadCadnum;

    @JsonProperty("stead_type")
    private String steadType;

    @JsonProperty("stead_type_full")
    private String steadTypeFull;

    private String stead;

    @JsonProperty("house_fias_id")
    private String houseFiasId;

    @JsonProperty("house_kladr_id")
    private String houseKladrId;

    @JsonProperty("house_cadnum")
    private String houseCadnum;

    @JsonProperty("house_type")
    private String houseType;

    @JsonProperty("house_type_full")
    private String houseTypeFull;

    private String house;

    @JsonProperty("block_type")
    private String blockType;

    @JsonProperty("block_type_full")
    private String blockTypeFull;

    private String block;

    private String entrance;

    private String floor;

    @JsonProperty("flat_fias_id")
    private String flatFiasId;

    @JsonProperty("flat_cadnum")
    private String flatCadnum;

    @JsonProperty("flat_type")
    private String flatType;

    @JsonProperty("flat_type_full")
    private String flatTypeFull;

    private String flat;

    @JsonProperty("flat_area")
    private String flatArea;

    @JsonProperty("square_meter_price")
    private String squareMeterPricel;

    @JsonProperty("flat_price")
    private String flatPrice;

    @JsonProperty("room_fias_id")
    private String roomFiasId;

    @JsonProperty("room_cadnum")
    private String roomCadnum;

    @JsonProperty("room_type")
    private String roomType;

    @JsonProperty("room_type_full")
    private String roomTypeFull;

    private String room;

    @JsonProperty("postal_box")
    private String postalBox;

    @JsonProperty("fias_id")
    private String fiasId;

    @JsonProperty("fias_code")
    private String fiasCode;

    @JsonProperty("fias_level")
    private String fiasLevel;

    @JsonProperty("fias_actuality_state")
    private String fiasActualityState;

    @JsonProperty("kladr_id")
    private String kladrId;

    @JsonProperty("geoname_id")
    private String geonameId;

    @JsonProperty("capital_marker")
    private String capitalMarker;

    private String okato;

    private String oktmo;

    @JsonProperty("tax_office")
    private String taxOffice;

    @JsonProperty("tax_office_legal")
    private String taxOfficeLegal;

    private String timezone;

    @JsonProperty("geo_lat")
    private String geoLat;

    @JsonProperty("geo_lon")
    private String geoLon;

    @JsonProperty("beltway_hit")
    private String beltwayHit;

    @JsonProperty("beltway_distance")
    private String beltwayDistance;

    private String metro;

    private String divisions;

    @JsonProperty("qc_geo")
    private String qcGeo;

    @JsonProperty("qc_complete")
    private String qcComplete;

    @JsonProperty("qc_house")
    private String qcHouse;

    @JsonProperty("history_values")
    private String historyValues;

    @JsonProperty("unparsed_parts")
    private String unparsedParts;

    private String source;

    @JsonProperty("qc")
    private String qc;

}
