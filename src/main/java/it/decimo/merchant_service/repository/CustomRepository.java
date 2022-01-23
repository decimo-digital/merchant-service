package it.decimo.merchant_service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.decimo.merchant_service.model.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CustomRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper mapper;

    /**
     * Recupera tutti i merchant con i relativi metadati di occupazione attuale
     */
    public List<Merchant> findAllMerchantsWithMetadata() {
        final var sql = "select *,(case when total_seats = 0 then 100 else ((occupied / total_seats::float) * 100)::numeric(3, 2) end) as occupancy_rate from (select merchant.id, merchant.owner, merchant.store_name, merchant.location, merchant.total_seats, merchant.description, (case when currently_prenotated is null then merchant.total_seats else merchant.total_seats - currently_prenotated end) as free_seats, (case when currently_prenotated is null then 0 else currently_prenotated end) as occupied from merchant left join (select merchant, count(prenotation.id) as currently_prenotated from prenotation right join merchant m on prenotation.merchant = m.id where prenotation.id in (select id from (select id, age(now(), to_timestamp(date_millis / 1000)) as age from prenotation) as prenotation_ages where extract(hour from prenotation_ages.age) <= 1 and extract(minute from prenotation_ages.age) <= 30) and prenotation_enabled = true group by merchant) as prenotations on id = prenotations.merchant) as data;";

        final var map = jdbcTemplate.queryForList(sql);

        final var merchants = new ArrayList<Merchant>();

        map.forEach(item -> {
            try {
                merchants.add(mapper.readValue(mapper.writeValueAsString(item), Merchant.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return merchants;
    }

    /**
     * Ritorna i dati del merchant specificato
     */
    public Merchant getMerchantData(int merchantId) {
        final var sql = String.format("select *,(case when total_seats = 0 then 100 else ((occupied / total_seats::float) * 100)::numeric(3, 2) end) as occupancy_rate from (select merchant.id, merchant.owner, merchant.store_name, merchant.location, merchant.total_seats, merchant.description, (case when currently_prenotated is null then merchant.total_seats else merchant.total_seats - currently_prenotated end) as free_seats, (case when currently_prenotated is null then 0 else currently_prenotated end) as occupied from merchant left join (select merchant, count(prenotation.id) as currently_prenotated from prenotation right join merchant m on prenotation.merchant = m.id where prenotation.id in (select id from (select id, age(now(), to_timestamp(date_millis / 1000)) as age from prenotation) as prenotation_ages where extract(hour from prenotation_ages.age) <= 1 and extract(minute from prenotation_ages.age) <= 30) and prenotation_enabled = true group by merchant) as prenotations on id = prenotations.merchant where merchant.id = %d) as data;", merchantId);

        final var map = jdbcTemplate.queryForList(sql);

        if (map.size() != 1) {
            log.error("Got an unexpected amount of merchants: {}", map.size());
            throw new RuntimeException("Got an unexpected amount of merchants");
        }

        try {
            return mapper.readValue(mapper.writeValueAsString(map.get(0)), Merchant.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
