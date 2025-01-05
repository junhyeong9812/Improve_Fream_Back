package Fream_back.improve_Fream_Back.accessLog.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoIPService {

    private final DatabaseReader databaseReader;

    // GeoLite2-City.mmdb 초기화
    public GeoIPService() throws IOException {
        File database = new File(getClass().getClassLoader().getResource("GeoLite2-City.mmdb").getFile());
        this.databaseReader = new DatabaseReader.Builder(database).build();
    }

    // IP를 기반으로 위치 정보 조회
    public Location getLocation(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = databaseReader.city(ipAddress);

            Country country = response.getCountry();
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();

            return new Location(
                    country.getName(),
                    subdivision.getName(),
                    city.getName()
            );
        } catch (Exception e) {
            // IP 조회 실패 시 기본값 반환
            return new Location("Unknown", "Unknown", "Unknown");
        }
    }

    // 위치 정보 클래스
    public static class Location {
        private final String country;
        private final String region;
        private final String city;

        public Location(String country, String region, String city) {
            this.country = country;
            this.region = region;
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public String getRegion() {
            return region;
        }

        public String getCity() {
            return city;
        }
    }
}
